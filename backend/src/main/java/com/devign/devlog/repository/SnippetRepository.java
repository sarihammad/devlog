package com.devign.devlog.repository;

import com.devign.devlog.entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    List<Snippet> findByUserId(Long userId);
}