package com.example.cloudservice.service;

import com.example.cloudservice.exception.FileException;
import com.example.cloudservice.model.FileEntity;
import com.example.cloudservice.model.User;
import com.example.cloudservice.repository.FileRepository;
import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.transfer.file.FileProjection;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final MessageSourceAccessor messages;

    public FileService(FileRepository fileRepository,
                       UserRepository userRepository,
                       MessageSourceAccessor messages) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.messages = messages;
    }

    @Transactional
    public ResponseEntity<Object> uploadFile(String filename, MultipartFile file, String username) {
        if (fileRepository.existsByFilenameAndUserUsername(filename, username)) {
            throw new FileException(messages.getMessage("file.upload.file.exists"));
        }
        try {
            final User user = userRepository.findById(username).orElseThrow(
                    () -> new UsernameNotFoundException(messages.getMessage("user.not.found")));
            final FileEntity fileEntity = createFileEntity(filename, user, file);
            fileRepository.saveAndFlush(fileEntity);

        } catch (IOException e) {
            throw new FileException(messages.getMessage("file.upload.problems"));
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Object> listFiles(int limit, String username) {
        final List<FileProjection> files = fileRepository.findFileByUserUsername(username);
        return ResponseEntity.ok().body(files);
    }

    @Transactional
    public ResponseEntity<Object> downloadFile(String filename, String username) {

        final FileEntity fileEntity = fileRepository.findFileByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> new FileException(messages.getMessage("file.download.problems")));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getFileType()));
        headers.setContentDisposition(ContentDisposition.builder(fileEntity.getFileType())
                .filename(fileEntity.getFilename()).build());
        headers.setContentLength(fileEntity.getData().length);

        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(fileEntity.getData()));
    }

    @Transactional
    public ResponseEntity<Object> deleteFile(String filename, String username) {
        if (fileRepository.existsByFilenameAndUserUsername(filename, username)) {
            fileRepository.removeByFilenameAndUserUsername(filename, username);
        } else throw new FileException(messages.getMessage("file.delete.problems"));
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Object> renameFile(String filename, String newFilename, String username) {
        if (fileRepository.existsByFilenameAndUserUsername(filename, username)) {
            fileRepository.renameFile(filename, newFilename, username);
        } else throw new FileException(messages.getMessage("file.rename.problems"));
        return ResponseEntity.ok().build();
    }

    FileEntity createFileEntity(String filename, User user, MultipartFile file) throws IOException {
        return new FileEntity()
                .setFilename(filename)
                .setFileType(file.getContentType())
                .setSize(file.getSize())
                .setData(file.getBytes())
                .setUser(user);
    }
}
