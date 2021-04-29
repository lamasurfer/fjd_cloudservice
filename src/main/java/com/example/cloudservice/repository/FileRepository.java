package com.example.cloudservice.repository;

import com.example.cloudservice.model.FileEntity;
import com.example.cloudservice.transfer.file.FileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, String> {

    Optional<FileEntity> findFileByFilenameAndUserUsername(String filename, String username);

    boolean existsByFilenameAndUserUsername(String filename, String username);

    void removeByFilenameAndUserUsername(String filename, String username);

    @Modifying
    @Query("UPDATE files f SET f.filename = :newFilename WHERE f.filename = :filename AND f.user.username = :username")
    void renameFile(String filename, String newFilename, String username);

    @Query(value = "SELECT filename, size FROM files where username = :username", nativeQuery = true)
    List<FileProjection> findFileByUserUsername(String username);
}
