package fr.afpa.hostel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.afpa.hostel.dto.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ DataNotFoundException.class })
    public ResponseEntity<Response> handleDataNotFoundException(final DataNotFoundException e) {

        return new ResponseEntity<>(new Response("error", "Donnée non retrouvée."), HttpStatus.NOT_FOUND);
    }
    
}
