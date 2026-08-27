package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.dto.ClienteResponseDTO;
import br.com.renan.vendas.online.repository.IClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // Melhora a performance em operações de apenas leitura
public class BuscaCliente {

    private final IClienteRepository clienteRepository;

    public BuscaCliente(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Page<ClienteResponseDTO> buscar(Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(this::mapToResponseDTO);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado pelo id: " + id));
        return mapToResponseDTO(cliente);
    }

    // endpoint enxuto, só existência - é o que o VendasService chama via Feign,
    // não faz sentido trafegar o cliente inteiro só pra validar se ele existe
    public Boolean isCadastrado(Long id) {
        return clienteRepository.existsById(id);
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getCpf()
        );
    }
}
