package pl.weljak.expensetrackerrestapiwithjwt.webapi.exceptionhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponse;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponseUtils;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<EtResponse> accessDenied(Exception e, WebRequest webRequest) {
        log.warn("Access denied. Insufficient privileges. {}", e.getMessage());
        return new ResponseEntity<>(EtResponseUtils.errorEtResponse(webRequest.getDescription(false),"AccessDeniedException", "access denied", HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<EtResponse> badCredentials(Exception e, WebRequest webRequest) {
        log.warn("Bad credentials {}", e.getMessage());
        return new ResponseEntity<>(EtResponseUtils.errorEtResponse(webRequest.getDescription(false),"BadCredentials", "Wrong credentials", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }
}
