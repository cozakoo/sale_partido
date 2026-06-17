package io.github.salepartido.api.domain.eventos.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.eventos.controller.dto.UsuarioSesionDTO;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioSesionDTO toSesionDTO(Usuario u) {
        if (u == null) return null;
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
