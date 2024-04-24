package ru.filini.cloudstorage.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.filini.cloudstorage.DTO.requests.EditFileNameRequest;
import ru.filini.cloudstorage.DTO.responses.FileResponse;
import ru.filini.cloudstorage.exceptions.InputDataException;
import ru.filini.cloudstorage.exceptions.UnauthorizedException;
import ru.filini.cloudstorage.repository.AuthRepository;
import ru.filini.cloudstorage.repository.FileRepository;
import ru.filini.cloudstorage.repository.UserRepository;
import ru.filini.cloudstorage.model.StorageFile;
import ru.filini.cloudstorage.model.User;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileService {

    private FileRepository fileRepository;
    private AuthRepository authRepository;
    private UserRepository userRepository;

    public boolean uploadFile(String authToken, String filename, MultipartFile file) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Upload file: Unauthorized");
            throw new UnauthorizedException("Upload file: Unauthorized");
        }

        try {
            fileRepository.save(new StorageFile(filename, LocalDateTime.now(), file.getSize(), file.getBytes(), user));
            log.info("Success upload file. User {}", user.getUsername());
            return true;
        } catch (IOException e) {
            log.error("Upload file: Input data exception");
            throw new InputDataException("Upload file: Input data exception");
        }
    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Delete file: Unauthorized");
            throw new UnauthorizedException("Delete file: Unauthorized");
        }

        fileRepository.deleteByUserAndFilename(user, filename);

        final Optional<StorageFile> tryingToGetDeletedFile = fileRepository.findByUserAndFilename(user, filename);
        if (tryingToGetDeletedFile.isPresent()) {
            log.error("Delete file: Input data exception");
            throw new InputDataException("Delete file: Input data exception");
        }
        log.info("Success delete file. User {}", user.getUsername());
    }

    public byte[] downloadFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Download file: Unauthorized");
            throw new UnauthorizedException("Download file: Unauthorized");
        }

        final Optional<StorageFile> file = fileRepository.findByUserAndFilename(user, filename);
        if (file.isEmpty()) {
            log.error("Download file: Input data exception");
            throw new InputDataException("Download file: Input data exception");
        }
        log.info("Success download file. User {}", user.getUsername());
        return file.get().getFileContent();
    }

    @Transactional
    public void editFileName(String authToken, String filename, EditFileNameRequest editFileNameRQ) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Edit file name: Unauthorized");
            throw new UnauthorizedException("Edit file name: Unauthorized");
        }

        fileRepository.editFileNameByUser(user, filename, editFileNameRQ.getFilename());

        final Optional<StorageFile> fileWithOldName = fileRepository.findByUserAndFilename(user, filename);
        if (fileWithOldName.isPresent()) {
            log.error("Edit file name: Input data exception");
            throw new InputDataException("Edit file name: Input data exception");
        }
        log.info("Success edit file name. User {}", user.getUsername());
    }

    public List<FileResponse> getAllFiles(String authToken, Integer limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Get all files: Unauthorized");
            throw new UnauthorizedException("Get all files: Unauthorized");
        }
        log.info("Success get all files. User {}", user.getUsername());
        return fileRepository.findAllByUser(user).stream()
                .map(o -> new FileResponse(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }

    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authRepository.getUsernameByToken(authTokenWithoutBearer);
            return userRepository.findByUsername(username);
        }
        return null;
    }

    public boolean isFileExists(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Check file existence: Unauthorized");
            throw new UnauthorizedException("Check file existence: Unauthorized");
        }

        return fileRepository.existsByUserAndFilename(user, filename);
    }
}