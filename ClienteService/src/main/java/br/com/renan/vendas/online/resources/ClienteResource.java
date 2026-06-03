package br.com.renan.vendas.online.resources;

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

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.usecase.BuscaCliente;
import br.com.renan.vendas.online.usecase.CadastroCliente;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/**
 * Classe que representa a recursos de clientes.
 */
@RestController
@RequestMapping(value = "/cliente")
public class ClienteResource {

    private final BuscaCliente buscaCliente;
    private final CadastroCliente cadastroCliente;

    /**
     * Construtor da classe, onde é injetado as dependências.
     *
     * @param buscaCliente Instância do uso de caso de busca de clientes.
     * @param cadastroCliente Instância do uso de caso de cadastro de clientes.
     */
    public ClienteResource(BuscaCliente buscaCliente,
                           CadastroCliente cadastroCliente) {
        this.buscaCliente = buscaCliente;
        this.cadastroCliente = cadastroCliente;
    }

    /**
     * Busca clientes paginados.
     *
     * @param pageable Paginação para buscar os clientes.
     * @return Lista de clientes paginados.
     */
    @GetMapping
    public ResponseEntity<Page<Cliente>> buscar(Pageable pageable){
        // Exemplo de JSON retornado:
        //{
        //    "content": [
        //        {
        //            "id": 1,
        //            "nome": "João",
        //            "email": "joao@example.com"
        //        },
        //        {
        //            "id": 2,
        //            "nome": "Maria",
        //            "email": "maria@example.com"
        //        }
        //    ],
        //    "pageNumber": 0,
        //    "pageSize": 10,
        //    "totalPages": 1,
        //    "totalElements": 2
        //}
        return ResponseEntity.ok(buscaCliente.buscar(pageable));
    }

    /**
     * Cadastra um novo cliente.
     *
     * @param cliente Instância do cliente a ser cadastrado.
     * @return Instância do cliente cadastrado.
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente",
                description = "Inserir novos dados de cliente.",
                tags = {"Clientes"})
    public ResponseEntity<Cliente> cadastrar(@RequestBody @Valid Cliente cliente) {
        // Exemplo de JSON que deve ser enviado:
        //{
        //    "nome": "João",
        //    "email": "joao@example.com"
        //}
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroCliente.cadastrar(cliente));
    }

    /**
     * Busca um cliente pelo seu ID.
     *
     * @param id Id do cliente a ser buscado.
     * @return Instância do cliente com o ID fornecido.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable(value = "id") String id) {
        // Exemplo de JSON retornado:
        //{
        //    "id": 1,
        //    "nome": "João",
        //    "email": "joao@example.com"
        //}
        return ResponseEntity.ok(buscaCliente.buscarPorId(id));
    }

    /**
     * Verifica se um cliente está cadastrado pelo seu ID.
     *
     * @param id Id do cliente a ser verificado.
     * @return True se o cliente estiver cadastrado, false caso contrário.
     */
    @GetMapping(value = "/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable(value = "id") String id) {
        // Exemplo de JSON retornado:
        //{
        //    "cadastrado": true
        //}
        return ResponseEntity.ok(buscaCliente.isCadastrado(id));
    }

    /**
     * Atualiza um cliente com o ID fornecido.
     *
     * @param cliente Instância do cliente a ser atualizado.
     * @return Instância do cliente atualizada.
     */
    @PutMapping
    public ResponseEntity<Cliente> atualizar(@RequestBody @Valid Cliente cliente) {
        // Exemplo de JSON que deve ser enviado:
        //{
        //    "nome": "João",
        //    "email": "joao@example.com"
        //}
        return ResponseEntity.ok(cadastroCliente.atualizar(cliente));
    }

    /**
     * Remove um cliente com o ID fornecido.
     *
     * @param id Id do cliente a ser removido.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") String id) {
        cadastroCliente.remover(id);
        return ResponseEntity.noContent().build();
    }
}
