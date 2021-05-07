package com.example.cloudservice.repository;

import com.example.cloudservice.model.FileEntity;
import com.example.cloudservice.model.User;
import com.example.cloudservice.transfer.file.FileProjection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileRepositoryTest {

    private final User user1 = new User().setUsername("user1");
    private final User user2 = new User().setUsername("user2");

    private final FileEntity user1_file1 = new FileEntity("test1", "testType", 1, new byte[0], user1);
    private final FileEntity user1_file2 = new FileEntity("test2", "testType", 1, new byte[0], user1);
    private final FileEntity user2_file1 = new FileEntity("test3", "testType", 1, new byte[0], user2);

    @Autowired
    private FileRepository fileRepository;

    @AfterEach
    void clear() {
        fileRepository.deleteAll();
    }

    @Test
    void test_findFileByFilenameAndUserUsername() {
        fileRepository.saveAndFlush(user1_file1);
        fileRepository.saveAndFlush(user2_file1);

        final Optional<FileEntity> optionalFile1 = fileRepository
                .findFileByFilenameAndUserUsername(user1_file1.getFilename(), user1.getUsername());
        final Optional<FileEntity> optionalFile2 = fileRepository
                .findFileByFilenameAndUserUsername(user2_file1.getFilename(), user2.getUsername());
        if (optionalFile1.isEmpty() || optionalFile2.isEmpty()) {
            fail();
        }
        final FileEntity fileEntity1 = optionalFile1.get();
        final FileEntity fileEntity2 = optionalFile2.get();
        assertEquals(user1_file1, fileEntity1);
        assertEquals(user2_file1, fileEntity2);

    }

    @Test
    void test_existsByFilenameAndUserUsername() {
        fileRepository.saveAndFlush(user1_file1);
        fileRepository.saveAndFlush(user2_file1);

        assertTrue(fileRepository.existsByFilenameAndUserUsername(user1_file1.getFilename(), user1.getUsername()));
        assertTrue(fileRepository.existsByFilenameAndUserUsername(user2_file1.getFilename(), user2.getUsername()));
        assertFalse(fileRepository.existsByFilenameAndUserUsername(user1_file2.getFilename(), user1.getUsername()));
    }

    @Test
    @Transactional
    void test_removeByFilenameAndUserUsername() {
        fileRepository.saveAndFlush(user1_file1);
        fileRepository.saveAndFlush(user2_file1);

        fileRepository.removeByFilenameAndUserUsername(user1_file1.getFilename(), user1.getUsername());
        final List<FileEntity> fileEntityList = fileRepository.findAll();

        assertFalse(fileEntityList.contains(user1_file1));
        assertTrue(fileEntityList.contains(user2_file1));
    }

    @Test
    @Transactional
    void test_renameFile() {
        final String newFileName = "newFileName";

        fileRepository.saveAndFlush(user1_file1);
        fileRepository.renameFile(user1_file1.getFilename(), newFileName, user1.getUsername());

        assertFalse(fileRepository.existsByFilenameAndUserUsername(user1_file1.getFilename(), user1.getUsername()));
        assertTrue(fileRepository.existsByFilenameAndUserUsername(newFileName, user1.getUsername()));
    }

    @Test
    void test_findFileByUserUsername() {
        fileRepository.saveAndFlush(user1_file1);
        fileRepository.saveAndFlush(user2_file1);

        final List<FileProjection> fileProjections = fileRepository.findFileByUserUsername(user1.getUsername());
        assertEquals(1, fileProjections.size());

        final FileProjection fileProjection = fileProjections.get(0);
        assertEquals(user1_file1.getFilename(), fileProjection.getFilename());
        assertEquals(user1_file1.getSize(), fileProjection.getSize());
    }
}