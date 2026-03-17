package br.com.renan.vendas.online.usecase;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.repository.IClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CadastroCliente {

	private final IClienteRepository clienteRepository;

	public CadastroCliente(IClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public Cliente cadastrar(@Valid Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	public Cliente atualizar(@Valid Cliente cliente) {
		if (cliente.getId() == null) {
			throw new IllegalArgumentException("ID não pode ser nulo para atualização");
		}
		if (!clienteRepository.existsById(cliente.getId())) {
			throw new EntityNotFoundException(Cliente.class.getSimpleName() + " não encontrado pelo id: " + cliente.getId());
		}
		return clienteRepository.save(cliente);
	}

	public void remover(String id) {
		if (!clienteRepository.existsById(id)) {
			throw new EntityNotFoundException(Cliente.class.getSimpleName() + " não encontrado pelo id: " + id);
		}
		clienteRepository.deleteById(id);
	}
}
