package br.com.renan.vendas.online.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteClientFallback implements IClienteClient {

    @Override
    public ResponseEntity<Boolean> isCadastrado(Long id) {
        throw new IllegalStateException("Serviço de clientes indisponível ao verificar cadastro do id: " + id);
    }
}
