package com.tutorlink.exception;

/**
 * Excepción para errores de comunicación con Ollama.
 */
public class OllamaCommunicationException extends RuntimeException {
    public OllamaCommunicationException(String message) {
        super(message);
    }
    public OllamaCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
