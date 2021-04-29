package com.example.cloudservice.security;

import com.example.cloudservice.transfer.ErrorMessage;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MessagingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final int STATUS = HttpStatus.UNAUTHORIZED.value();
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private final MessageSourceAccessor messages;

    public MessagingAuthenticationEntryPoint(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    // TODO нормальные сообщения сделать
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        final ErrorMessage errorMessage = new ErrorMessage(messages.getMessage("wrong.auth.token"), STATUS);
        response.setStatus(STATUS);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().println(errorMessage);
    }
}
