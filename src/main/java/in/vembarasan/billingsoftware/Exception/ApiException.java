package in.vembarasan.billingsoftware.Exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, Object> additionalData;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.additionalData = null;
    }

    public ApiException(String message, HttpStatus status, Map<String, Object> additionalData) {
        super(message);
        this.status = status;
        this.additionalData = additionalData;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

}
