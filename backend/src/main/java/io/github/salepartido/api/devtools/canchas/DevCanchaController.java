package io.github.salepartido.api.devtools.canchas;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@Profile("dev")
public class DevCanchaController {
    
    private final SeedService seedService;

    public DevCanchaController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping("/seed")
    public void seed() {
        seedService.seed();
    }
}
