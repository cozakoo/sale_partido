package io.github.salepartido.api.infrastructure.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        Exception.class
    })
    public ProblemDetail handleAnything(Exception exception){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Operación fallida");
        problem.setDetail("Algo salió mal, por favor reintente más tarde.");

        return problem;
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Operación fallida");
        problem.setDetail("Su petición contiene datos inválidos.");
        Map<String, List<String>> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors
                .computeIfAbsent(error.getField(), key -> new ArrayList<>())
                .add(error.getDefaultMessage());
        });

        problem.setProperty("errores", errors);

        return problem;
    }

}