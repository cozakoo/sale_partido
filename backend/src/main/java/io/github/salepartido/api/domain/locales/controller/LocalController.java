package io.github.salepartido.api.domain.locales.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.service.LocalService;
import io.github.salepartido.api.domain.locales.controller.dto.*;
import io.github.salepartido.api.domain.locales.controller.mapper.LocalMapper;
import io.github.salepartido.api.domain.locales.controller.mapper.CanchaMapper;
import io.github.salepartido.api.domain.locales.model.Local;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/locales")
public class LocalController {

    private final LocalService localService;
    private final LocalMapper localMapper;
    private final CanchaMapper canchaMapper;

    public LocalController(LocalService localService, LocalMapper localMapper, CanchaMapper canchaMapper) {
        this.localService = localService;
        this.localMapper = localMapper;
        this.canchaMapper = canchaMapper;
    }

    @GetMapping
    public List<LocalSummary> getLocales() {
        return localService.obtenerTodosLosLocales().stream()
                .map(localMapper::toSummary)
                .collect(Collectors.toList());
    }

    @GetMapping("/{uuid}")
    public LocalDetail getLocalById(@PathVariable UUID uuid) {
        Local local = localService.buscarLocalPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));
        return localMapper.toDetail(local);
    }

    @GetMapping("/{uuid}/canchas")
    public Object getCanchasFromLocal(@PathVariable UUID uuid, @RequestParam(defaultValue = "resumen") String view) {
        Local local = localService.buscarLocalPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));
        
        if ("detalle".equalsIgnoreCase(view)) {
            return local.getCanchas().stream().map(canchaMapper::toDetail).collect(Collectors.toList());
        } else {
            return local.getCanchas().stream().map(canchaMapper::toSummary).collect(Collectors.toList());
        }
    }

    @PostMapping("/{uuid}/configuraciones-horarios")
    public SaveCanchaConfiguracionHorarioResponse saveConfiguracionesHorarios(@PathVariable UUID uuid, @Valid @RequestBody SaveCanchaConfiguracionHorarioRequest request) {
        localService.actualizarConfiguracionesHorarios(uuid, request);
        return new SaveCanchaConfiguracionHorarioResponse(true, "Configuraciones guardadas exitosamente");
    }
}