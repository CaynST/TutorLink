package com.tutorlink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.Rol;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.RespuestaRepository;
import com.tutorlink.repository.RolRepository;
import com.tutorlink.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class TutorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    private String estudianteEmail;
    private String estudiantePassword;
    private String tutorEmail;
    private String tutorPassword;

    @BeforeEach
    void setup() {
        estudianteEmail = "stud@example.com";
        estudiantePassword = "P4ssword!";
        tutorEmail = "tutor@example.com";
        tutorPassword = "T4ssword!";
    }

    @Test
    void seguridad_estudiante_no_accede_a_rutas_tutor() throws Exception {
        // Registrar estudiante
        String registerBody = "{" +
                "\"correo\":\"" + estudianteEmail + "\"," +
                "\"contrasena\":\"" + estudiantePassword + "\"," +
                "\"nombre\":\"Stu\"," +
                "\"apellidos\":\"Dent\"" +
                "}";
        try {
            mockMvc.perform(post("/api/auth/register").header("Content-Type", "application/json").content(registerBody))
                    .andExpect(status().isCreated());
        } catch (AssertionError ignored) {}

        // Login estudiante
        String loginBody = "{" +
                "\"correo\":\"" + estudianteEmail + "\"," +
                "\"contrasena\":\"" + estudiantePassword + "\"" +
                "}";
        String loginResp = mockMvc.perform(post("/api/auth/login").header("Content-Type", "application/json").content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String studentToken = objectMapper.readTree(loginResp).get("token").asText();

        // Intento de acceso a /api/tutor/** debe ser 403
        mockMvc.perform(get("/api/tutor/preguntas/pendientes").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void flujo_completo_pendientes_y_aprobacion() throws Exception {
        // a) Registrar y loguear estudiante
        String regEst = "{" +
                "\"correo\":\"" + estudianteEmail + "\"," +
                "\"contrasena\":\"" + estudiantePassword + "\"," +
                "\"nombre\":\"Stu\"," +
                "\"apellidos\":\"Dent\"" +
                "}";
        try {
            mockMvc.perform(post("/api/auth/register").header("Content-Type", "application/json").content(regEst))
                    .andExpect(status().isCreated());
        } catch (AssertionError ignored) {}
        String loginEst = "{" +
                "\"correo\":\"" + estudianteEmail + "\"," +
                "\"contrasena\":\"" + estudiantePassword + "\"" +
                "}";
        String estToken = objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login").header("Content-Type", "application/json").content(loginEst))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        ).get("token").asText();

        // b) Crear usuario TUTOR directamente y loguearlo
        Rol rolTutor = rolRepository.findByNombreRol("TUTOR").orElseThrow();
        Usuario tutor = usuarioRepository.findByCorreo(tutorEmail).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setCorreo(tutorEmail);
            u.setContrasena(passwordEncoder.encode(tutorPassword));
            u.setNombre("Tom");
            u.setApellidos("Tutor");
            u.setRol(rolTutor);
            return usuarioRepository.save(u);
        });
        String loginTutorBody = "{" +
                "\"correo\":\"" + tutorEmail + "\"," +
                "\"contrasena\":\"" + tutorPassword + "\"" +
                "}";
        String tutorToken = objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login").header("Content-Type", "application/json").content(loginTutorBody))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        ).get("token").asText();

        // c) Estudiante crea una pregunta PENDIENTE
        String titulo = "Tema de polimorfismo";
        String texto = "Explica con ejemplos simples";
        String createPregunta = "{" +
                "\"titulo\":\"" + titulo + "\"," +
                "\"texto\":\"" + texto + "\"," +
                "\"scope_tipo\":\"GENERAL\"" +
                "}";
        String respCreate = mockMvc.perform(post("/api/preguntas")
                        .header("Authorization", "Bearer " + estToken)
                        .header("Content-Type", "application/json")
                        .content(createPregunta))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode createdPreguntaNode = objectMapper.readTree(respCreate);
        long idPregunta = createdPreguntaNode.get("id_pregunta").asLong();
        assertThat(idPregunta).isPositive();

        // d) Tutor consulta pendientes y encuentra la pregunta
        String pendientes = mockMvc.perform(get("/api/tutor/preguntas/pendientes")
                        .header("Authorization", "Bearer " + tutorToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode pendientesJson = objectMapper.readTree(pendientes);
        assertThat(pendientesJson.get("content").toString()).contains(String.valueOf(idPregunta));

        // e) Crear una Respuesta asociada (como si fuese del LLM)
        Rol rolLlm = rolRepository.findByNombreRol("LLM").orElseThrow();
        Usuario autorLlm = usuarioRepository.findByCorreo("llm@example.com").orElseGet(() -> {
            Usuario u = new Usuario();
            u.setCorreo("llm@example.com");
            u.setContrasena(passwordEncoder.encode("dummyPass1!"));
            u.setNombre("LLM");
            u.setApellidos("Bot");
            u.setRol(rolLlm);
            return usuarioRepository.save(u);
        });
        Pregunta pregunta = preguntaRepository.findById((int) idPregunta).orElseThrow();
        Respuesta respuesta = new Respuesta();
        respuesta.setPregunta(pregunta);
        respuesta.setAutor(autorLlm);
        respuesta.setContenido("Respuesta generada por LLM");
        respuesta.setVersionRespuesta(1);
        respuesta.setEstadoRespuesta("PENDIENTE_REVISION");
        respuesta.setFechaRespuesta(Instant.now());
        respuesta = respuestaRepository.save(respuesta);

        // f) Tutor aprueba la respuesta
        mockMvc.perform(post("/api/tutor/respuestas/" + respuesta.getIdRespuesta() + "/aprobar")
                        .header("Authorization", "Bearer " + tutorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado_respuesta").value("PUBLICADA"));

        // Verificar en BD: pregunta.PUBLICADA y revisor/aprobador asignados al tutor
        Pregunta updatedPregunta = preguntaRepository.findById((int) idPregunta).orElseThrow();
        Respuesta updatedRespuesta = respuestaRepository.findById(respuesta.getIdRespuesta().intValue()).orElseThrow();
        assertThat(updatedPregunta.getEstado().name()).isEqualTo("PUBLICADA");
        assertThat(updatedPregunta.getRevisor()).isNotNull();
        assertThat(updatedRespuesta.getAprobador()).isNotNull();
        assertThat(updatedPregunta.getRevisor().getIdUsuario()).isEqualTo(tutor.getIdUsuario());
        assertThat(updatedRespuesta.getAprobador().getIdUsuario()).isEqualTo(tutor.getIdUsuario());
    }
}
