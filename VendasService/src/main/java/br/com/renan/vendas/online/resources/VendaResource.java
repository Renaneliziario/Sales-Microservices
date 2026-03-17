package br.com.renan.vendas.online.resources;

import br.com.renan.vendas.online.domain.Venda;
import br.com.renan.vendas.online.usecase.BuscaVenda;
import br.com.renan.vendas.online.usecase.CadastroVenda;
import br.com.renan.vendas.online.dto.VendaDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/venda")
public class VendaResource {

    private final BuscaVenda buscaVenda;
    private final CadastroVenda cadastroVenda;

    public VendaResource(BuscaVenda buscaVenda,
                           CadastroVenda cadastroVenda) {
        this.buscaVenda = buscaVenda;
        this.cadastroVenda = cadastroVenda;
    }

    @GetMapping
    public ResponseEntity<Page<Venda>> buscar(Pageable pageable){
        return ResponseEntity.ok(buscaVenda.buscar(pageable));
    }

    @PostMapping
    public ResponseEntity<Venda> cadastrar(@RequestBody @Valid VendaDTO vendaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroVenda.cadastrar(vendaDTO));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(buscaVenda.buscarPorId(id));
    }

    @GetMapping(value = "/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(buscaVenda.isCadastrado(id));
    }

    @PutMapping(value = "/{codigo}/finalizar")
    public ResponseEntity<Venda> finalizar(@PathVariable(value = "codigo") String codigo) {
        return ResponseEntity.ok(cadastroVenda.finalizar(codigo));
    }

    @PutMapping(value = "/{codigo}/cancelar")
    public ResponseEntity<Venda> cancelar(@PathVariable(value = "codigo") String codigo) {
        return ResponseEntity.ok(cadastroVenda.cancelar(codigo));
    }

    @PutMapping
    public ResponseEntity<Venda> atualizar(@RequestBody @Valid Venda venda) {
        return ResponseEntity.ok(cadastroVenda.atualizar(venda));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") String id) {
        cadastroVenda.remover(id);
        return ResponseEntity.noContent().build();
    }
}
