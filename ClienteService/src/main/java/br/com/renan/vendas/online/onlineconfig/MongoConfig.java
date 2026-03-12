package br.com.renan.vendas.online.onlineconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "br.com.renan.vendas.online.repository")
public class MongoConfig {

}
