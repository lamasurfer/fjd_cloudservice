package com.example.cloudservice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "files")
public class FileEntity {
    @Id
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
        return Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename);
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                ", filename='" + filename + '\'' +
                ", fileType='" + fileType + '\'' +
                ", size=" + size +
                ", user=" + user.getUsername() +
                '}';
    }
}
