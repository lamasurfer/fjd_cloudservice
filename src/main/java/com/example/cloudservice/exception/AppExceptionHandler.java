package com.example.cloudservice.exception;

import com.example.cloudservice.transfer.ErrorMessage;
import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionHandler.class);
    private final MessageSourceAccessor messages;

    public AppExceptionHandler(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    // http method
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {
        final String message = messages.getMessage("request.invalid.method");
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // json parsing
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        final String message = messages.getMessage("request.invalid.message");
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // request param
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException e,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        final String message = messages.getMessage("request.invalid.parameter");
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // request param
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        final String message = messages.getMessage("request.invalid.parameter");
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // /list?limit=asdf
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        final String message = messages.getMessage("request.invalid.data.format");
        ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // multipart
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorMessage> handleMultipart(MultipartException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final String message = messages.getMessage("request.upload.file.is.missing");
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(errorMessage);
    }

    // validation annotations
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        final BindingResult result = e.getBindingResult();
        final String message = result.getAllErrors()
                .stream()
                .map(messages::getMessage)
                .sorted()
                .collect(Collectors.joining(", "));
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).headers(headers).body(errorMessage);
    }

    // validation annotations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(ConstraintViolationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(", "));
        final ErrorMessage errorMessage = new ErrorMessage(message, status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentials(BadCredentialsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final ErrorMessage errorMessage = new ErrorMessage(messages.getMessage("bad.credentials"), status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUsernameNotFound(UsernameNotFoundException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final ErrorMessage errorMessage = new ErrorMessage(messages.getMessage("user.not.found"), status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorMessage> handleFileException(FileException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(JOSEException.class)
    public ResponseEntity<ErrorMessage> handleJOSEException(JOSEException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), status.value());
        LOGGER.warn(e.getClass().getSimpleName() + " : " + errorMessage.getMessage());
        return ResponseEntity.status(status).body(errorMessage);
    }
}
