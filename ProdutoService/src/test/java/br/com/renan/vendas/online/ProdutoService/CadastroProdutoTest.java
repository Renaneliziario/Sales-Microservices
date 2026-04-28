package br.com.renan.vendas.online.ProdutoService;

import br.com.renan.vendas.online.domain.Produto;
import br.com.renan.vendas.online.repository.IProdutoRepository;
import br.com.renan.vendas.online.service.CadastroProduto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroProdutoTest {

    @Mock
    private IProdutoRepository produtoRepository;

    @InjectMocks
    private CadastroProduto cadastroProduto;

    @Test
    void deveCadastrarProdutoComSucesso() {
        Produto produto = Produto.builder().nome("Notebook").valor(BigDecimal.valueOf(3500)).build();
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto resultado = cadastroProduto.cadastrar(produto);

        assertThat(resultado).isEqualTo(produto);
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveAtualizarProdutoComSucesso() {
        Produto produto = Produto.builder().id(1L).nome("Mouse").valor(BigDecimal.valueOf(150)).build();
        when(produtoRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto resultado = cadastroProduto.atualizar(produto);

        assertThat(resultado.getNome()).isEqualTo("Mouse");
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoSemId() {
        Produto produto = Produto.builder().nome("Sem ID").build();

        assertThatThrownBy(() -> cadastroProduto.atualizar(produto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID não pode ser nulo");
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        Produto produto = Produto.builder().id(99L).nome("Ghost").build();
        when(produtoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> cadastroProduto.atualizar(produto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deveRemoverProdutoComSucesso() {
        when(produtoRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> cadastroProduto.remover(1L)).doesNotThrowAnyException();
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoRemoverProdutoInexistente() {
        when(produtoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> cadastroProduto.remover(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
