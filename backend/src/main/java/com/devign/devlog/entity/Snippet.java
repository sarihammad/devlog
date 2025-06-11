package com.devign.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String language;

    @Column(length = 10000)
    private String code;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    private User user;
}