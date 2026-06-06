package io.github.salepartido.api.domain.locales.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Reserva;
import io.github.salepartido.api.domain.locales.repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Reserva guardarReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public List<Reserva> guardarTodas(List<Reserva> reservas) {
        return reservaRepository.saveAll(reservas);
    }
}
