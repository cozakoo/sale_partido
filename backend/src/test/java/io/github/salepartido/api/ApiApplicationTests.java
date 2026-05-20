package io.github.salepartido.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ApiApplicationTests {

    @MockitoBean
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

}
