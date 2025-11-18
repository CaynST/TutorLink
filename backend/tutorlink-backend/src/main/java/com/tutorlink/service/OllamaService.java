package com.tutorlink.service;

import com.tutorlink.exception.OllamaCommunicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Servicio para interactuar con Ollama (POST /api/generate).
 */
@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String defaultModelName;

    public OllamaService(
            RestTemplateBuilder builder,
            @Value("${tutorlink.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${tutorlink.ollama.model-name:llama3.1}") String defaultModelName,
            @Value("${tutorlink.ollama.timeout:30000}") long timeoutMs
    ) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
        this.baseUrl = baseUrl;
        this.defaultModelName = defaultModelName;
    }

    /**
     * Envía un prompt a Ollama y devuelve el texto generado.
     * @param prompt Texto del prompt
     * @param modelName Nombre del modelo (si es null o vacío, usa el configurado por defecto)
     */
    public String generarRespuesta(String prompt, String modelName) {
        String model = (modelName == null || modelName.isBlank()) ? defaultModelName : modelName;
        String url = this.baseUrl + "/api/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GenerateRequest req = new GenerateRequest();
        req.setModel(model);
        req.setPrompt(prompt);
        req.setStream(false); // respuesta no-stream para facilitar el parseo

        HttpEntity<GenerateRequest> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<GenerateResponse> resp = restTemplate.postForEntity(url, entity, GenerateResponse.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new OllamaCommunicationException("Respuesta inválida de Ollama: " + resp.getStatusCode());
            }
            String texto = resp.getBody().getResponse();
            if (texto == null || texto.isBlank()) {
                throw new OllamaCommunicationException("Ollama no devolvió contenido de respuesta");
            }
            return texto;
        } catch (RestClientResponseException e) {
            // Errores HTTP (4xx/5xx)
            String detalle = e.getResponseBodyAsString();
            throw new OllamaCommunicationException("Error de Ollama (" + e.getRawStatusCode() + "): " + detalle, e);
        } catch (ResourceAccessException e) {
            // Timeouts, fallos de conexión
            throw new OllamaCommunicationException("Error de conexión/timeout al contactar Ollama", e);
        } catch (RestClientException e) {
            // Otros errores del cliente
            throw new OllamaCommunicationException("Fallo al invocar Ollama: " + e.getMessage(), e);
        }
    }

    // --- Clases DTO internas para la API de Ollama ---

    public static class GenerateRequest {
        private String model;
        private String prompt;
        private Boolean stream;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        public Boolean getStream() { return stream; }
        public void setStream(Boolean stream) { this.stream = stream; }
    }

    public static class GenerateResponse {
        // Según la API, cuando stream=false la respuesta suele contener 'response' y otros campos.
        private String response;
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }
}
