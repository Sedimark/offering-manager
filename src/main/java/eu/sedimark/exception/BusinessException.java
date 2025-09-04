package eu.sedimark.exception;

import jakarta.ws.rs.core.Response;

public class BusinessException extends ApiException {
    private final Response.Status status;

    public BusinessException(String message) {
        this(message, Response.Status.BAD_REQUEST);
    }

    public BusinessException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message, Throwable cause) {
        this(message, Response.Status.BAD_REQUEST, cause);
    }

    public BusinessException(String message, Response.Status status, Throwable cause) {
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
