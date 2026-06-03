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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class FluxoVendaIntegrationTest {

    @Autowired
    private CadastroVenda cadastroVenda;

    @Autowired
    private IVendaRepository vendaRepository;

    @MockBean
    private IClienteClient clienteClient;

    @MockBean
    private IProdutoClient produtoClient;

    @BeforeEach
    void setup() {
        vendaRepository.deleteAll();
    }

    @Test
    void deveRealizarFluxoCompletoDeVendaComSucesso() {
        // GIVEN
        Long clienteId = 1L;
        String codigoProduto = "PROD123";
        
        // Simular Cliente Cadastrado
        when(clienteClient.isCadastrado(clienteId))
                .thenReturn(ResponseEntity.ok(true));

        // Simular Produto existente
        ProdutoDTO produtoDTO = ProdutoDTO.builder()
                .id(10L)
                .codigo(codigoProduto)
                .nome("Produto Teste")
                .valor(BigDecimal.valueOf(100.0))
                .build();
        
        when(produtoClient.buscarPorCodigo(codigoProduto))
                .thenReturn(ResponseEntity.ok(produtoDTO));

        // Simular Baixa de Estoque
        when(produtoClient.baixarEstoque(anyString(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        VendaDTO vendaDTO = VendaDTO.builder()
                .codigo("VENDA-001")
                .clienteId(clienteId)
                .itens(List.of(
                        ProdutoQuantidade.builder()
                                .codigoProduto(codigoProduto)
                                .quantidade(2)
                                .build()
                ))
                .build();

        // WHEN
        Venda vendaSalva = cadastroVenda.cadastrar(vendaDTO);

        // THEN
        assertThat(vendaSalva.getId()).isNotNull();
        assertThat(vendaSalva.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(200.0));
        assertThat(vendaSalva.getStatus()).isEqualTo(StatusVenda.INICIADA);
        assertThat(vendaSalva.getItens()).hasSize(1);
        
        // Verificar persistência no banco
        Venda vendaNoBanco = vendaRepository.findById(vendaSalva.getId()).orElse(null);
        assertThat(vendaNoBanco).isNotNull();
        assertThat(vendaNoBanco.getCodigo()).isEqualTo("VENDA-001");
    }
}
