package br.com.renan.vendas.online.usecase;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.repository.IClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CadastroCliente {

	private final IClienteRepository clienteRepository;  // final pra warning

	@Autowired
	public CadastroCliente(IClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public Cliente cadastrar(@Valid Cliente cliente) {
		return clienteRepository.save(cliente);  // ← save() insere novo
	}

	public Cliente atualizar(@Valid Cliente cliente) {
		return clienteRepository.save(cliente);  // ← save() atualiza se ID existe
	}

	public void remover(String id) {
		clienteRepository.deleteById(id);
	}
}
