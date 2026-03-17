package br.com.renan.vendas.online;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.config.import=",
        "spring.data.mongodb.uri=mongodb://localhost:27017/test",
        "services.produto.url=http://localhost:8082"
    }
)
class VendasServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
