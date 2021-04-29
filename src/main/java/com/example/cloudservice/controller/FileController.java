package com.example.cloudservice.controller;

import com.example.cloudservice.service.FileService;
import com.example.cloudservice.transfer.file.RenameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

@RestController
@Validated
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/file")
    public ResponseEntity<Object> uploadFile(@NotBlank(message = "{upload.file.filename.is.blank}")
                                             @RequestParam String filename,
                                             @RequestPart MultipartFile file,
                                             Principal principal) {
        final String username = principal.getName();
        return fileService.uploadFile(filename, file, username);
    }

    @DeleteMapping(value = "/file")
    public ResponseEntity<Object> deleteFile(@NotBlank(message = "{delete.file.filename.is.blank}") String filename,
                                             Principal principal) {
        final String username = principal.getName();
        return fileService.deleteFile(filename, username);
    }

    @GetMapping(value = "/file")
    public ResponseEntity<Object> downloadFile(@NotBlank(message = "{download.file.filename.is.blank}") String filename,
                                               Principal principal) {
        final String username = principal.getName();
        return fileService.downloadFile(filename, username);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<Object> renameFile(@NotBlank(message = "{rename.file.filename.is.blank}")
                                             @RequestParam String filename,
                                             @Valid
                                             @RequestBody RenameRequest request,
                                             Principal principal) {
        final String username = principal.getName();
        final String newFilename = request.getFilename();
        return fileService.renameFile(filename, newFilename, username);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('SCOPE_files')") // проверка jwt
    public ResponseEntity<Object> listFiles(@RequestParam(required = false, defaultValue = "3") int limit,
                                            Principal principal) {
        final String username = principal.getName();
        return fileService.listFiles(limit, username);
    }
}
