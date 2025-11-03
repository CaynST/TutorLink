package com.tutorlink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_login_me_flow() throws Exception {
        String email = "alice@example.com";
        String password = "P4ssword!";

        // Register
        String registerBody = "{" +
                "\"correo\":\"" + email + "\"," +
                "\"contrasena\":\"" + password + "\"," +
                "\"nombre\":\"Alice\"," +
                "\"apellidos\":\"Doe\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .header("Content-Type", "application/json")
                        .content(registerBody))
                .andExpect(status().isCreated());

        // Login
        String loginBody = "{" +
                "\"correo\":\"" + email + "\"," +
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
        String token = node.get("token").asText();
        assertThat(token).isNotBlank();

        // Me
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.authorities").isArray());
    }
}
