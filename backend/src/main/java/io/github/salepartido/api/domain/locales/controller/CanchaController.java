package io.github.salepartido.api.domain.locales.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.controller.dto.CanchaDetail;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaRequestDTO;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaSummary;
import io.github.salepartido.api.domain.locales.controller.mapper.CanchaMapper;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.service.CanchaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/canchas")
public class CanchaController {

    private final CanchaService canchaService;
    private final CanchaMapper canchaMapper;

    public CanchaController(CanchaService canchaService, CanchaMapper canchaMapper) {
        this.canchaService = canchaService;
        this.canchaMapper = canchaMapper;
    }

    @GetMapping
    public List<CanchaSummary> getCanchas() {
        return canchaService.obtenerTodasLasCanchas().stream()
                .map(canchaMapper::toSummary)
                .collect(Collectors.toList());
    }

    @GetMapping("/{uuid}")
    public CanchaDetail getCanchaById(@PathVariable UUID uuid) {
        Cancha cancha = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
        return canchaMapper.toDetail(cancha);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaSummary createCancha(@Valid @RequestBody CanchaRequestDTO request) {
        Cancha cancha = new Cancha();
        cancha.setNombre(request.name());
        
        Cancha saved = canchaService.guardarCancha(cancha);
        return canchaMapper.toSummary(saved);
    }

    @PutMapping("/{uuid}")
    public CanchaSummary updateCancha(@PathVariable UUID uuid, @Valid @RequestBody CanchaRequestDTO request) {
        Cancha existing = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));

        existing.setNombre(request.name());
        
        Cancha updated = canchaService.guardarCancha(existing);
        return canchaMapper.toSummary(updated);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCancha(@PathVariable UUID uuid) {
        if (!canchaService.buscarCanchaPorId(uuid).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada");
        }
        canchaService.eliminarCancha(uuid);
    }
}