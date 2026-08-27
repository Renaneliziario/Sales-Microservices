package br.com.renan.vendas.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
// @RefreshScope sem o endpoint /actuator/refresh exposto (só health,info no yml) não
// tem quem dispare o reload - fica pronto pra quando isso for ligado, mas hoje é inerte
@RefreshScope
public class ClienteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClienteServiceApplication.class, args);
	}

}
