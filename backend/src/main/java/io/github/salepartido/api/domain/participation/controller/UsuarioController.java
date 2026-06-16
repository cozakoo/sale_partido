package io.github.salepartido.api.domain.participation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.participation.controller.dto.UsuarioSesionDTO;
import io.github.salepartido.api.domain.participation.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioSesionDTO> getUsuarios() {
        return usuarioService.obtenerTodos().stream()
                .map(UsuarioSesionDTO::from)
                .toList();
    }
}
