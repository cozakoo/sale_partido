package io.github.salepartido.api.domain;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloWorldRepository extends JpaRepository<HelloWorld, UUID> {
    
}
