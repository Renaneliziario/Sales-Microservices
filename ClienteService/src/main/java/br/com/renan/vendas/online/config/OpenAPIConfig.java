package br.com.renan.vendas.online.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI customOpenAPI(
			@Value("${application.version:1.0.0}") String appVersion) {

		return new OpenAPI()
				.info(new Info()
						.title("Cliente Service API")
						.version(appVersion)
						.description("API para gerenciamento de clientes")
						.termsOfService("http://swagger.io/terms/")
						.contact(new Contact()
								.name("Renan Queiroz")
								.url("https://github.com/renaneliziario"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://springdoc.org")));
	}
}