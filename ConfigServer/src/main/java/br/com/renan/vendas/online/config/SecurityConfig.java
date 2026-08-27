package br.com.renan.vendas.online.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // chamada serviço-a-serviço, não formulário de browser
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            // Basic puro, credenciais em texto plano em cada request - só não é um problema
            // maior porque isso roda tudo dentro da rede docker interna, sem exposição externa
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
