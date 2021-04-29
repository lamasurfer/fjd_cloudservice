package com.example.cloudservice.transfer.file;

public interface FileProjection {

    String getFilename();

    void setFilename(String filename);

    long getSize();

    void setSize(long size);
}
