package br.com.renan.vendas.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;


// @EnableConfigServer transforma essa aplicação boot comum num Spring Cloud Config Server:
// os outros serviços buscam a config deles aqui no startup em vez de ter application.yml próprio
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
