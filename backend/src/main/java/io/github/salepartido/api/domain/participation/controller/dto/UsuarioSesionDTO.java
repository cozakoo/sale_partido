package io.github.salepartido.api.domain.participation.controller.dto;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.salepartido.api.domain.participation.model.Usuario;

public record UsuarioSesionDTO(UUID uuid, String nombre, Map<String, String> habilidades) {

    public static UsuarioSesionDTO from(Usuario u) {
        Map<String, String> habs = Map.of();
        if (u.getHabilidades() != null) {
            habs = u.getHabilidades().stream()
                    .filter(h -> h.getDeporte() != null && h.getNivel() != null)
                    .collect(Collectors.toMap(
                            h -> h.getDeporte().getNombre(),
                            h -> h.getNivel().getNombre(),
                            (existing, replacement) -> existing
                    ));
        }
        return new UsuarioSesionDTO(u.getUuid(), u.getNombre(), habs);
    }
}
