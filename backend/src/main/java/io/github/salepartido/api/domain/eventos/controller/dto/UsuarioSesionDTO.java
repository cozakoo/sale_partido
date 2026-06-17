package io.github.salepartido.api.domain.eventos.controller.dto;

import java.util.Map;
import java.util.UUID;

public record UsuarioSesionDTO(UUID uuid, String nombre, Map<String, String> habilidades) {
}
