package com.devign.devlog.service;

import com.devign.devlog.dto.SnippetDTO;
import com.devign.devlog.entity.*;
import com.devign.devlog.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepo;
    private final UserRepository userRepo;

    public SnippetService(SnippetRepository snippetRepo, UserRepository userRepo) {
        this.snippetRepo = snippetRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username).orElseThrow();
    }

    public SnippetDTO create(SnippetDTO dto) {
        User user = getCurrentUser();
        Snippet snippet = new Snippet();
        snippet.setTitle(dto.getTitle());
        snippet.setCode(dto.getCode());
        snippet.setDescription(dto.getDescription());
        snippet.setLanguage(dto.getLanguage());
        snippet.setUser(user);
        return toDTO(snippetRepo.save(snippet));
    }

    public List<SnippetDTO> getAll() {
        User user = getCurrentUser();
        return snippetRepo.findByUserId(user.getId()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        User user = getCurrentUser();
        Snippet snippet = snippetRepo.findById(id).orElseThrow();
        if (!snippet.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        snippetRepo.deleteById(id);
    }

    private SnippetDTO toDTO(Snippet snippet) {
        SnippetDTO dto = new SnippetDTO();
        dto.setId(snippet.getId());
        dto.setTitle(snippet.getTitle());
        dto.setLanguage(snippet.getLanguage());
        dto.setCode(snippet.getCode());
        dto.setDescription(snippet.getDescription());
        return dto;
    }
}