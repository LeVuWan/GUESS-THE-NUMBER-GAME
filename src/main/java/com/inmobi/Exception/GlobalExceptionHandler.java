package com.inmobi.Exception;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setTimestamp(new Date());
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setPath(request.getDescription(false).replace("uri=", ""));
        res.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        res.setMessage(message);
        return res;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setTimestamp(new Date());
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setPath(request.getDescription(false).replace("uri=", ""));
        res.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());

        String message = "Malformed JSON request";

        // Nếu có nguyên nhân cụ thể (ví dụ parse lỗi)
        if (ex.getMostSpecificCause() != null) {
            message = ex.getMostSpecificCause().getMessage();
        }

        res.setMessage(message);
        return res;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Exception ex, WebRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setTimestamp(new Date());
        res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setPath(request.getDescription(false).replace("uri=", ""));
        res.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        if (ex instanceof MethodArgumentTypeMismatchException) {
            res.setMessage(" Failed to convert value of type");
        }
        return res;
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameter(
            org.springframework.web.bind.MissingServletRequestParameterException ex,
            WebRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setTimestamp(new Date());
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setPath(request.getDescription(false).replace("uri=", ""));
        res.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        res.setMessage("Missing required parameter: " + ex.getParameterName());
        return res;
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnexpectedType(UnexpectedTypeException ex, WebRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setTimestamp(new Date());
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setPath(request.getDescription(false).replace("uri=", ""));
        res.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        res.setMessage("Unexpected type: " + ex.getMessage());
        return res;
    }
}