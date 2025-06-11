package com.devign.devlog.service;

import com.devign.devlog.dto.LogEntryDTO;
import com.devign.devlog.entity.LogEntry;
import com.devign.devlog.entity.User;
import com.devign.devlog.repository.LogEntryRepository;
import com.devign.devlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogEntryServiceTest {

    @Mock
    private LogEntryRepository logRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private LogEntryService service;

    private final String username = "testUser";
    private final User testUser = new User();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser.setId(1L);
        testUser.setUsername(username);

        // Mock logged-in user
        var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));
    }

    @Test
    public void testCreateLogEntry() {
        LogEntryDTO dto = new LogEntryDTO();
        dto.setTitle("Test Entry");
        dto.setContent("Sample content");

        LogEntry saved = new LogEntry();
        saved.setId(1L);
        saved.setTitle(dto.getTitle());
        saved.setContent(dto.getContent());
        saved.setUser(testUser);

        when(logRepo.save(any(LogEntry.class))).thenReturn(saved);

        LogEntryDTO result = service.create(dto);

        assertEquals(dto.getTitle(), result.getTitle());
        assertNotNull(result.getId());
        verify(logRepo, times(1)).save(any());
    }

    @Test
    public void testGetAllLogs() {
        LogEntry entry = new LogEntry();
        entry.setId(1L);
        entry.setTitle("Journal");
        entry.setContent("Thoughts...");
        entry.setUser(testUser);

        when(logRepo.findByUserId(1L)).thenReturn(List.of(entry));

        List<LogEntryDTO> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Journal", result.get(0).getTitle());
    }

    @Test
    public void testDeleteLogEntryAuthorized() {
        LogEntry entry = new LogEntry();
        entry.setId(1L);
        entry.setUser(testUser);

        when(logRepo.findById(1L)).thenReturn(Optional.of(entry));

        assertDoesNotThrow(() -> service.deleteById(1L));
        verify(logRepo).deleteById(1L);
    }

    @Test
    public void testDeleteLogEntryUnauthorized() {
        LogEntry entry = new LogEntry();
        entry.setId(1L);
        User anotherUser = new User();
        anotherUser.setId(99L);
        entry.setUser(anotherUser);

        when(logRepo.findById(1L)).thenReturn(Optional.of(entry));

        assertThrows(RuntimeException.class, () -> service.deleteById(1L));
        verify(logRepo, never()).deleteById(any());
    }
}