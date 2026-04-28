package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CadastroProduto {

	private final IProdutoRepository produtoRepository;

	public CadastroProduto(IProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	public Produto cadastrar(@Valid Produto produto) {
		return produtoRepository.save(produto);
	}

	public Produto atualizar(@Valid Produto produto) {
		if (produto.getId() == null) {
			throw new IllegalArgumentException("ID não pode ser nulo para atualização");
		}
		if (!produtoRepository.existsById(produto.getId())) {
			throw new EntityNotFoundException(Produto.class.getSimpleName() + " não encontrado pelo id: " + produto.getId());
		}
		return produtoRepository.save(produto);
	}

	public void remover(Long id) {
		if (!produtoRepository.existsById(id)) {
			throw new EntityNotFoundException(Produto.class.getSimpleName() + " não encontrado pelo id: " + id);
		}
		produtoRepository.deleteById(id);
	}
}
