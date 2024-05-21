package ru.filini.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;
import ru.filini.cloudstorage.DTO.requests.EditFileNameRequest;
import ru.filini.cloudstorage.DTO.responses.FileResponse;
import ru.filini.cloudstorage.exceptions.InputDataException;
import ru.filini.cloudstorage.exceptions.UnauthorizedException;
import ru.filini.cloudstorage.model.StorageFile;
import ru.filini.cloudstorage.model.User;
import ru.filini.cloudstorage.repository.AuthRepository;
import ru.filini.cloudstorage.repository.FileRepository;
import ru.filini.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    private static final String BEARER_TOKEN = "Bearer validToken";
    private static final String TOKEN = "invalidToken";
    private static final String USERNAME = "testUser";
    private static final String FILENAME = "testFile.txt";
    private static final String NEW_FILENAME = "newFile.txt";
    private static final long FILE_SIZE = 1024L;
    private static final byte[] FILE_CONTENT = "file content".getBytes();
    private static final EditFileNameRequest EDIT_FILE_NAME_RQ = new EditFileNameRequest(NEW_FILENAME);
    private static final User USER = new User();

    @BeforeEach
    void setUp() throws IOException {
        when(authRepository.getUsernameByToken("validToken")).thenReturn(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(USER);
        when(multipartFile.getBytes()).thenReturn(FILE_CONTENT);
        when(multipartFile.getSize()).thenReturn(FILE_SIZE);
    }

    @Test
    void uploadFile() throws IOException {
        assertTrue(fileService.uploadFile(BEARER_TOKEN, FILENAME, multipartFile));
        verify(fileRepository, times(1)).save(any(StorageFile.class));
    }

    @Test
    void uploadFileUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.uploadFile(TOKEN, FILENAME, multipartFile));
    }

    @Test
    void uploadFileInputDataException() throws IOException {
        when(multipartFile.getBytes()).thenThrow(IOException.class);
        assertThrows(InputDataException.class, () -> fileService.uploadFile(BEARER_TOKEN, FILENAME, multipartFile));
    }

    @Test
    void deleteFile() {
        fileService.deleteFile(BEARER_TOKEN, FILENAME);
        verify(fileRepository, times(1)).deleteByUserAndFilename(USER, FILENAME);
    }

    @Test
    void deleteFileUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.deleteFile(TOKEN, FILENAME));
    }

    @Test
    void deleteFileInputDataException() {
        when(fileRepository.findByUserAndFilename(USER, FILENAME)).thenReturn(Optional.of(new StorageFile()));
        assertThrows(InputDataException.class, () -> fileService.deleteFile(BEARER_TOKEN, FILENAME));
    }

    @Test
    void downloadFile() {
        when(fileRepository.findByUserAndFilename(USER, FILENAME)).thenReturn(Optional.of(new StorageFile(FILENAME, LocalDateTime.now(), FILE_SIZE, FILE_CONTENT, USER)));
        assertArrayEquals(FILE_CONTENT, fileService.downloadFile(BEARER_TOKEN, FILENAME));
    }

    @Test
    void downloadFileUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.downloadFile(TOKEN, FILENAME));
    }

    @Test
    void downloadFileInputDataException() {
        when(fileRepository.findByUserAndFilename(USER, FILENAME)).thenReturn(Optional.empty());
        assertThrows(InputDataException.class, () -> fileService.downloadFile(BEARER_TOKEN, FILENAME));
    }

    @Test
    void editFileName() {
        fileService.editFileName(BEARER_TOKEN, FILENAME, EDIT_FILE_NAME_RQ);
        verify(fileRepository, times(1)).editFileNameByUser(USER, FILENAME, NEW_FILENAME);
    }

    @Test
    void editFileNameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.editFileName(TOKEN, FILENAME, EDIT_FILE_NAME_RQ));
    }

    @Test
    void editFileNameInputDataException() {
        when(fileRepository.findByUserAndFilename(USER, FILENAME)).thenReturn(Optional.of(new StorageFile()));
        assertThrows(InputDataException.class, () -> fileService.editFileName(BEARER_TOKEN, FILENAME, EDIT_FILE_NAME_RQ));
    }

    @Test
    void getAllFiles() {
        when(fileRepository.findAllByUser(USER)).thenReturn(List.of(new StorageFile(FILENAME, LocalDateTime.now(), FILE_SIZE, FILE_CONTENT, USER)));
        List<FileResponse> fileResponses = fileService.getAllFiles(BEARER_TOKEN, 1);
        assertEquals(1, fileResponses.size());
        assertEquals(FILENAME, fileResponses.get(0).getFilename());
        assertEquals(FILE_SIZE, fileResponses.get(0).getSize());
    }

    @Test
    void getAllFilesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.getAllFiles(TOKEN, 1));
    }

    @Test
    void isFileExists() {
        when(fileRepository.existsByUserAndFilename(USER, FILENAME)).thenReturn(true);
        assertTrue(fileService.isFileExists(BEARER_TOKEN, FILENAME));
    }

    @Test
    void isFileExistsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> fileService.isFileExists(TOKEN, FILENAME));
    }
}
