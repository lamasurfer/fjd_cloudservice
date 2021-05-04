package com.example.cloudservice.security;

import com.example.cloudservice.transfer.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MessagingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final int STATUS = HttpStatus.UNAUTHORIZED.value();
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private final MessageSourceAccessor messages;
    private ObjectMapper objectMapper;

    public MessagingAuthenticationEntryPoint(MessageSourceAccessor messages) {
        this.messages = messages;
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public MessagingAuthenticationEntryPoint setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        final ErrorMessage errorMessage = new ErrorMessage(messages.getMessage("wrong.auth.token"), STATUS);
        response.setStatus(STATUS);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().println(objectMapper.writeValueAsString(errorMessage));
    }
}
