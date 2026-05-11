package io.github.salepartido.api.devtools.canchas;

import java.util.Collection;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.canchas.model.ConfiguracionDia;

@Profile("dev")
@RestController
@RequestMapping("/dev-canchas")
public class DevCanchaController {
    
    private final SeedService seedService;

    public DevCanchaController(SeedService seedService) {
        this.seedService = seedService;
    }

    @GetMapping("/preview/{count}")
    public Collection<ConfiguracionDia> preview(
        @PathVariable("count") int count
    ) {
        return seedService.seed(count);
    }

    @PostMapping("/seed")
    public void seed() {
        // poblar la db
    }
}
