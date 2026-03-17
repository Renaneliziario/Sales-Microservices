package br.com.renan.vendas.online.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.renan.vendas.online.dto.ProdutoDTO;

@FeignClient(name = "produto-service", url = "${services.produto.url}", fallback = ProdutoClientFallback.class)
public interface IProdutoClient {

    @GetMapping("/produto/{codigo}")
    ResponseEntity<ProdutoDTO> buscarPorCodigo(@PathVariable String codigo);
}
