package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

// mesma observação do CadastroProduto: @Validated sem constraint em parâmetro solto
// não valida nada sozinho - se quiser travar quantidade negativa direto na assinatura,
// precisa de @Positive Integer quantidade nos métodos abaixo
@Service
@Validated
public class GestaoEstoque {

    private final IProdutoRepository produtoRepository;

    public GestaoEstoque(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // check-then-act sem lock - duas baixas concorrentes no mesmo produto podem passar
    // as duas na checagem antes de qualquer uma salvar, e o estoque fica negativo.
    // pra corrigir de verdade precisaria de @Lock(PESSIMISTIC_WRITE) no repository
    // ou uma coluna de versão (@Version, lock otimista)
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
