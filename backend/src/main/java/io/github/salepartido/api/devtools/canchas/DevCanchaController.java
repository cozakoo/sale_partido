package io.github.salepartido.api.devtools.canchas;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.canchas.model.Cancha;
import io.github.salepartido.api.domain.canchas.model.Local;
import io.github.salepartido.api.domain.canchas.service.CanchaService;

@Profile("dev")
@RestController
@RequestMapping("/dev-canchas")
public class DevCanchaController {
    
    private final SeedService seedService;

    private final CanchaService canchaService;

    public DevCanchaController(CanchaService canchaService, SeedService seedService) {
        this.seedService = seedService;
        this.canchaService = canchaService;
    }

    @GetMapping("/locales/random/{cantidad}")
    public List<Local> generarLocalesRandom(
        @PathVariable("cantidad") int cantidad
    ) {
        return seedService.generarLocales(cantidad, 3);
    }

    @PostMapping("/canchas/poblar")
    public void seed() {
        List<Cancha> canchas = seedService.generarCanchas(50);
        canchas.forEach(canchaService::guardarCancha);
    }


}
