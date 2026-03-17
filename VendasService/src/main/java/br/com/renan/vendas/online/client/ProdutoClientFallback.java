package br.com.renan.vendas.online.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.renan.vendas.online.dto.ProdutoDTO;

@Component
public class ProdutoClientFallback implements IProdutoClient {

    @Override
    public ResponseEntity<ProdutoDTO> buscarPorCodigo(String codigo) {
        throw new IllegalStateException("Serviço de produtos indisponível ao buscar o código: " + codigo);
    }
}
