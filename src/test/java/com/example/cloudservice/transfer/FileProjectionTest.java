package com.example.cloudservice.transfer;

import com.example.cloudservice.transfer.file.FileProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class FileProjectionTest {

    @Autowired
    private JacksonTester<FileProjection> jacksonTester;

    @Test
    void test_serialization() throws IOException {

        final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        final FileProjection projection = factory.createProjection(FileProjection.class);
        projection.setFilename("test.file");
        projection.setSize(7000);

        final JsonContent<FileProjection> result = this.jacksonTester.write(projection);
        final String expectedJson = "{\"filename\":\"test.file\",\"size\":7000}";
        assertEquals(expectedJson, result.getJson());
    }
}