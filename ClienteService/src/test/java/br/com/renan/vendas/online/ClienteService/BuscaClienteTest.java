package br.com.renan.vendas.online.ClienteService;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.repository.IClienteRepository;
import br.com.renan.vendas.online.usecase.BuscaCliente;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscaClienteTest {

    @Mock
    private IClienteRepository clienteRepository;

    @InjectMocks
    private BuscaCliente buscaCliente;

    @Test
    void deveBuscarClientesComPaginacao() {
        Page<Cliente> pagina = new PageImpl<>(List.of(Cliente.builder().id("1").nome("Ana").build()));
        when(clienteRepository.findAll(any(Pageable.class))).thenReturn(pagina);

        Page<Cliente> resultado = buscaCliente.buscar(Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    void deveBuscarClientePorId() {
        Cliente cliente = Cliente.builder().id("abc").nome("Pedro").build();
        when(clienteRepository.findById("abc")).thenReturn(Optional.of(cliente));

        Cliente resultado = buscaCliente.buscarPorId("abc");

        assertThat(resultado.getNome()).isEqualTo("Pedro");
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscaCliente.buscarPorId("inexistente"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("inexistente");
    }
}
