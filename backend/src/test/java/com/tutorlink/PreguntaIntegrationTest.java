package com.tutorlink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class PreguntaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String correo;

    @BeforeEach
    void setupUserAndToken() throws Exception {
        correo = "bob@example.com";
        String password = "P4ssword!";

        String registerBody = "{" +
                "\"correo\":\"" + correo + "\"," +
                "\"contrasena\":\"" + password + "\"," +
                "\"nombre\":\"Bob\"," +
                "\"apellidos\":\"Builder\"" +
                "}";

        // Try register; if already exists, ignore failure and proceed to login
        try {
            mockMvc.perform(post("/api/auth/register")
                            .header("Content-Type", "application/json")
                            .content(registerBody))
                    .andExpect(status().isCreated());
        } catch (AssertionError ignored) {
            // user may already exist from another test; continue
        }

        String loginBody = "{" +
                "\"correo\":\"" + correo + "\"," +
                "\"contrasena\":\"" + password + "\"" +
                "}";

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .header("Content-Type", "application/json")
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(loginResponse);
        token = node.get("token").asText();
        assertThat(token).isNotBlank();
    }

    @Test
    void crear_y_listar_pregunta_general() throws Exception {
        String titulo = "¿Cómo funciona la herencia en Java?";
        String texto = "Necesito una explicación sencilla con ejemplos.";

        String createBody = "{" +
                "\"titulo\":\"" + titulo + "\"," +
                "\"texto\":\"" + texto + "\"," +
                "\"scope_tipo\":\"GENERAL\"" +
                "}";

        // Create question
        String createResp = mockMvc.perform(post("/api/preguntas")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_pregunta").exists())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.scope_tipo").value("GENERAL"))
                .andReturn().getResponse().getContentAsString();

        JsonNode created = objectMapper.readTree(createResp);
        long idPregunta = created.get("id_pregunta").asLong();
        assertThat(idPregunta).isPositive();

        // List questions filtered by scope GENERAL
        mockMvc.perform(get("/api/preguntas")
                        .header("Authorization", "Bearer " + token)
                        .param("scope_tipo", "GENERAL")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id_pregunta").exists());
    }
}
