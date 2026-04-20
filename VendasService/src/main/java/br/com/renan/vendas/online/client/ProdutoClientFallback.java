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

    @Override
    public ResponseEntity<Void> baixarEstoque(String codigo, Integer quantidade) {
        throw new IllegalStateException("Serviço de produtos indisponível ao baixar estoque do código: " + codigo);
    }

    @Override
    public ResponseEntity<Void> reporEstoque(String codigo, Integer quantidade) {
        throw new IllegalStateException("Serviço de produtos indisponível ao repor estoque do código: " + codigo);
    }
}
