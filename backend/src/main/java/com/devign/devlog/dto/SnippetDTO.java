package com.devign.devlog.dto;

import lombok.Data;

@Data
public class SnippetDTO {
    private Long id;
    private String title;
    private String language;
    private String code;
    private String description;
}