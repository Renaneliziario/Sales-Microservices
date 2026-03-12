package br.com.renan.vendas.online.resources;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.usecase.BuscaCliente;
import br.com.renan.vendas.online.usecase.CadastroCliente;
import org.hibernate.internal.util.collections.AbstractPagedArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
