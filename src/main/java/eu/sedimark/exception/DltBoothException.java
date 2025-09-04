package eu.sedimark.exception;

import jakarta.ws.rs.core.Response;

public class DltBoothException extends RuntimeException {

    public DltBoothException(String message) {
        super(message);
    }

    public DltBoothException(String message, Throwable cause) {
        super(message, cause);
    }
}