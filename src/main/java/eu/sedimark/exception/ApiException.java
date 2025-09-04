package eu.sedimark.exception;

import jakarta.ws.rs.core.Response;

public abstract class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract Response.Status getHttpStatus();

    public abstract String getErrorType();
}