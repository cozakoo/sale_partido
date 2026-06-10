package io.github.salepartido.api.infrastructure.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.participation.exception.EventoNoDisponibleException;
import io.github.salepartido.api.domain.participation.exception.EventoSinCuposException;
import io.github.salepartido.api.domain.participation.exception.InvitacionYaRespondidaException;
import io.github.salepartido.api.domain.participation.exception.NivelInsuficienteException;
import io.github.salepartido.api.domain.participation.exception.ParticipanteYaRegistradoException;
import io.github.salepartido.api.domain.participation.exception.TipoEventoInvalidoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(exception.getStatusCode());
        problem.setTitle("Operación fallida");
        problem.setDetail(exception.getReason());
        return problem;
    }

    @ExceptionHandler({
        EventoNoDisponibleException.class,
        EventoSinCuposException.class,
        NivelInsuficienteException.class,
        ParticipanteYaRegistradoException.class,
        TipoEventoInvalidoException.class,
        InvitacionYaRespondidaException.class
    })
    public ProblemDetail handleConflictException(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Operación fallida");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ProblemDetail handleBadRequestException(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Operación fallida");
        problem.setDetail("Su petición contiene datos inválidos.");
        return problem;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        problem.setTitle("Operación fallida");
        problem.setDetail("El tipo de contenido no es compatible.");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
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

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAnything(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Operación fallida");
        problem.setDetail("Algo salió mal, por favor reintente más tarde.");
        return problem;
    }

}