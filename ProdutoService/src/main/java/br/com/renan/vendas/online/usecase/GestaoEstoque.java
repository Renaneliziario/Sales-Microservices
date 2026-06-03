package br.com.renan.vendas.online.usecase;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class GestaoEstoque {

    private final IProdutoRepository produtoRepository;

    public GestaoEstoque(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void baixarEstoque(String codigo, Integer quantidade) {
        Produto produto = produtoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + codigo));

        if (produto.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + codigo);
        }

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);
    }

    public void reporEstoque(String codigo, Integer quantidade) {
        Produto produto = produtoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + codigo));

        produto.setQuantidade(produto.getQuantidade() + quantidade);
        produtoRepository.save(produto);
    }
}
