package br.com.renan.vendas.online.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.service.BuscaProduto;
import br.com.renan.vendas.online.service.CadastroProduto;
import br.com.renan.vendas.online.service.GestaoEstoque;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/produto")
public class ProdutoResource {

    private final BuscaProduto buscaProduto;
    private final CadastroProduto cadastroProduto;
    private final GestaoEstoque gestaoEstoque;

    public ProdutoResource(BuscaProduto buscaProduto,
                           CadastroProduto cadastroProduto,
                           GestaoEstoque gestaoEstoque) {
        this.buscaProduto = buscaProduto;
        this.cadastroProduto = cadastroProduto;
        this.gestaoEstoque = gestaoEstoque;
    }

    @GetMapping
    public ResponseEntity<Page<Produto>> buscar(Pageable pageable) {
        return ResponseEntity.ok(buscaProduto.buscar(pageable));
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody @Valid Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cadastroProduto.cadastrar(produto));
    }

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(buscaProduto.buscarPorId(id));
    }

    @GetMapping(value = "/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(buscaProduto.buscarPorCodigo(codigo));
    }

    @GetMapping(value = "/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable String id) {
        return ResponseEntity.ok(buscaProduto.isCadastrado(id));
    }

    @PutMapping
    public ResponseEntity<Produto> atualizar(@RequestBody @Valid Produto produto) {
        return ResponseEntity.ok(cadastroProduto.atualizar(produto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        cadastroProduto.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{codigo}/estoque/baixa")
    public ResponseEntity<Void> baixarEstoque(@PathVariable String codigo, @RequestBody Integer quantidade) {
        gestaoEstoque.baixarEstoque(codigo, quantidade);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{codigo}/estoque/reposicao")
    public ResponseEntity<Void> reporEstoque(@PathVariable String codigo, @RequestBody Integer quantidade) {
        gestaoEstoque.reporEstoque(codigo, quantidade);
        return ResponseEntity.ok().build();
    }
}
