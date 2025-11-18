package com.tutorlink.service;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String fromAddress;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * RF-09: Notificar al alumno cuando su duda haya sido respondida (y aprobada/publicada).
     */
    public void enviarNotificacionRespuestaAprobada(Usuario alumno, Pregunta pregunta, Respuesta respuesta) {
        if (alumno == null || pregunta == null || respuesta == null) {
            log.warn("No se enviará correo: parámetros nulos alumno={}, pregunta={}, respuesta={}", alumno, pregunta, respuesta);
            return;
        }
        String destinatario = alumno.getCorreo();
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("No se enviará correo: el alumno {} no tiene correo registrado", alumno.getIdUsuario());
            return;
        }

        String asunto = "Tu duda ha sido respondida y publicada";
        String cuerpo = "Hola " + alumno.getNombre() + " " + alumno.getApellidos() + ",\n\n" +
                "Tu pregunta ha sido respondida y aprobada por tu tutor.\n" +
                "Título: " + pregunta.getTitulo() + "\n" +
                "Versión de respuesta: v" + respuesta.getVersionRespuesta() + "\n\n" +
                "Resumen de la respuesta: \n" +
                truncar(respuesta.getContenido(), 600) + "\n\n" +
                "Ingresa a Tutorlink para ver el detalle completo.\n\n" +
                "Este es un mensaje automático, por favor no responder.";

        enviarCorreoSeguro(destinatario, asunto, cuerpo);
    }

    /**
     * RF-10: Notificar a los tutores cuando la pregunta de sus tutorados haya sido respondida por el LLM.
     */
    public void enviarNotificacionNuevaRespuestaParaTutor(Usuario tutor, Usuario alumno, Pregunta pregunta, Respuesta respuesta) {
        if (tutor == null || pregunta == null || respuesta == null) {
            log.warn("No se enviará correo a tutor: parámetros nulos tutor={}, pregunta={}, respuesta={}", tutor, pregunta, respuesta);
            return;
        }
        String destinatario = tutor.getCorreo();
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("No se enviará correo: el tutor {} no tiene correo registrado", tutor.getIdUsuario());
            return;
        }

        String alumnoNombre = alumno != null ? (alumno.getNombre() + " " + alumno.getApellidos()) : "(Desconocido)";
        String asunto = "Nueva respuesta generada para tu tutorado";
        String cuerpo = "Hola " + tutor.getNombre() + " " + tutor.getApellidos() + ",\n\n" +
                "Se ha generado una nueva respuesta por el modelo para la pregunta de tu tutorado.\n" +
                "Alumno: " + alumnoNombre + "\n" +
                "Título de la pregunta: " + pregunta.getTitulo() + "\n" +
                "Versión de respuesta: v" + respuesta.getVersionRespuesta() + "\n\n" +
                "Resumen de la respuesta: \n" +
                truncar(respuesta.getContenido(), 600) + "\n\n" +
                "Ingresa a Tutorlink para revisarla y aprobarla o solicitar ajustes.\n\n" +
                "Este es un mensaje automático, por favor no responder.";

        enviarCorreoSeguro(destinatario, asunto, cuerpo);
    }

    private void enviarCorreoSeguro(String to, String subject, String body) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(resolveFromAddress());
            mensaje.setTo(to);
            mensaje.setSubject(subject);
            mensaje.setText(body);
            mailSender.send(mensaje);
            log.info("Correo enviado a {} con asunto '{}'", to, subject);
        } catch (MailException ex) {
            // No interrumpir el flujo principal; solo registrar el error
            log.error("Error enviando correo a {}: {}", to, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Fallo inesperado enviando correo a {}: {}", to, ex.getMessage(), ex);
        }
    }

    private String resolveFromAddress() {
        String from = (fromAddress != null && !fromAddress.isBlank()) ? fromAddress : mailUsername;
        if (from == null || from.isBlank()) {
            // No romper si no hay remitente; dejar en blanco permite a algunos servidores usar el por defecto.
            log.warn("Remitente de correo no configurado (app.mail.from o spring.mail.username)");
            return "";
        }
        return from;
    }

    private String truncar(String texto, int max) {
        if (texto == null) return "";
        if (texto.length() <= max) return texto;
        return texto.substring(0, Math.max(0, max - 3)) + "...";
    }
}
