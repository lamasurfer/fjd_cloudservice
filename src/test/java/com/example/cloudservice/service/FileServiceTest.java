package com.example.cloudservice.service;

import com.example.cloudservice.exception.FileException;
import com.example.cloudservice.model.FileEntity;
import com.example.cloudservice.model.User;
import com.example.cloudservice.repository.FileRepository;
import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.transfer.file.FileProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    private final String username = "user";
    private final User user = new User().setUsername(username);
    private final String filename = "file.test";
    private final String fileType = "test/content";
    private final byte[] data = new byte[]{1, 2, 3};
    private final MockMultipartFile file = new MockMultipartFile(filename, filename, fileType, data);

    @Mock
    private FileRepository fileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageSourceAccessor messages;

    @InjectMocks
    private FileService fileService;

    @Test
    void test_createFileEntity_expectedBehaviour() throws IOException {
        final FileEntity fileEntity = fileService.createFileEntity(filename, user, file);

        assertNotNull(fileEntity);
        assertEquals(filename, fileEntity.getFilename());
        assertEquals(fileType, fileEntity.getFileType());
        assertEquals(data, fileEntity.getData());
        assertEquals(data.length, fileEntity.getSize());
        assertEquals(user, fileEntity.getUser());
    }

    @Test
    void test_uploadFile_expectedBehaviour() throws IOException {
        final FileEntity fileEntity = fileService.createFileEntity(filename, user, file);
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(false);
        when(userRepository.getOne(username)).thenReturn(user);

        final ResponseEntity<Object> expected = ResponseEntity.ok().build();
        final ResponseEntity<Object> actual = fileService.uploadFile(filename, file, username);

        assertEquals(expected, actual);
        verify(fileRepository).existsByFilenameAndUserUsername(filename, username);
        verify(fileRepository).saveAndFlush(fileEntity);
        verify(userRepository).getOne(username);
    }

    @Test
    void test_uploadFile_fileAlreadyExists_throwsException() {
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(true);

        assertThrows(FileException.class, () -> fileService.uploadFile(filename, file, username));
    }

    @Test
    void test_list_returnsListOfFileProjections() {
        final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        final FileProjection projection1 = factory.createProjection(FileProjection.class);
        projection1.setFilename("test1.file");
        projection1.setSize(7000);
        final FileProjection projection2 = factory.createProjection(FileProjection.class);
        projection2.setFilename("test2.file");
        projection2.setSize(9000);
        final List<FileProjection> fileProjections = List.of(projection1, projection2);
        when(fileRepository.findFileByUserUsername(username)).thenReturn(fileProjections);

        final ResponseEntity<Object> expected = ResponseEntity.ok().body(fileProjections);

        assertEquals(expected, fileService.listFiles(2, username));
        verify(fileRepository).findFileByUserUsername(username);
    }

    @Test
    void test_downloadFile_expectedBehaviour() throws IOException {
        final FileEntity fileEntity = fileService.createFileEntity(filename, user, file);
        when(fileRepository.findFileByFilenameAndUserUsername(filename, username)).thenReturn(Optional.of(fileEntity));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getFileType()));
        headers.setContentDisposition(ContentDisposition.builder(fileEntity.getFileType())
                .filename(fileEntity.getFilename()).build());
        headers.setContentLength(fileEntity.getData().length);

        final ResponseEntity<Object> expected = ResponseEntity
                .ok()
                .headers(headers)
                .body(new ByteArrayResource(fileEntity.getData()));

        assertEquals(expected, fileService.downloadFile(filename, username));
        verify(fileRepository).findFileByFilenameAndUserUsername(filename, username);
    }

    @Test
    void test_downloadFile_ifFileIsNotPresent_throwsException() {
        when(fileRepository.findFileByFilenameAndUserUsername(filename, username)).thenReturn(Optional.empty());

        assertThrows(FileException.class, () -> fileService.downloadFile(filename, username));
    }

    @Test
    void test_deleteFile_expectedBehaviour() {
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(true);
        final ResponseEntity<Object> expected = ResponseEntity.ok().build();

        assertEquals(expected, fileService.deleteFile(filename, username));
    }

    @Test
    void test_deleteFile_ifFileIsNotPresent_throwsException() {
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(false);

        assertThrows(FileException.class, () -> fileService.deleteFile(filename, username));
    }

    @Test
    void test_renameFile_expectedBehaviour() {
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(true);
        final String newFileName = "newFileName";
        final ResponseEntity<Object> expected = ResponseEntity.ok().build();

        assertEquals(expected, fileService.renameFile(filename, newFileName, username));
    }

    @Test
    void test_renameFile_ifFileIsNotPresent_throwsException() {
        when(fileRepository.existsByFilenameAndUserUsername(filename, username)).thenReturn(false);
        final String newFileName = "newFileName";

        assertThrows(FileException.class, () -> fileService.renameFile(filename, newFileName, username));
    }
}