package com.example.cloudservice.security;

import com.example.cloudservice.transfer.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingAuthenticationEntryPointTest {

    @Mock
    private MessageSourceAccessor messages;

    @InjectMocks
    private MessagingAuthenticationEntryPoint entryPoint;

    @Test
    void test_commence() throws IOException {
        final HttpStatus status = HttpStatus.UNAUTHORIZED;
        final String messageCode = "wrong.auth.token";
        final String errorMessage = "Токен доступа недействителен, отозван или поврежден";
        final String contentType = "application/json;charset=UTF-8";

        when(messages.getMessage(messageCode)).thenReturn(errorMessage);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, null);

        assertEquals(status.value(), response.getStatus());
        assertEquals(contentType, response.getContentType());

        final ObjectMapper objectMapper = new ObjectMapper();

        final ErrorMessage expected = new ErrorMessage(errorMessage, status.value());
        final ErrorMessage actual = objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);

        assertEquals(expected, actual);
    }
}