package com.example.cloudservice.transfer;

import com.example.cloudservice.transfer.file.RenameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class RenameRequestTest {

    @Autowired
    private JacksonTester<RenameRequest> jacksonTester;

    @Test
    void test_deserialization() throws IOException {
        final String json = "{\n" +
                "  \"filename\": \"test.file\"\n" +
                "}";

        final RenameRequest renameRequest = jacksonTester.parseObject(json);
        assertEquals("test.file", renameRequest.getFilename());
    }
}