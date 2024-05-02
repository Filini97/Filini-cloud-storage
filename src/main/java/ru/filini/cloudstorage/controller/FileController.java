package ru.filini.cloudstorage.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.filini.cloudstorage.DTO.requests.EditFileNameRequest;
import ru.filini.cloudstorage.DTO.responses.FileResponse;
import ru.filini.cloudstorage.service.FileService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class FileController {

    private FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") @NotBlank String filename,
            @RequestParam("file") @Valid MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded file is empty");
        }

        fileService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") @NotBlank String filename) {

        if (isFileExists(authToken, filename)) {
            return ResponseEntity.notFound().build();
        }

        fileService.deleteFile(authToken, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") @NotBlank String filename) {

        if (isFileExists(authToken, filename)) {
            return ResponseEntity.notFound().build();
        }

        byte[] file = fileService.downloadFile(authToken, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") @NotBlank String filename,
            @RequestBody @Valid EditFileNameRequest editFileNameRQ) {

        if (isFileExists(authToken, filename)) {
            return ResponseEntity.notFound().build();
        }

        fileService.editFileName(authToken, filename, editFileNameRQ);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<FileResponse> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        return fileService.getAllFiles(authToken, limit);
    }

    private boolean isFileExists(String authToken, String filename) {
        return !fileService.isFileExists(authToken, filename);
    }
}

