package eu.sedimark.exception;

import eu.sedimark.model.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        int status;
        String error;

        logException(exception);

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getHttpStatus().getStatusCode();
            error = apiException.getErrorType();
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            error = "Internal Server Error";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                error,
                exception.getMessage(),
                uriInfo.getPath()
        );

        return Response.status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void logException(Throwable exception) {
        if (exception instanceof ApiException) {
            LOGGER.warning(buildLogMessage(exception));
        } else {
            LOGGER.severe(buildLogMessage(exception));
        }
    }

    private String buildLogMessage(Throwable exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception caught: ").append(exception.getClass().getSimpleName())
                .append(" - Message: ").append(exception.getMessage());

        StackTraceElement[] stackTrace = exception.getStackTrace();
        int limit = Math.min(6, stackTrace.length);
        for (int i = 0; i < limit; i++) {
            sb.append("\n\tat ").append(stackTrace[i].toString());
        }
        if (stackTrace.length > 5) {
            sb.append("\n\t... and ").append(stackTrace.length - 5).append(" more");
        }
        return sb.toString();
    }
}
