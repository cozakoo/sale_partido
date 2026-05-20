package io.github.salepartido.api.devtools.canchas;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.service.LocalService;

@Profile("dev")
@RestController
@RequestMapping("/dev-locales")
public class DevCanchaController {
    
    private final SeedService seedService;

    private final LocalService localService;

    public DevCanchaController(LocalService localService, SeedService seedService) {
        this.seedService = seedService;
        this.localService = localService;
    }

    @GetMapping("/random/{cantidad}")
    public List<Local> generarLocalesRandom(
        @PathVariable("cantidad") int cantidad
    ) {
        return seedService.generarLocales(cantidad, 3);
    }

    @GetMapping("/poblar")
    public String seed() {
        List<Local> canchas = seedService.generarLocales(50, 3);
        canchas.forEach(localService::guardarLocal);
        return "Locales poblados con éxito";
    }


}
