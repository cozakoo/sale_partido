package io.github.salepartido.api.domain.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.eventos.controller.dto.UsuarioSesionDTO;
import io.github.salepartido.api.domain.eventos.controller.mapper.UsuarioMapper;
import io.github.salepartido.api.domain.eventos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    public List<UsuarioSesionDTO> getUsuarios() {
        return usuarioService.obtenerTodos().stream()
                .map(usuarioMapper::toSesionDTO)
                .toList();
    }
}
