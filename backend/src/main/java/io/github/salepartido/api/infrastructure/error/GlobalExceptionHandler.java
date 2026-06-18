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

import io.github.salepartido.api.domain.eventos.exception.CupoMaximoSuperaCapacidadException;
import io.github.salepartido.api.domain.eventos.exception.CupoMinimoInvalidoException;
import io.github.salepartido.api.domain.eventos.exception.CupoMinimoMayorMaximoException;
import io.github.salepartido.api.domain.eventos.exception.EventoNoDisponibleException;
import io.github.salepartido.api.domain.eventos.exception.EventoSinCuposException;
import io.github.salepartido.api.domain.eventos.exception.InvitacionYaRespondidaException;
import io.github.salepartido.api.domain.eventos.exception.NivelInsuficienteException;
import io.github.salepartido.api.domain.eventos.exception.ParticipanteYaRegistradoException;
import io.github.salepartido.api.domain.eventos.exception.TiempoCancelacionInvalidoException;
import io.github.salepartido.api.domain.eventos.exception.TipoEventoInvalidoException;
import io.github.salepartido.api.domain.eventos.exception.TurnoRequeridoException;
import io.github.salepartido.api.domain.locales.exception.LocalNoEncontradoException;
import io.github.salepartido.api.domain.locales.exception.CanchaNoEncontradaException;
import io.github.salepartido.api.domain.locales.exception.FechaInvalidaException;
import io.github.salepartido.api.domain.locales.exception.HorarioInvalidoException;
import io.github.salepartido.api.domain.locales.exception.CanchaNoPerteneceAlLocalException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_TITLE = "Operación fallida";

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(exception.getStatusCode());
        problem.setTitle(ERROR_TITLE);
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
        problem.setTitle(ERROR_TITLE);
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({
        CupoMaximoSuperaCapacidadException.class
    })
    public ProblemDetail handleCupoConflict(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ProblemDetail handleBadRequestException(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail("Su petición contiene datos inválidos.");
        return problem;
    }

    @ExceptionHandler({
        CupoMinimoInvalidoException.class,
        CupoMinimoMayorMaximoException.class,
        TiempoCancelacionInvalidoException.class,
        FechaInvalidaException.class,
        HorarioInvalidoException.class,
        CanchaNoPerteneceAlLocalException.class
    })
    public ProblemDetail handleDomainBadRequest(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({
        TurnoRequeridoException.class,
        LocalNoEncontradoException.class,
        CanchaNoEncontradaException.class
    })
    public ProblemDetail handleNotFoundRuntime(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail("El tipo de contenido no es compatible.");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail("Su petición contiene datos inválidos.");
        Map<String, List<String>> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage())
        );
        problem.setProperty("errores", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAnything(Exception exception) {
        exception.printStackTrace();
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle(ERROR_TITLE);
        problem.setDetail("Algo salió mal, por favor reintente más tarde.");
        return problem;
    }

}