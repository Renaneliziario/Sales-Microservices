package br.com.renan.vendas.online.usecase;

import br.com.renan.vendas.online.domain.Venda;
import br.com.renan.vendas.online.repository.IVendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;  // ← CORREÇÃO: Spring Data
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BuscaVenda {

	private final IVendaRepository vendaRepository;

	public BuscaVenda(IVendaRepository vendaRepository) {
		this.vendaRepository = vendaRepository;
	}

	public Page<Venda> buscar(Pageable pageable) {  // ← Pageable correto
		return vendaRepository.findAll(pageable);
	}

	public Venda buscarPorId(String id) {
		return vendaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrado pelo id: " + id));
	}

	public Boolean isCadastrado(String id) {
		return vendaRepository.existsById(id);
	}

}
