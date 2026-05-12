package io.github.salepartido.api.domain.canchas.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.canchas.model.Cancha;
import io.github.salepartido.api.domain.canchas.service.CanchaService;

@RestController
@RequestMapping("/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @GetMapping
    public List<Cancha> getCanchas() {
        // try {
        //     GuardarCanchasEnDB();
        // } catch (Exception e) {
        //     GuardarExcepcionEnLog(e);
        // }
        return canchaService.obtenerTodasLasCanchas();
    }

    private void GuardarExcepcionEnLog(Exception e) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("[%s] Error: %s\nStackTrace:\n%s\n\n",
                timestamp, e.getMessage(), e.getStackTrace().toString());

        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/log.txt", true)) {
            writer.write(logMessage);
        } catch (IOException ioException) {
            // Si no se puede escribir el log, al menos imprimir en consola
            System.err.println("Error al escribir log: " + ioException.getMessage());
        }
    }

    private void GuardarCanchasEnDB() {
        List<Cancha> canchas = canchaService.obtenerTodasLasCanchas();
        canchas.forEach(canchaService::guardarCancha);
    }

    @GetMapping("/{uuid}")
    public CanchaInfo getCanchaById(@PathVariable UUID uuid) {
        Cancha cancha = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
        return new CanchaInfo(cancha.getUuid(), cancha.getNombre());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaInfo createCancha(@RequestBody CanchaRequest request) {
        Cancha cancha = new Cancha();
        cancha.setNombre(request.name());
        
        Cancha saved = canchaService.guardarCancha(cancha);
        return new CanchaInfo(saved.getUuid(), saved.getNombre());
    }

    @PutMapping("/{uuid}")
    public CanchaInfo updateCancha(@PathVariable UUID uuid, @RequestBody CanchaRequest request) {
        Cancha existing = canchaService.buscarCanchaPorId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));

        existing.setNombre(request.name());
        
        Cancha updated = canchaService.guardarCancha(existing);
        return new CanchaInfo(updated.getUuid(), updated.getNombre());
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCancha(@PathVariable UUID uuid) {
        if (!canchaService.buscarCanchaPorId(uuid).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada");
        }
        canchaService.eliminarCancha(uuid);
    }

    /* 
    @GetMapping("/disponibilidad/{codigo}")
    public DisponibilidadResponse verificarDisponibilidad(@PathVariable String codigo) {
        boolean disponible = canchaService.verificarDisponibilidadCancha(codigo);
        return new DisponibilidadResponse(codigo, disponible);
    }
    */

    public static record CanchaRequest(String name, String code) {
    }

    public static record CanchaInfo(String uuid, String name) {
    }

    public static record DisponibilidadResponse(String codigo, boolean disponible) {
    }
}