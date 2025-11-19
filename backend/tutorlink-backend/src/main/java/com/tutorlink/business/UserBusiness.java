package com.tutorlink.business;

import com.tutorlink.business.interfaces.UserBusinessInterface;
import com.tutorlink.exception.ResourceNotFoundException;
import com.tutorlink.exception.UnauthorizedException;
import com.tutorlink.exception.ValidationException;
import com.tutorlink.model.DetalleEstudiante;
import com.tutorlink.model.Rol;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserBusiness implements UserBusinessInterface {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final DetalleEstudianteRepository detalleEstudianteRepository;
    private final PlanEducativoRepository planEducativoRepository;
    private final ProgramaEducativoRepository programaEducativoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserBusiness(UsuarioRepository usuarioRepository,
                        RolRepository rolRepository,
                        DetalleEstudianteRepository detalleEstudianteRepository,
                        PlanEducativoRepository planEducativoRepository,
                        ProgramaEducativoRepository programaEducativoRepository,
                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.detalleEstudianteRepository = detalleEstudianteRepository;
        this.planEducativoRepository = planEducativoRepository;
        this.programaEducativoRepository = programaEducativoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Valida si el correo es único
    private void validarCorreoUnico(String correo) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ValidationException("El correo ya está registrado");
        }
    }

    // Valida si la matrícula es única
    private void validarMatriculaUnica(String matricula) {
        if (matricula != null && usuarioRepository.existsByMatricula(matricula)) {
            throw new ValidationException("La matrícula ya está registrada");
        }
    }

    private Rol obtenerRolObligatorio(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el rol: " + nombreRol));
    }

    @Override
    public Usuario registrarAlumno(String correo, String contrasenaHash, String nombre, String apellidos, String matricula, Long idPlanEducativo, Long idTutorAsignado) {
        validarCorreoUnico(correo);
        validarMatriculaUnica(matricula);

        var rolAlumno = obtenerRolObligatorio("ESTUDIANTE");
        var usuario = new Usuario(correo, contrasenaHash, nombre, apellidos, rolAlumno);
        usuario.setMatricula(matricula);

        var plan = planEducativoRepository.findById(idPlanEducativo)
            .orElseThrow(() -> new ResourceNotFoundException("Plan educativo no encontrado"));
        Usuario tutor = null;
        if (idTutorAsignado != null) {
            tutor = usuarioRepository.findById(idTutorAsignado)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor asignado no encontrado"));
        }

        usuario = usuarioRepository.save(usuario);

        // Crear detalle de estudiante
        var detalle = new DetalleEstudiante(usuario, tutor);
        detalle.setPlanEducativo(plan);
        detalleEstudianteRepository.save(detalle);

        return usuario;
    }

    @Override
    public Usuario registrarTutor(String correo, String contrasenaHash, String nombre, String apellidos, String telefono, Long idProgramaEducativo) {
        validarCorreoUnico(correo);
        var rolTutor = obtenerRolObligatorio("TUTOR");

        var usuario = new Usuario(correo, contrasenaHash, nombre, apellidos, rolTutor);
        usuario.setTelefono(telefono);

        // Validación ligera del programa educativo (si viene)
        if (idProgramaEducativo != null) {
            programaEducativoRepository.findById(idProgramaEducativo)
                    .orElseThrow(() -> new ResourceNotFoundException("Programa educativo no encontrado"));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario login(String correo, String contrasenaRaw) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        String stored = usuario.getContrasena();
        // Si stored parece ser un hash (comienza por $2a$ o $2b$), usar encoder.matches
        if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            if (!passwordEncoder.matches(contrasenaRaw, stored)) {
                throw new UnauthorizedException("Credenciales inválidas");
            }
        } else {
            // Fallback para cuentas antiguas (sin hash)
            if (!Objects.equals(stored, contrasenaRaw)) {
                throw new UnauthorizedException("Credenciales inválidas");
            }
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario consultarPerfil(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public Usuario actualizarFotoPerfil(Long idUsuario, String nuevaUrlFoto) {
        Usuario usuario = consultarPerfil(idUsuario);
        usuario.setFotoPerfilUrl(nuevaUrlFoto);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTutoradosDeTutor(Long idTutor) {
        Usuario tutor = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        // Recuperar detalles de estudiantes por tutor asignado
        var detalles = detalleEstudianteRepository.findByTutorAsignado(tutor);
        List<Usuario> resultado = new ArrayList<>();
        for (var d : detalles) {
            resultado.add(d.getUsuario());
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarTutorados(Long idTutor, String query) {
        String q = Optional.ofNullable(query).orElse("").toLowerCase();
        return obtenerTutoradosDeTutor(idTutor).stream()
                .filter(u ->
                        (u.getNombre() + " " + u.getApellidos()).toLowerCase().contains(q)
                                || (u.getMatricula() != null && u.getMatricula().toLowerCase().contains(q))
                                || (u.getCorreo() != null && u.getCorreo().toLowerCase().contains(q))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> filtrarTutorados(Long idTutor, Long idPlanEducativo, Integer semestre) {
        Usuario tutor = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        var detalles = detalleEstudianteRepository.findByTutorAsignado(tutor);

        return detalles.stream()
                .filter(d -> idPlanEducativo == null || (d.getPlanEducativo() != null && idPlanEducativo.equals(d.getPlanEducativo().getIdPlan())))
                .filter(d -> semestre == null || (d.getSemestre() != null && semestre.equals(d.getSemestre())))
                .map(DetalleEstudiante::getUsuario)
                .collect(Collectors.toList());
    }
}
