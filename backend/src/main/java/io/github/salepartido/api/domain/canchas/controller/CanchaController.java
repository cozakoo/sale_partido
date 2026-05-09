package io.github.salepartido.api.domain.canchas.controller;

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

import io.github.salepartido.api.domain.canchas.model.Cancha;
import io.github.salepartido.api.domain.canchas.service.CanchaService;

@RestController
@RequestMapping("/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @GetMapping
    public List<Cancha> getCanchas() {
        return canchaService.obtenerTodasLasCanchas();
    }

    @GetMapping("/{uuid}")
    public CanchaInfo getCanchaById(@PathVariable UUID uuid) {
        Cancha cancha = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
        return new CanchaInfo(cancha.getUuid(), cancha.getNombre(), cancha.getCodigo());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaInfo createCancha(@RequestBody CanchaRequest request) {
        Cancha cancha = new Cancha();
        cancha.setNombre(request.name());
        cancha.setCodigo(request.code());
        
        Cancha saved = canchaService.guardarCancha(cancha);
        return new CanchaInfo(saved.getUuid(), saved.getNombre(), saved.getCodigo());
    }

    @PutMapping("/{uuid}")
    public CanchaInfo updateCancha(@PathVariable UUID uuid, @RequestBody CanchaRequest request) {
        Cancha existing = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));

        existing.setNombre(request.name());
        existing.setCodigo(request.code());
        
        Cancha updated = canchaService.guardarCancha(existing);
        return new CanchaInfo(updated.getUuid(), updated.getNombre(), updated.getCodigo());
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCancha(@PathVariable UUID uuid) {
        if (!canchaService.buscarCanchaPorId(uuid).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada");
        }
        canchaService.eliminarCancha(uuid);
    }

    /* 
    @GetMapping("/disponibilidad/{codigo}")
    public DisponibilidadResponse verificarDisponibilidad(@PathVariable String codigo) {
        boolean disponible = canchaService.verificarDisponibilidadCancha(codigo);
        return new DisponibilidadResponse(codigo, disponible);
    }
    */

    public static record CanchaRequest(String name, String code) {
    }

    public static record CanchaInfo(String uuid, String name, String code) {
    }

    public static record DisponibilidadResponse(String codigo, boolean disponible) {
    }
}