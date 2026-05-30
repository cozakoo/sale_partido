package io.github.salepartido.api.domain.helloworld;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloWorldRepository extends JpaRepository<HelloWorld, UUID> {
    
}
