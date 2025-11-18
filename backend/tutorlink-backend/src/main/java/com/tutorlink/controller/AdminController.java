package com.tutorlink.controller;

import com.tutorlink.model.dto.ApiResponse;
import com.tutorlink.service.interfaces.AdminServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones administrativas.
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','SUDO')")
public class AdminController {

    private final AdminServiceInterface adminService;

    public AdminController(AdminServiceInterface adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/usuarios/{id}/aprobar")
    public ResponseEntity<ApiResponse> aprobarUsuario(@PathVariable("id") Long idUsuario) {
        adminService.aprobarRegistroUsuario(idUsuario);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario aprobado"));
    }

    @PostMapping("/estudiantes/{idEst}/asignar-tutor/{idTutor}")
    public ResponseEntity<ApiResponse> asignarTutor(@PathVariable Long idEst,
                                                    @PathVariable Long idTutor) {
        adminService.asignarTutorAEstudiante(idEst, idTutor);
        return ResponseEntity.ok(new ApiResponse(true, "Tutor asignado"));
    }

    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(@PathVariable("id") Long idUsuario,
                                                     @RequestParam("activo") boolean activo) {
        adminService.cambiarEstadoUsuario(idUsuario, activo);
        return ResponseEntity.ok(new ApiResponse(true, "Estado actualizado"));
    }

    @DeleteMapping("/preguntas/{id}")
    public ResponseEntity<ApiResponse> eliminarPregunta(@PathVariable("id") Long idPregunta) {
        adminService.gestionarPreguntaEliminar(idPregunta);
        return ResponseEntity.ok(new ApiResponse(true, "Pregunta eliminada"));
    }
}
