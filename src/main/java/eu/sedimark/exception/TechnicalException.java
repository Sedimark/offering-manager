package eu.sedimark.exception;

import jakarta.ws.rs.core.Response;

import java.io.IOException;

public class TechnicalException extends ApiException {
    private final Response.Status status;

    public TechnicalException(String message) {
        this(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public TechnicalException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public TechnicalException(String message, Throwable cause) {
        this(message, Response.Status.INTERNAL_SERVER_ERROR, cause);
    }

    public TechnicalException(String message, Response.Status status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    @Override
    public Response.Status getHttpStatus() {
        return this.status;
    }

    @Override
    public String getErrorType() {
        return this.status.getReasonPhrase();
    }
}
