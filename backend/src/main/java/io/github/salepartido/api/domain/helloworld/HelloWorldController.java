package io.github.salepartido.api.domain.helloworld;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
public class HelloWorldController {

    private final HelloWorldRepository helloWorldRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_KEY = "helloWorlds";

    public HelloWorldController(
            HelloWorldRepository helloWorldRepository,
            StringRedisTemplate redisTemplate) {
        this.helloWorldRepository = helloWorldRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/")
    public List<HelloWorldResponse> getAll() throws Exception {
        String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedData != null) {
            // Data found in Redis, deserialize and tag as CACHE
            List<HelloWorld> cachedList = objectMapper.readValue(cachedData, new TypeReference<List<HelloWorld>>() {});
            return cachedList.stream()
                    .map(hw -> new HelloWorldResponse(hw.getUuid(), hw.getText(), "CACHE"))
                    .collect(Collectors.toList());
        }

        // Not found in cache, fetch from Database
        List<HelloWorld> dbList = helloWorldRepository.findAll();
        
        // Serialize and save to Redis for subsequent requests with a 10-minute TTL
        redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(dbList), 10, TimeUnit.MINUTES);

        return dbList.stream()
                .map(hw -> new HelloWorldResponse(hw.getUuid(), hw.getText(), "DB"))
                .collect(Collectors.toList());
    }

    @PostMapping("/")
    public HelloWorldResponse create(@RequestBody HelloWorld helloWorld) {
        helloWorld.setUuid(null);
        HelloWorld saved = helloWorldRepository.save(helloWorld);
        
        // Invalidate (delete) the cache so fresh data gets loaded on the next GET
        redisTemplate.delete(CACHE_KEY);
        
        return new HelloWorldResponse(saved.getUuid(), saved.getText(), "DB");
    }

} 
