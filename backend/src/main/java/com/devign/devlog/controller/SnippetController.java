package com.devign.devlog.controller;

import com.devign.devlog.dto.SnippetDTO;
import com.devign.devlog.service.SnippetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService service;

    public SnippetController(SnippetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SnippetDTO> create(@RequestBody SnippetDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SnippetDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}