package br.com.renan.vendas.online.resources;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.usecase.BuscaProduto;
import br.com.renan.vendas.online.usecase.CadastroProduto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/produto")
public class ProdutoResource {

    private final BuscaProduto buscaProduto;
    private final CadastroProduto cadastroProduto;

    public ProdutoResource(BuscaProduto buscaProduto,
                           CadastroProduto cadastroProduto) {
        this.buscaProduto = buscaProduto;
        this.cadastroProduto = cadastroProduto;
    }

    @GetMapping
    public ResponseEntity<Page<Produto>> buscar(Pageable pageable){
        return ResponseEntity.ok(buscaProduto.buscar(pageable));
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody @Valid Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroProduto.cadastrar(produto));
    }

    @GetMapping(value = "/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable(value = "codigo") String codigo) {
        return ResponseEntity.ok(buscaProduto.buscarPorCodigo(codigo));
    }

    @GetMapping(value = "/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(buscaProduto.isCadastrado(id));
    }

    @PutMapping
    public ResponseEntity<Produto> atualizar(@RequestBody @Valid Produto produto) {
        return ResponseEntity.ok(cadastroProduto.atualizar(produto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") String id) {
        cadastroProduto.remover(id);
        return ResponseEntity.noContent().build();
    }
}
