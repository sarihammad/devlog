package com.devign.devlog.integration;

import com.devign.devlog.dto.AuthRequest;
import com.devign.devlog.dto.LogEntryDTO;
// import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    // @Autowired
    // private ObjectMapper objectMapper;

    private String getBaseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    static String jwtToken;

    @Test
    @Order(1)
    public void registerUser() {
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("secret");

        ResponseEntity<String> response = rest.postForEntity(getBaseUrl("/auth/register"), req, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    public void loginUser() {
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("secret");

        ResponseEntity<String> response = rest.postForEntity(getBaseUrl("/auth/login"), req, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        jwtToken = response.getBody().substring(response.getBody().indexOf(":\"") + 2, response.getBody().length() - 2);
    }

    @Test
    @Order(3)
    public void createLogEntry() {
        LogEntryDTO dto = new LogEntryDTO();
        dto.setTitle("Integration Log");
        dto.setContent("End-to-end tested");
        dto.setTags(Set.of("int", "test"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LogEntryDTO> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<LogEntryDTO> response = rest.postForEntity(getBaseUrl("/api/logs"), entity, LogEntryDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEqualTo("Integration Log");
    }

    @Test
    @Order(4)
    public void getLogs() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.exchange(
            getBaseUrl("/api/logs"), HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Integration Log");
    }
}