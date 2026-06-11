package io.github.salepartido.api.domain.helloworld;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping("/")
    public List<HelloWorldResponse> getAll() {
        String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedData != null) {
            // Data found in Redis, deserialize and tag as CACHE
            try {
                List<HelloWorld> cachedList = objectMapper.readValue(cachedData, new TypeReference<List<HelloWorld>>() {});
                return cachedList.stream()
                        .map(hw -> new HelloWorldResponse(hw.getUuid(), hw.getText(), "CACHE"))
                        .collect(Collectors.toList());
            } catch (JsonProcessingException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al deserializar la cache de HelloWorld", ex);
            }
        }

        // Not found in cache, fetch from Database
        List<HelloWorld> dbList = helloWorldRepository.findAll();
        
        try {
            // Serialize and save to Redis for subsequent requests with a 10-minute TTL
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(dbList), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al serializar HelloWorld para cache", ex);
        }

        return dbList.stream()
                .map(hw -> new HelloWorldResponse(hw.getUuid(), hw.getText(), "DB"))
                .toList();
    }

    @PostMapping("/")
    public HelloWorldResponse create(@RequestBody HelloWorld helloWorld) {
        HelloWorld saved = helloWorldService.createHelloWorld(helloWorld);
        return new HelloWorldResponse(saved.getUuid(), saved.getText(), "DB");
    }

} 
