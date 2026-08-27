package br.com.renan.vendas.online.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

// implementado certo (a versão anterior era um stub vazio que nem compilava contra a
// interface), mas nunca é chamado hoje - ver o comentário no IClienteClient sobre o
// circuit breaker estar desligado de propósito
@Component
public class ClienteClientFallback implements IClienteClient {

    @Override
    public ResponseEntity<Boolean> isCadastrado(Long id) {
        throw new IllegalStateException("Serviço de clientes indisponível ao verificar cadastro do id: " + id);
    }
}
