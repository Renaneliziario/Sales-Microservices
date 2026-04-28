package br.com.renan.vendas.online.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", url = "${services.cliente.url}")
public interface IClienteClient {

    @GetMapping("/cliente/isCadastrado/{id}")
    ResponseEntity<Boolean> isCadastrado(@PathVariable("id") Long id);
}
