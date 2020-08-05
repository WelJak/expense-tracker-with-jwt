package pl.weljak.expensetrackerrestapiwithjwt.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EtResponseUtils {
    private EtResponseUtils(){}

    private static Clock clock = Clock.systemDefaultZone();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static ResponseEntity<EtResponse> success(String endpoint, Object any, String message, HttpStatus httpStatus) {
        EtResponse response = successEtResponse(endpoint, any, message, httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<EtResponse> error(String endpoint, String error, String message, HttpStatus httpStatus) {
        EtResponse response = errorEtResponse(endpoint, error, message, httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<EtResponse> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static EtResponse successEtResponse(String endpoint, Object any, String message, HttpStatus httpStatus) {
        return EtResponse.builder()
                .timeStamp(timeStamp())
                .path(endpoint)
                .responseCode(httpStatus.value())
                .message(message)
                .payload(any)
                .success(true)
                .status(httpStatus.getReasonPhrase())
                .build();
    }

    public static EtResponse errorEtResponse(String endpoint, String error, String message, HttpStatus httpStatus) {
        return EtResponse.builder()
                .timeStamp(timeStamp())
                .path(endpoint)
                .responseCode(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .success(false)
                .message(message)
                .error(error)
                .build();
    }

    private static String timeStamp() {
        LocalDateTime now = LocalDateTime.now(clock);
        return formatter.format(now);
    }

}
