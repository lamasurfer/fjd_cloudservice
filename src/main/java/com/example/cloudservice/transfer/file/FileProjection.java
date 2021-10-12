package com.example.cloudservice.transfer.file;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"filename", "size"})
public interface FileProjection {

    String getFilename();

    void setFilename(String filename);

    long getSize();

    void setSize(long size);
}
