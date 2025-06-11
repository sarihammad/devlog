package com.devign.devlog.controller;

import com.devign.devlog.dto.LogEntryDTO;
import com.devign.devlog.service.LogEntryService;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService service;

    public LogEntryController(LogEntryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LogEntryDTO> create(@RequestBody LogEntryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<LogEntryDTO>> getAllLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        return ResponseEntity.ok(service.getPagedLogs(page, size, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogEntryDTO>> searchLogs(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(service.search(tag, keyword, startDate, endDate));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMarkdown() {
        String md = service.exportToMarkdown();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs.md")
            .contentType(MediaType.TEXT_MARKDOWN)
            .body(md.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportLogsAsPdf() {
        byte[] pdfBytes = service.exportToPdf();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}