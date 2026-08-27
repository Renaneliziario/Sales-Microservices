package br.com.renan.vendas.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;


// profile native, não git-backed - a config vive dentro desse jar (classpath:/config).
// mudar uma porta ou uma url de banco significa rebuild + redeploy do ConfigServer,
// não um git push. bom o suficiente pra 4 serviços, revisar se isso crescer muito mais
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
