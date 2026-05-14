package io.github.salepartido.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ApiApplicationTests {

    @MockBean
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

}
