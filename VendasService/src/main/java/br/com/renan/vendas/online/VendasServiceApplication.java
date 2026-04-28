package br.com.renan.vendas.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@RefreshScope
@EnableFeignClients
@EnableJpaRepositories
public class VendasServiceApplication {

        public static void main(String[] args) {
                SpringApplication.run(VendasServiceApplication.class, args);
        }

}
