package br.com.renan.vendas.online.controller;

import br.com.renan.vendas.online.dto.ClienteRequestDTO;
import br.com.renan.vendas.online.dto.ClienteResponseDTO;
import br.com.renan.vendas.online.service.BuscaCliente;
import br.com.renan.vendas.online.service.CadastroCliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClienteController {

    private final BuscaCliente buscaCliente;
    private final CadastroCliente cadastroCliente;

    public ClienteController(BuscaCliente buscaCliente,
                             CadastroCliente cadastroCliente) {
        this.buscaCliente = buscaCliente;
        this.cadastroCliente = cadastroCliente;
    }

    @GetMapping
    @Operation(summary = "Listar clientes paginados")
    public ResponseEntity<Page<ClienteResponseDTO>> buscar(Pageable pageable) {
        return ResponseEntity.ok(buscaCliente.buscar(pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados")
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroCliente.cadastrar(dto));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscaCliente.buscarPorId(id));
    }

    @GetMapping(value = "/is-cadastrado/{id}")
    @Operation(summary = "Verificar se cliente existe pelo ID")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable Long id) {
        return ResponseEntity.ok(buscaCliente.isCadastrado(id));
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para atualização")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.ok(cadastroCliente.atualizar(id, dto));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Remover um cliente")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para remoção")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        cadastroCliente.remover(id);
        return ResponseEntity.noContent().build();
    }
}
