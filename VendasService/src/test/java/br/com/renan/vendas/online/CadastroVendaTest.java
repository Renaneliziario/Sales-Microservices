package br.com.renan.vendas.online;

import br.com.renan.vendas.online.client.IClienteClient;
import br.com.renan.vendas.online.client.IProdutoClient;
import br.com.renan.vendas.online.domain.StatusVenda;
import br.com.renan.vendas.online.domain.Venda;
import br.com.renan.vendas.online.dto.ProdutoDTO;
import br.com.renan.vendas.online.dto.ProdutoQuantidade;
import br.com.renan.vendas.online.dto.VendaDTO;
import br.com.renan.vendas.online.repository.IVendaRepository;
import br.com.renan.vendas.online.service.CadastroVenda;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroVendaTest {

    @Mock
    private IVendaRepository vendaRepository;

    @Mock
    private IProdutoClient produtoClient;

    @Mock
    private IClienteClient clienteClient;

    @InjectMocks
    private CadastroVenda cadastroVenda;

    @Test
    void deveCadastrarVendaComSucesso() {
        ProdutoDTO produto = ProdutoDTO.builder()
                .id(1L).codigo("COD01").nome("Teclado").valor(BigDecimal.valueOf(200)).build();

        VendaDTO vendaDTO = VendaDTO.builder()
                .codigo("V001")
                .clienteId(1L)
                .itens(List.of(ProdutoQuantidade.builder().codigoProduto("COD01").quantidade(2).build()))
                .build();

        when(clienteClient.isCadastrado(1L)).thenReturn(ResponseEntity.ok(true));
        when(produtoClient.buscarPorCodigo("COD01")).thenReturn(ResponseEntity.ok(produto));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

        Venda resultado = cadastroVenda.cadastrar(vendaDTO);

        assertThat(resultado.getCodigo()).isEqualTo("V001");
        assertThat(resultado.getStatus()).isEqualTo(StatusVenda.INICIADA);
        assertThat(resultado.getItens()).hasSize(1);
        assertThat(resultado.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(400));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        VendaDTO vendaDTO = VendaDTO.builder()
                .codigo("V002")
                .clienteId(1L)
                .itens(List.of(ProdutoQuantidade.builder().codigoProduto("INVALIDO").quantidade(1).build()))
                .build();

        when(clienteClient.isCadastrado(1L)).thenReturn(ResponseEntity.ok(true));
        when(produtoClient.buscarPorCodigo("INVALIDO"))
                .thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> cadastroVenda.cadastrar(vendaDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("INVALIDO");
    }

    @Test
    void deveFinalizarVendaComSucesso() {
        Venda venda = Venda.builder().id(1L).codigo("V001").status(StatusVenda.INICIADA).build();
        when(vendaRepository.findByCodigo("V001")).thenReturn(Optional.of(venda));
        when(vendaRepository.save(venda)).thenReturn(venda);

        Venda resultado = cadastroVenda.finalizar("V001");

        assertThat(resultado.getStatus()).isEqualTo(StatusVenda.CONCLUIDA);
    }

    @Test
    void deveCancelarVendaComSucesso() {
        Venda venda = Venda.builder()
                .id(1L)
                .codigo("V001")
                .status(StatusVenda.INICIADA)
                .itens(new java.util.ArrayList<>())
                .build();
        when(vendaRepository.findByCodigo("V001")).thenReturn(Optional.of(venda));
        when(vendaRepository.save(venda)).thenReturn(venda);

        Venda resultado = cadastroVenda.cancelar("V001");

        assertThat(resultado.getStatus()).isEqualTo(StatusVenda.CANCELADA);
    }

    @Test
    void deveRemoverVendaComSucesso() {
        when(vendaRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> cadastroVenda.remover(1L)).doesNotThrowAnyException();
        verify(vendaRepository).deleteById(1L);
    }
}
