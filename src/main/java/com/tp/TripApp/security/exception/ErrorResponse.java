package  com.tp.TripApp.security.exception;

import java.time.Instant;

public class ErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, Instant timestamp, String message, String path) {
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}