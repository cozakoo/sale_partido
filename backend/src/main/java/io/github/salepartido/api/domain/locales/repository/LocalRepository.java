package io.github.salepartido.api.domain.locales.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.salepartido.api.domain.locales.model.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, UUID> {}