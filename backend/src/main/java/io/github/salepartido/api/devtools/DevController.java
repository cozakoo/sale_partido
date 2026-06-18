package io.github.salepartido.api.devtools;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Profile("dev")
@RestController
@RequestMapping("/dev")
public class DevController {

    private final SeedService seedService;

    public DevController(SeedService seedService) {
        this.seedService = seedService;
    }

    @GetMapping("/seed-db")
    public ResponseEntity<Object> generarNuevoSqlStaging() {
        seedService.generate();
        return ResponseEntity.ok().body("Success");
    }
}