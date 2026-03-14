package br.com.renan.vendas.online.resources;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.usecase.BuscaCliente;
import br.com.renan.vendas.online.usecase.CadastroCliente;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/cliente")
public class ClienteResource {

    private final BuscaCliente buscaCliente;
    private final CadastroCliente cadastroCliente;

    public ClienteResource(BuscaCliente buscaCliente,
                           CadastroCliente cadastroCliente) {
        this.buscaCliente = buscaCliente;
        this.cadastroCliente = cadastroCliente;
    }

    @GetMapping
    public ResponseEntity<Page<Cliente>> buscar(Pageable pageable){
        return ResponseEntity.ok(buscaCliente.buscar(pageable));
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody @Valid Cliente cliente) {
        return ResponseEntity.ok(cadastroCliente.cadastrar(cliente));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(buscaCliente.buscarPorId(id));
    }

    @GetMapping(value = "/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(buscaCliente.isCadastrado(id));
    }

    @PutMapping
    public ResponseEntity<Cliente> atualizar(@RequestBody @Valid Cliente cliente) {
        return ResponseEntity.ok(cadastroCliente.atualizar(cliente));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") String id) {
        cadastroCliente.remover(id);
        return ResponseEntity.noContent().build();
    }
}
