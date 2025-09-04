package eu.sedimark.model;

import java.time.Instant;
import lombok.Getter;

public class ErrorResponse {
    @Getter private final String timestamp;
    @Getter private final int status;
    @Getter private final String error;
    @Getter private final String message;
    @Getter private final String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
