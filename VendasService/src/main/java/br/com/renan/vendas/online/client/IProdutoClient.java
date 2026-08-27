package br.com.renan.vendas.online.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.renan.vendas.online.dto.ProdutoDTO;

// mesma situação do IClienteClient: fallback existe e está registrado, mas
// feign.circuitbreaker.enabled não está ligado em nenhum profile, então nunca dispara -
// quem trata a falha de verdade é o try/catch manual dentro do CadastroVenda
@FeignClient(name = "produto-service", url = "${services.produto.url}", fallback = ProdutoClientFallback.class)
public interface IProdutoClient {

    @GetMapping("/produto/{codigo}")
    ResponseEntity<ProdutoDTO> buscarPorCodigo(@PathVariable String codigo);

    @PostMapping("/produto/{codigo}/estoque/baixa")
    ResponseEntity<Void> baixarEstoque(@PathVariable("codigo") String codigo, @RequestBody Integer quantidade);

    @PostMapping("/produto/{codigo}/estoque/reposicao")
    ResponseEntity<Void> reporEstoque(@PathVariable("codigo") String codigo, @RequestBody Integer quantidade);
}
