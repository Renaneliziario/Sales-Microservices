package br.com.renan.vendas.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope // mesma situação do ClienteService: sem /actuator/refresh exposto, não dispara sozinho
public class ProdutoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProdutoServiceApplication.class, args);
	}

}
