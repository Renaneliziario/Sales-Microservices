package br.com.renan.vendas.online.usecase;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.repository.IClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;  // ← CORREÇÃO: Spring Data
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BuscaCliente {

	private final IClienteRepository clienteRepository;  // final pra warning

	@Autowired
	public BuscaCliente(IClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public Page<Cliente> buscar(Pageable pageable) {  // ← Pageable correto
		return clienteRepository.findAll(pageable);
	}

	public Cliente buscarPorId(String id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Cliente.class.getSimpleName() + " não encontrado pelo id: " + id));
	}

	public Boolean isCadastrado(String id) {
		Optional<Cliente> cli
