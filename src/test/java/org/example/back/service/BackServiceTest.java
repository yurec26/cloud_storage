package org.example.back.service;

import org.example.back.entity.*;
import org.example.back.repository.FileRepository;
import org.example.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private BackService backService;

    private User testUser;
    private Token testToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testUser");
        testUser.setPassword("password");
        testToken = new Token("sampleToken");
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        when(userRepository.findByLoginAndPassword("testUser", "password"))
                .thenReturn(Optional.of(testUser));

        Token result = backService.login(loginRequest);

        assertNotNull(result);
        assertEquals(testUser.getAuthToken().getAuthToken(), result.getAuthToken());
        verify(userRepository).save(testUser);
    }

    @Test
    void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        when(userRepository.findByLoginAndPassword("testUser", "wrongPassword"))
                .thenReturn(Optional.empty());

        Token result = backService.login(loginRequest);

        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testPostFile() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn("fileContent".getBytes());

        backService.postFile(mockFile, "testFile.txt");

        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        verify(fileRepository).save(fileCaptor.capture());
        assertEquals("testFile.txt", fileCaptor.getValue().getFilename());
        assertEquals(1024L, fileCaptor.getValue().getSize());
    }

    @Test
    void testGetFileSuccess() {
        File file = new File();
        file.setFilename("testFile.txt");
        file.setContent("fileContent".getBytes());

        when(fileRepository.findFileByFilename("testFile.txt"))
                .thenReturn(Optional.of(file));

        byte[] result = backService.getFile("testFile.txt");

        assertNotNull(result);
        assertEquals("fileContent", new String(result));
    }

    @Test
    void testGetFileNotFound() {
        when(fileRepository.findFileByFilename("nonExistentFile.txt"))
                .thenReturn(Optional.empty());

        byte[] result = backService.getFile("nonExistentFile.txt");

        assertNull(result);
    }

    @Test
    void testDeleteFileSuccess() {
        File fileToDelete = new File();
        fileToDelete.setFilename("testFile.txt");
        when(fileRepository.findFileByFilename("testFile.txt"))
                .thenReturn(Optional.of(fileToDelete));

        backService.deleteFile("testFile.txt");

        verify(fileRepository).delete(fileToDelete);
    }

    @Test
    void testDeleteFileNotFound() {
        when(fileRepository.findFileByFilename("nonExistentFile.txt"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            backService.deleteFile("nonExistentFile.txt");
        });

        assertEquals("Bad request", exception.getMessage());
    }
}