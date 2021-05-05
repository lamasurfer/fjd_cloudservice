package com.example.cloudservice.integration;

import com.example.cloudservice.transfer.login.LoginRequest;
import com.example.cloudservice.transfer.login.LoginResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    private static final int APP_PORT = 8081;
    private static final String HOST = "http://localhost:";
    private static final String AUTH_TOKEN = "auth-token";
    private static final String BEARER = "Bearer ";
    private static final String file = "application.properties";

    private static final Network network = Network.newNetwork();

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest")
            .withNetwork(network)
            .withNetworkAliases("db")
            .withDatabaseName("storage_db")
            .withUsername("user")
            .withPassword("user");

    @Container
    private static final GenericContainer<?> CLOUD_SERVICE_APP = new GenericContainer<>("cloudservice:latest")
            .withNetwork(network)
            .withExposedPorts(APP_PORT)
            .withEnv(Map.of("DATASOURCE_URL", "jdbc:mysql://db:3306/storage_db"))
            .dependsOn(MYSQL_CONTAINER);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void test() {
        final int mappedPort = CLOUD_SERVICE_APP.getMappedPort(APP_PORT);
        // login
        final LoginRequest loginRequest = new LoginRequest("john", "john");

        final ResponseEntity<LoginResponse> loginResponseEntity =
                restTemplate.postForEntity(HOST + mappedPort + "/login", loginRequest, LoginResponse.class);

        assertEquals(HttpStatus.SC_OK, loginResponseEntity.getStatusCodeValue());

        final LoginResponse loginResponse = loginResponseEntity.getBody();

        assertNotNull(loginResponse);

        final String token = loginResponse.getToken();

        // upload file
        final MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("filename", file);
        parameters.add("file", new ClassPathResource(file));

        final HttpHeaders uploadFileHeaders = new HttpHeaders();
        uploadFileHeaders.set(AUTH_TOKEN, BEARER + token);
        uploadFileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        final HttpEntity<MultiValueMap<String, Object>> uploadFileEntity =
                new HttpEntity<>(parameters, uploadFileHeaders);

        final ResponseEntity<Object> uploadFileResponse =
                restTemplate.exchange(HOST + mappedPort + "/file",
                        HttpMethod.POST,
                        uploadFileEntity,
                        Object.class);
        assertEquals(HttpStatus.SC_OK,  uploadFileResponse.getStatusCodeValue());

        // download file
        final HttpHeaders downloadFileHeaders = new HttpHeaders();
        downloadFileHeaders.set(AUTH_TOKEN, BEARER + token);

        final HttpEntity<MultiValueMap<String, Object>> downloadFileEntity = new HttpEntity<>(downloadFileHeaders);

        final ResponseEntity<ByteArrayResource> downloadFileResponse =
                restTemplate.exchange(HOST + mappedPort + "/file?filename=" + file,
                        HttpMethod.GET,
                        downloadFileEntity,
                        ByteArrayResource.class);

        assertEquals(HttpStatus.SC_OK, downloadFileResponse.getStatusCodeValue());

        final ByteArrayResource byteArrayResource = downloadFileResponse.getBody();
        assertNotNull(byteArrayResource);

        final byte[] loadedData = byteArrayResource.getByteArray();

        final String expected = "server.port=8081\r\n" +
                "spring.main.banner-mode=off\r\n" +
                "\r\n" +
                "app.security.cors.allowed-origins=http://localhost:8080, http://localhost\r\n" +
                "app.security.cors.allowed-methods=GET, POST, PUT, DELETE\r\n" +
                "app.security.cors.allowed-headers=Authorization, Content-Type, auth-token\r\n" +
                "app.security.cors.allow-credentials=true\r\n" +
                "app.security.jwt.issuer=example.com\r\n" +
                "app.security.jwt.token-expiration-time-millis=600000\r\n" +
                "app.security.jwt.invalid-token-store-time-millis=660000\r\n" +
                "app.security.jwt.cache-reset-rate-millis=300000\r\n" +
                "app.security.jwt.signature-algorithm=RS512\r\n" +
                "app.security.key-provider.signature-algorithm=RSA\r\n" +
                "app.security.key-provider.key-size=2048\r\n" +
                "app.security.key-provider.public-key-file-name=public.key\r\n" +
                "app.security.key-provider.private-key-file-name=private.key";

        assertEquals(expected, new String(loadedData, StandardCharsets.UTF_8));

        // logout
        final HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.set(AUTH_TOKEN, BEARER + token);

        final HttpEntity<Object> logoutEntity = new HttpEntity<>(logoutHeaders);

        final ResponseEntity<Object> logoutResponse =
                restTemplate.exchange(HOST + mappedPort + "/logout",
                        HttpMethod.POST,
                        logoutEntity,
                        Object.class);
        assertEquals(HttpStatus.SC_OK, logoutResponse.getStatusCodeValue());

        // logged-out token -> unauthorized
        final HttpHeaders loggedOutHeaders = new HttpHeaders();
        loggedOutHeaders.set(AUTH_TOKEN, BEARER + token);

        final HttpEntity<Object> loggedOutEntity = new HttpEntity<>(loggedOutHeaders);

        final ResponseEntity<Object> loggedOutResponse =
                restTemplate.exchange(HOST + mappedPort + "/list?limit=3",
                        HttpMethod.GET,
                        loggedOutEntity,
                        Object.class);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, loggedOutResponse.getStatusCodeValue());
    }
}
