package io.github.salepartido.api.devtools;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.NivelDeporte;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import io.github.salepartido.api.domain.eventos.repository.EventoRepository;
import io.github.salepartido.api.domain.eventos.repository.NivelDeporteRepository;
import io.github.salepartido.api.domain.eventos.repository.UsuarioRepository;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.DeporteRepository;
import io.github.salepartido.api.domain.locales.repository.LocalidadRepository;
import io.github.salepartido.api.domain.locales.service.LocalService;
import io.github.salepartido.api.domain.locales.service.TurnoService;

@Component
@Profile("dev")
public class SeedRepository {

    private final DeporteRepository deporteRepository;
    private final LocalidadRepository localidadRepository;
    private final NivelDeporteRepository nivelDeporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final LocalService localService;
    private final TurnoService turnoService;

    public SeedRepository(
            DeporteRepository deporteRepository,
            LocalidadRepository localidadRepository,
            NivelDeporteRepository nivelDeporteRepository,
            UsuarioRepository usuarioRepository,
            EventoRepository eventoRepository,
            LocalService localService,
            TurnoService turnoService) {
        this.deporteRepository = deporteRepository;
        this.localidadRepository = localidadRepository;
        this.nivelDeporteRepository = nivelDeporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.localService = localService;
        this.turnoService = turnoService;
    }

    public List<Localidad> findLocalidadesExistentes() {
        return localidadRepository.findAll();
    }

    public Localidad guardarLocalidad(Localidad localidad) {
        return localidadRepository.save(localidad);
    }

    public Local guardarLocal(Local local) {
        return localService.guardarLocal(local);
    }

    public List<Deporte> findDeportesExistentes() {
        return deporteRepository.findAll();
    }

    public Optional<Deporte> findDeporteByNombre(String nombre) {
        return deporteRepository.findByNombre(nombre);
    }

    public Deporte guardarDeporte(Deporte deporte) {
        return deporteRepository.save(deporte);
    }

    public List<NivelDeporte> findNivelesDeportePorDeporte(UUID deporteUuid) {
        return nivelDeporteRepository.findByDeporteUuidOrderByOrdenAsc(deporteUuid);
    }

    public NivelDeporte guardarNivelDeporte(NivelDeporte nivel) {
        return nivelDeporteRepository.save(nivel);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Turno guardarTurno(Turno turno) {
        return turnoService.guardarTurno(turno);
    }

    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }
}
