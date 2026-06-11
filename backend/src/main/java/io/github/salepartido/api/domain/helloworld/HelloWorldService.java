package io.github.salepartido.api.domain.helloworld;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HelloWorldService {

    private final HelloWorldRepository helloWorldRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_KEY = "helloWorlds";

    public HelloWorldService(
            HelloWorldRepository helloWorldRepository,
            StringRedisTemplate redisTemplate) {
        this.helloWorldRepository = helloWorldRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(readOnly = true)
    public List<HelloWorld> getAllHelloWorlds() throws Exception {
        String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedData != null) {
            return objectMapper.readValue(cachedData, new TypeReference<List<HelloWorld>>() {});
        }

        List<HelloWorld> dbList = helloWorldRepository.findAll();
        redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(dbList), 10, TimeUnit.MINUTES);

        return dbList;
    }

    @Transactional
    public HelloWorld createHelloWorld(HelloWorld helloWorld) {
        helloWorld.setUuid(null);
        HelloWorld saved = helloWorldRepository.save(helloWorld);
        redisTemplate.delete(CACHE_KEY);
        return saved;
    }
}
