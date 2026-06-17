package io.github.salepartido.api.domain.eventos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.eventos.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
}
