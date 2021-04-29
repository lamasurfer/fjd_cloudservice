package com.example.cloudservice.transfer.file;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class RenameRequest {
    @NotBlank(message = "{rename.file.new.filename.is.blank}")
    private String filename;

    public RenameRequest() {
    }

    public RenameRequest(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public RenameRequest setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenameRequest that = (RenameRequest) o;
        return Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename);
    }

    @Override
    public String toString() {
        return "RenameRequest{" +
                "filename='" + filename + '\'' +
                '}';
    }
}
