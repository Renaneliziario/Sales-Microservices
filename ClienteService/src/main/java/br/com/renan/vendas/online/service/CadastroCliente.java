package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.dto.ClienteRequestDTO;
import br.com.renan.vendas.online.dto.ClienteResponseDTO;
import br.com.renan.vendas.online.repository.IClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCliente {

    private final IClienteRepository clienteRepository;

    public CadastroCliente(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        // mapeamento na mão mesmo, sem MapStruct - projeto pequeno o suficiente
        // pra não valer a pena a dependência extra
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setTel(dto.tel());
        cliente.setEmail(dto.email());
        cliente.setEndereco(dto.endereco());
        cliente.setNumero(dto.numero());
        cliente.setCidade(dto.cidade());
        cliente.setEstado(dto.estado());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return mapToResponseDTO(clienteSalvo);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado pelo id: " + id));

        // cpf não entra aqui de propósito - depois de cadastrado não muda mais
        clienteExistente.setNome(dto.nome());
        clienteExistente.setTel(dto.tel());
        clienteExistente.setEmail(dto.email());
        clienteExistente.setEndereco(dto.endereco());
        clienteExistente.setNumero(dto.numero());
        clienteExistente.setCidade(dto.cidade());
        clienteExistente.setEstado(dto.estado());

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);

        return mapToResponseDTO(clienteAtualizado);
    }

    // remove sem checar se esse id está referenciado em alguma Venda - não tem como
    // checar isso daqui mesmo (venda mora em outro banco), mas o resultado é que dá
    // pra apagar um cliente que já tem venda registrada, e o clienteId lá vira órfão
    @Transactional
    public void remover(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado pelo id: " + id);
        }
        clienteRepository.deleteById(id);
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
