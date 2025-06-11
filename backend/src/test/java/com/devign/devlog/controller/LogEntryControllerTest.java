package com.devign.devlog.controller;

import com.devign.devlog.dto.LogEntryDTO;
import com.devign.devlog.service.LogEntryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
@WebMvcTest(LogEntryController.class)
public class LogEntryControllerTest {
    @Mock
    private LogEntryService logEntryService;

    @InjectMocks
    private LogEntryController logEntryController;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateLogEntry() throws Exception {
        LogEntryDTO request = new LogEntryDTO();
        request.setTitle("Test Log");
        request.setContent("Some markdown");
        request.setTags(Set.of("dev", "thoughts"));

        LogEntryDTO response = new LogEntryDTO();
        response.setId(1L);
        response.setTitle("Test Log");
        response.setContent("Some markdown");
        response.setTags(Set.of("dev", "thoughts"));

        Mockito.when(logEntryService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Log"));
    }

    @Test
    public void testGetAllLogs() throws Exception {
        LogEntryDTO log = new LogEntryDTO();
        log.setId(1L);
        log.setTitle("Reflection");

        Mockito.when(logEntryService.getAll()).thenReturn(List.of(log));

        mockMvc.perform(get("/api/logs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Reflection"));
    }

    @Test
    public void testDeleteLog() throws Exception {
        mockMvc.perform(delete("/api/logs/1"))
            .andExpect(status().isNoContent());
    }
}