package com.devign.devlog.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class LogEntryDTO {
    private Long id;
    private String title;
    private String content;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}