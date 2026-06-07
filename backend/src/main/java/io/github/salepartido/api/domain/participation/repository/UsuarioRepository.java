package io.github.salepartido.api.domain.participation.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.participation.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
}
