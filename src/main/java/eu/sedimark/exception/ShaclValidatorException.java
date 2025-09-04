package eu.sedimark.exception;

public class ShaclValidatorException extends RuntimeException {

    public ShaclValidatorException(String message) {
        super(message);
    }

    public ShaclValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}