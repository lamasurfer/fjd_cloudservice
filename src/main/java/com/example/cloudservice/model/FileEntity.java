package com.example.cloudservice.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String filename;
    private String fileType;
    private long size;
    @Lob
    private byte[] data;
    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    public FileEntity() {
    }

    public FileEntity(String filename,
                      String fileType,
                      long size,
                      byte[] data,
                      User user) {
        this.filename = filename;
        this.fileType = fileType;
        this.size = size;
        this.data = data;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public FileEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public FileEntity setFilename(String name) {
        this.filename = name;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public FileEntity setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public long getSize() {
        return size;
    }

    public FileEntity setSize(long size) {
        this.size = size;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public FileEntity setData(byte[] data) {
        this.data = data;
        return this;
    }

    public User getUser() {
        return user;
    }

    public FileEntity setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return id == that.id
                && size == that.size
                && Objects.equals(filename, that.filename)
                && Objects.equals(fileType, that.fileType)
                && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filename, fileType, size);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                ", id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", fileType='" + fileType + '\'' +
                ", size=" + size +
                ", user=" + user.getUsername() +
                '}';
    }
}
