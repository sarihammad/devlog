package com.devign.devlog.repository;

import com.devign.devlog.entity.LogEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByUserId(Long userId);
    
    Page<LogEntry> findByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT l FROM LogEntry l
        WHERE l.user.id = :userId
            AND (:tag IS NULL OR :tag MEMBER OF l.tags)
            AND (:keyword IS NULL OR l.title LIKE %:keyword% OR l.content LIKE %:keyword%)
            AND (:start IS NULL OR l.createdAt >= :start)
            AND (:end IS NULL OR l.createdAt <= :end)
    """)
    List<LogEntry> findByFilters(Long userId, String tag, String keyword, LocalDate start, LocalDate end);
}