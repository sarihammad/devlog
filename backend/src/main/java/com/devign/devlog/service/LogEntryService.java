package com.devign.devlog.service;

import com.devign.devlog.dto.LogEntryDTO;
import com.devign.devlog.entity.*;
import com.devign.devlog.repository.*;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogEntryService {

    private final LogEntryRepository logRepo;
    private final UserRepository userRepo;

    public LogEntryService(LogEntryRepository logRepo, UserRepository userRepo) {
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username).orElseThrow();
    }

    public LogEntryDTO create(LogEntryDTO dto) {
        User user = getCurrentUser();
        LogEntry entry = new LogEntry();
        entry.setTitle(dto.getTitle());
        entry.setContent(dto.getContent());
        entry.setTags(dto.getTags());
        entry.setUser(user);
        LogEntry saved = logRepo.save(entry);
        return toDTO(saved);
    }

    public List<LogEntryDTO> getAll() {
        User user = getCurrentUser();
        return logRepo.findByUserId(user.getId()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        User user = getCurrentUser();
        LogEntry entry = logRepo.findById(id).orElseThrow();
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        logRepo.deleteById(id);
    }

    public List<LogEntryDTO> search(String tag, String keyword, LocalDate start, LocalDate end) {
        Long userId = getCurrentUser().getId();
        List<LogEntry> logs = logRepo.findByFilters(userId, tag, keyword, start, end);
        return logs.stream().map(this::toDTO).toList();
    }

    public String exportToMarkdown() {
        Long userId = getCurrentUser().getId();
        List<LogEntry> logs = logRepo.findByUserId(userId);

        StringBuilder sb = new StringBuilder("# My Dev Logs\n\n");

        for (LogEntry l : logs) {
            sb.append("## ").append(l.getTitle()).append("\n");
            sb.append("_Created: ").append(l.getCreatedAt()).append("_\n\n");
            sb.append(l.getContent()).append("\n\n");
            if (l.getTags() != null && !l.getTags().isEmpty()) {
                sb.append("Tags: ").append(String.join(", ", l.getTags())).append("\n\n");
            }
            sb.append("---\n\n");
        }

        return sb.toString();
    }

    public byte[] exportToPdf() {
        List<LogEntry> logs = logRepo.findByUserId(getCurrentUser().getId());
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            PDFont font = PDType1Font.HELVETICA;
            float y = 750;

            content.beginText();
            content.setFont(font, 12);
            content.newLineAtOffset(50, y);

            for (LogEntry log : logs) {
                if (y < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    content.beginText();
                    content.setFont(font, 12);
                    y = 750;
                    content.newLineAtOffset(50, y);
                }

                content.showText("Title: " + log.getTitle());
                content.newLineAtOffset(0, -15);
                content.showText("Date: " + log.getCreatedAt());
                content.newLineAtOffset(0, -15);
                content.showText("Tags: " + String.join(", ", log.getTags()));
                content.newLineAtOffset(0, -15);
                content.showText(log.getContent().replace("\n", " "));
                content.newLineAtOffset(0, -25);
            }

            content.endText();
            content.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF export failed", e);
        }
    }

    public Page<LogEntryDTO> getPagedLogs(int page, int size, String[] sort) {
        Long userId = getCurrentUser().getId();

        Sort sortOrder = Sort.by(Arrays.stream(sort)
            .map(s -> {
                String[] parts = s.split(",");
                return new Sort.Order(Sort.Direction.fromString(parts[1].toUpperCase()), parts[0]);
            }).toList());

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return logRepo.findByUserId(userId, pageable)
            .map(this::toDTO);
    }

    private LogEntryDTO toDTO(LogEntry entry) {
        LogEntryDTO dto = new LogEntryDTO();
        dto.setId(entry.getId());
        dto.setTitle(entry.getTitle());
        dto.setContent(entry.getContent());
        dto.setTags(entry.getTags());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        return dto;
    }
}