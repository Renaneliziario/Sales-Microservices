package br.com.renan.vendas.online.ProdutoService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.config.import=",
        "spring.data.mongodb.uri=mongodb://localhost:27017/test"
    }
)
class ProdutoServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
