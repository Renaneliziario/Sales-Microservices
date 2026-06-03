package br.com.renan.vendas.online.ClienteService;

import br.com.renan.vendas.online.domain.Cliente;
import br.com.renan.vendas.online.repository.IClienteRepository;
import br.com.renan.vendas.online.usecase.CadastroCliente;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroClienteTest {

    @Mock
    private IClienteRepository clienteRepository;

    @InjectMocks
    private CadastroCliente cadastroCliente;

    @Test
    void deveCadastrarClienteComSucesso() {
        Cliente cliente = Cliente.builder().nome("João Silva").build();
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = cadastroCliente.cadastrar(cliente);

        assertThat(resultado).isEqualTo(cliente);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Cliente cliente = Cliente.builder().id("abc123").nome("Maria").build();
        when(clienteRepository.existsById("abc123")).thenReturn(true);
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = cadastroCliente.atualizar(cliente);

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteSemId() {
        Cliente cliente = Cliente.builder().nome("Sem ID").build();

        assertThatThrownBy(() -> cadastroCliente.atualizar(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID não pode ser nulo");
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        Cliente cliente = Cliente.builder().id("inexistente").nome("Ninguém").build();
        when(clienteRepository.existsById("inexistente")).thenReturn(false);

        assertThatThrownBy(() -> cadastroCliente.atualizar(cliente))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deveRemoverClienteComSucesso() {
        when(clienteRepository.existsById("abc123")).thenReturn(true);

        assertThatCode(() -> cadastroCliente.remover("abc123")).doesNotThrowAnyException();
        verify(clienteRepository).deleteById("abc123");
    }

    @Test
    void deveLancarExcecaoAoRemoverClienteInexistente() {
        when(clienteRepository.existsById("inexistente")).thenReturn(false);

        assertThatThrownBy(() -> cadastroCliente.remover("inexistente"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
