package io.github.salepartido.api.infrastructure.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        Exception.class
    })
    public ProblemDetail handleAnything(Exception exception){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Operation failed");
        problem.setDetail("Something went wrong");

        return problem;
    }

}