package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BuscaProduto {

	private final IProdutoRepository produtoRepository;

	public BuscaProduto(IProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	public Page<Produto> buscar(Pageable pageable) {  // ← Pageable correto
		return produtoRepository.findAll(pageable);
	}

	public Produto buscarPorId(Long id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Produto.class.getSimpleName() + " não encontrado pelo id: " + id));
	}

	public Produto buscarPorCodigo(String codigo) {
		return produtoRepository.findByCodigo(codigo)
				.orElseThrow(() -> new EntityNotFoundException(Produto.class.getSimpleName() + " não encontrado pelo código: " + codigo));
	}

	public Boolean isCadastrado(Long id) {
		return produtoRepository.existsById(id);
	}

}
