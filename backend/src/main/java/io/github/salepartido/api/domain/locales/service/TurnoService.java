package io.github.salepartido.api.domain.locales.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public Turno guardarTurno(Turno turno) {
        return turnoRepository.save(turno);
    }

    public List<Turno> guardarTodos(List<Turno> turnos) {
        return turnoRepository.saveAll(turnos);
    }
}
