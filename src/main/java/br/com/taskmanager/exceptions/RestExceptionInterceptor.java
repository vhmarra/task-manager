package br.com.taskmanager.exceptions;


import br.com.taskmanager.dtos.response.ExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;

@ControllerAdvice
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RestExceptionInterceptor {

    @ExceptionHandler({ InvalidInputException.class })
    public ResponseEntity<?> handleInvalidInputException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 400L),new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<?> handleNotFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 400L),new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({ ObjectAlreadyExistsException.class })
    public ResponseEntity<?> handleObjectAlreadyExistsException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 400L),new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({ TokenNotFoundException.class })
    public ResponseEntity<?> handleTokenNotFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

    @ExceptionHandler({ NotEnoughPermissionsException.class })
    public ResponseEntity<?> handleNotEnoughPermissionsException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

    @ExceptionHandler({ ObjectNotFoundException.class })
    public ResponseEntity<?> handleObjectNotFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 404L),new HttpHeaders(), NOT_FOUND);
    }

    @ExceptionHandler({ ExternalApiException.class })
    public ResponseEntity<?> handleExternalApiException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ExceptionResponse(ex.getMessage(), 408L),new HttpHeaders(), REQUEST_TIMEOUT);
    }


}
