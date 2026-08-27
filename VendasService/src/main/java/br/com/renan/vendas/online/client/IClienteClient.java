package br.com.renan.vendas.online.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// fallback registrado mas inerte - falta feign.circuitbreaker.enabled=true pra ativar
// de verdade. deixado assim de propósito: o CadastroVenda já trata FeignException na
// mão de um jeito mais fino (diferencia 404 de indisponibilidade), ligar os dois juntos
// sem cuidado faria um 404 legítimo (cliente não existe) cair como 503 (serviço fora)
@FeignClient(name = "cliente-service", url = "${services.cliente.url}", fallback =  ClienteClientFallback.class)
public interface IClienteClient {

    @GetMapping("/clientes/is-cadastrado/{id}")
    ResponseEntity<Boolean> isCadastrado(@PathVariable("id") Long id);
}
