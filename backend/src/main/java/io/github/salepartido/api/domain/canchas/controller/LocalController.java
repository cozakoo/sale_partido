package io.github.salepartido.api.domain.canchas.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

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

@RestController
@RequestMapping("/locales")
public class LocalController {

    private final List<LocalInfo> locales = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @GetMapping
    public List<LocalInfo> getLocales() {
            LocalInfo local1 = new LocalInfo(1, "Local A", "Dirección A");
            LocalInfo local2 = new LocalInfo(2, "Local B", "Dirección B");
            locales.add(local1);
            locales.add(local2);
        
        return locales;
    }

    @GetMapping("/{id}")
    public LocalInfo getLocalById(@PathVariable long id) {
        return locales.stream()
                .filter(local -> local.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalInfo createLocal(@RequestBody LocalRequest request) {
        LocalInfo local = new LocalInfo(idSequence.incrementAndGet(), request.name(), request.address());
        locales.add(local);
        return local;
    }

    @PutMapping("/{id}")
    public LocalInfo updateLocal(@PathVariable long id, @RequestBody LocalRequest request) {
        LocalInfo existing = locales.stream()
                .filter(local -> local.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));

        LocalInfo updated = new LocalInfo(existing.id(), request.name(), request.address());
        locales.remove(existing);
        locales.add(updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocal(@PathVariable long id) {
        boolean removed = locales.removeIf(local -> local.id() == id);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado");
        }
    }

    public static record LocalRequest(String name, String address) {
    }

    public static record LocalInfo(long id, String name, String address) {
    }
}
