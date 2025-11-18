package com.tutorlink.business;

import com.tutorlink.business.interfaces.AdminBusinessInterface;
import com.tutorlink.exception.ResourceNotFoundException;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.DetalleEstudianteRepository;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminBusiness implements AdminBusinessInterface {

    private final UsuarioRepository usuarioRepository;
    private final DetalleEstudianteRepository detalleEstudianteRepository;
    private final PreguntaRepository preguntaRepository;

    public AdminBusiness(UsuarioRepository usuarioRepository,
                         DetalleEstudianteRepository detalleEstudianteRepository,
                         PreguntaRepository preguntaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.detalleEstudianteRepository = detalleEstudianteRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public void aprobarRegistroUsuario(Long idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        // TODO: persistir bandera de validación de registro (campo no modelado). Por ahora es no-op.
    }

    @Override
    public void asignarTutorAEstudiante(Long idEstudiante, Long idTutor) {
        Usuario estudiante = usuarioRepository.findById(idEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        Usuario tutor = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        var detalle = detalleEstudianteRepository.findByIdUsuario(estudiante.getIdUsuario());
        if (detalle == null) {
            // Si no existe detalle, crearlo mínimamente
            var nuevo = new com.tutorlink.model.DetalleEstudiante(estudiante, tutor);
            detalleEstudianteRepository.save(nuevo);
        } else {
            detalle.setTutorAsignado(tutor);
            detalleEstudianteRepository.save(detalle);
        }
    }

    @Override
    public void cambiarEstadoUsuario(Long idUsuario, boolean activo) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        // TODO: Campo de estado/activo aún no modelado; no-op de persistencia.
    }

    @Override
    public void gestionarPreguntaEliminar(Long idPregunta) {
        Pregunta p = preguntaRepository.findById(idPregunta)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada"));
        preguntaRepository.delete(p);
    }
}
