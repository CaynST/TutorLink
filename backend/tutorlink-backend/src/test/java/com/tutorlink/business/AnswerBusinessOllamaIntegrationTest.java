package com.tutorlink.business;

import com.tutorlink.exception.OllamaCommunicationException;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.DetalleEstudianteRepository;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.RespuestaRepository;
import com.tutorlink.repository.UsuarioRepository;
import com.tutorlink.service.NotificationService;
import com.tutorlink.service.OllamaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AnswerBusiness.class}, properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings({"null"})
class AnswerBusinessOllamaIntegrationTest {

    @Autowired
    private AnswerBusiness answerBusiness;

    // Mockeamos dependencias externas/infra
    @MockBean
    private OllamaService ollamaService;

    @MockBean
    private RespuestaRepository respuestaRepository;

    @MockBean
    private PreguntaRepository preguntaRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private DetalleEstudianteRepository detalleEstudianteRepository;

    @MockBean
    private NotificationService notificationService;

    @Test
    void deberiaGuardarRespuestaSiOllamaRespondeConExito() {
        // Arrange
        Long idPregunta = 101L;
        Long idUsuarioLLM = 202L;

        Usuario alumno = new Usuario();
        alumno.setIdUsuario(1L);
        alumno.setNombre("Alumno");
        alumno.setApellidos("Ejemplo");
        alumno.setCorreo("alumno@example.com");

        Pregunta pregunta = new Pregunta(alumno, "Título prueba", "Contenido de la pregunta", "GENERAL");
        pregunta.setIdPregunta(idPregunta);

        Usuario llm = new Usuario();
        llm.setIdUsuario(idUsuarioLLM);
        llm.setNombre("LLM");
        llm.setApellidos("Bot");
        llm.setCorreo("llm@example.com");

        when(preguntaRepository.findById(idPregunta)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(idUsuarioLLM)).thenReturn(Optional.of(llm));

        String respuestaSimulada = "Esta es una respuesta simulada por Ollama";
        when(ollamaService.generarRespuesta(any(String.class), any())).thenReturn(respuestaSimulada);

        // Capturamos Respuesta que se persiste
        ArgumentCaptor<Respuesta> respuestaCaptor = ArgumentCaptor.forClass(Respuesta.class);
        doAnswer(invocation -> {
            Respuesta r = invocation.getArgument(0);
            r.setIdRespuesta(999L);
            return r;
        }).when(respuestaRepository).save(respuestaCaptor.capture());

        // Act
        Respuesta guardada = answerBusiness.generarRespuestaConOllama(idPregunta, idUsuarioLLM);

        // Assert
        // 1) Verificar invocación a Ollama con prompt calculado
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(ollamaService, times(1)).generarRespuesta(promptCaptor.capture(), eq(null));
        String promptUsado = promptCaptor.getValue();
        assertThat(promptUsado)
                .contains("Eres un tutor académico")
                .contains("Título")
                .contains(pregunta.getTitulo())
                .contains(pregunta.getTexto());

        // 2) Verificar que se guardó una respuesta con datos correctos
        Respuesta persistida = respuestaCaptor.getValue();
        assertThat(persistida.getPregunta()).isEqualTo(pregunta);
        assertThat(persistida.getAutor()).isEqualTo(llm);
        assertThat(persistida.getContenido()).isEqualTo(respuestaSimulada);
        assertThat(persistida.getEstadoRespuesta()).isEqualTo("PENDIENTE_REVISION");

        // 3) Verificar el retorno
        assertThat(guardada).isNotNull();
        assertThat(guardada.getIdRespuesta()).isEqualTo(999L);

        // 4) Asegurar que se intentó notificar (puede no enviarse si no hay tutor asignado)
        // No exigimos que se llame obligatoriamente porque depende del DetalleEstudianteRepository.
        // Pero sí verificamos que no se lanzaron excepciones y el flujo llegó hasta el 'save'.
    verify(respuestaRepository, times(1)).save(Mockito.<Respuesta>any());
    }

    @Test
    void deberiaPropagarExcepcionSiOllamaFalla() {
        // Arrange
        Long idPregunta = 301L;
        Long idUsuarioLLM = 401L;

        Usuario alumno = new Usuario();
        alumno.setIdUsuario(2L);
        alumno.setNombre("Estu");
        alumno.setApellidos("Diante");
        alumno.setCorreo("estu@example.com");

        Pregunta pregunta = new Pregunta(alumno, "Título error", "Pregunta que falla", "GENERAL");
        pregunta.setIdPregunta(idPregunta);

        Usuario llm = new Usuario();
        llm.setIdUsuario(idUsuarioLLM);
        llm.setNombre("LLM");
        llm.setApellidos("Bot");
        llm.setCorreo("llm@example.com");

        when(preguntaRepository.findById(idPregunta)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(idUsuarioLLM)).thenReturn(Optional.of(llm));

        when(ollamaService.generarRespuesta(any(String.class), any()))
                .thenThrow(new OllamaCommunicationException("Fallo de comunicación con Ollama"));

        // Act + Assert
        assertThrows(OllamaCommunicationException.class,
                () -> answerBusiness.generarRespuestaConOllama(idPregunta, idUsuarioLLM));

        // Debe NO intentar guardar si falla la generación
    verify(respuestaRepository, Mockito.never()).save(Mockito.<Respuesta>any());
    }
}
