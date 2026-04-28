package br.com.renan.vendas.online.service;

import br.com.renan.vendas.online.client.IClienteClient;
import br.com.renan.vendas.online.client.IProdutoClient;
import br.com.renan.vendas.online.domain.ItemVenda;
import br.com.renan.vendas.online.domain.StatusVenda;
import br.com.renan.vendas.online.domain.Venda;
import br.com.renan.vendas.online.dto.ProdutoDTO;
import br.com.renan.vendas.online.dto.VendaDTO;
import br.com.renan.vendas.online.repository.IVendaRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CadastroVenda {

        private final IVendaRepository vendaRepository;
        private final IProdutoClient produtoClient;
        private final IClienteClient clienteClient;

        public CadastroVenda(IVendaRepository vendaRepository, 
                        IProdutoClient produtoClient,
                        IClienteClient clienteClient) {
                this.vendaRepository = vendaRepository;
                this.produtoClient = produtoClient;
                this.clienteClient = clienteClient;
        }

        @Transactional
        public Venda cadastrar(@Valid VendaDTO vendaDTO) {
                Venda venda = Venda.builder()
                                .codigo(vendaDTO.getCodigo())
                                .clienteId(vendaDTO.getClienteId())
                                .status(StatusVenda.INICIADA)
                                .dataVenda(Instant.now())
                                .itens(new ArrayList<>())
                                .build();

                // 1. Validar Cliente
                try {
                        Boolean clienteCadastrado = clienteClient.isCadastrado(vendaDTO.getClienteId()).getBody();
                        if (Boolean.FALSE.equals(clienteCadastrado)) {
                                throw new EntityNotFoundException("Cliente não encontrado: " + vendaDTO.getClienteId());
                        }
                } catch (FeignException.NotFound e) {
                        throw new EntityNotFoundException("Cliente não encontrado: " + vendaDTO.getClienteId());
                } catch (FeignException e) {
                        throw new IllegalStateException("Serviço de clientes indisponível", e);
                }

                // 2. Processar Itens com Reserva de Estoque
                List<ItemVenda> itensProcessados = new ArrayList<>();

                try {
                        for (var itemDTO : vendaDTO.getItens()) {
                                // Busca Produto
                                ProdutoDTO produto;
                                try {
                                        var response = produtoClient.buscarPorCodigo(itemDTO.getCodigoProduto());
                                        produto = response.getBody();
                                } catch (FeignException.NotFound e) {
                                        throw new EntityNotFoundException("Produto não encontrado: " + itemDTO.getCodigoProduto());
                                } catch (FeignException e) {
                                        throw new IllegalStateException("Serviço de produtos indisponível", e);
                                }

                                if (produto == null) {
                                        throw new EntityNotFoundException("Produto não encontrado: " + itemDTO.getCodigoProduto());
                                }

                                // Baixa Estoque
                                try {
                                        produtoClient.baixarEstoque(itemDTO.getCodigoProduto(), itemDTO.getQuantidade());
                                } catch (FeignException e) {
                                        throw new IllegalStateException("Erro ao baixar estoque do produto: " + itemDTO.getCodigoProduto() + ". Verifique se há saldo suficiente.", e);
                                }

                                ItemVenda item = ItemVenda.builder()
                                                .produtoId(produto.getId())
                                                .codigoProduto(produto.getCodigo())
                                                .nome(produto.getNome())
                                                .quantidade(itemDTO.getQuantidade())
                                                .valorUnitario(produto.getValor())
                                                .venda(venda) // Associar venda
                                                .build();

                                itensProcessados.add(item);
                                venda.getItens().add(item);
                        }
                } catch (Exception e) {
                        // Rollback: Estornar estoque dos itens já processados
                        itensProcessados.forEach(item -> {
                                try {
                                        produtoClient.reporEstoque(item.getCodigoProduto(), item.getQuantidade());
                                } catch (Exception ex) {
                                        System.err.println("ERRO GRAVE: Falha ao estornar estoque do produto " + item.getCodigoProduto());
                                }
                        });
                        throw e;
                }

                if (venda.getItens().isEmpty()) {
                        throw new IllegalStateException("Nenhum item válido foi adicionado à venda");
                }

                venda.setValorTotal(calcularValorTotal(venda));
                return vendaRepository.save(venda);
        }

        private BigDecimal calcularValorTotal(Venda venda) {
                if (venda.getItens() == null || venda.getItens().isEmpty()) {
                        return BigDecimal.ZERO;
                }
                return venda.getItens().stream()
                                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Transactional
        public Venda finalizar(String codigo) {
                Venda venda = vendaRepository.findByCodigo(codigo)
                                .orElseThrow(() -> new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo código: " + codigo));
                venda.setStatus(StatusVenda.CONCLUIDA);
                return vendaRepository.save(venda);
        }

        @Transactional
        public Venda cancelar(String codigo) {
                Venda venda = vendaRepository.findByCodigo(codigo)
                                .orElseThrow(() -> new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo código: " + codigo));

                // Estornar estoque ao cancelar
                venda.getItens().forEach(item -> {
                        try {
                                produtoClient.reporEstoque(item.getCodigoProduto(), item.getQuantidade());
                        } catch (Exception e) {
                                System.err.println("ERRO: Falha ao estornar estoque no cancelamento da venda " + codigo + ", produto: " + item.getCodigoProduto());
                        }
                });

                venda.setStatus(StatusVenda.CANCELADA);
                return vendaRepository.save(venda);
        }

        @Transactional
        public Venda atualizar(@Valid Venda venda) {
                if (venda.getId() == null) {
                        throw new IllegalArgumentException("ID não pode ser nulo para atualização");
                }
                if (!vendaRepository.existsById(venda.getId())) {
                        throw new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo id: " + venda.getId());
                }
                // Garantir associação nos itens na atualização manual
                if (venda.getItens() != null) {
                    venda.getItens().forEach(item -> item.setVenda(venda));
                }
                venda.setValorTotal(calcularValorTotal(venda));
                return vendaRepository.save(venda);
        }

        @Transactional
        public void remover(Long id) {
                if (!vendaRepository.existsById(id)) {
                        throw new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo id: " + id);
                }
                vendaRepository.deleteById(id);
        }
    
    @Transactional
    public Venda adicionarProduto(Long id, String codigoProduto, Integer quantidade) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada: " + id));
        
        if (venda.getStatus() != StatusVenda.INICIADA) {
             throw new IllegalStateException("Não é possível adicionar produtos a uma venda com status: " + venda.getStatus());
        }

        // Busca Produto
        ProdutoDTO produto;
        try {
            var response = produtoClient.buscarPorCodigo(codigoProduto);
            produto = response.getBody();
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Produto não encontrado: " + codigoProduto);
        }

        // Baixa Estoque
        produtoClient.baixarEstoque(codigoProduto, quantidade);

        ItemVenda item = ItemVenda.builder()
                .produtoId(produto.getId())
                .codigoProduto(produto.getCodigo())
                .nome(produto.getNome())
                .quantidade(quantidade)
                .valorUnitario(produto.getValor())
                .venda(venda)
                .build();
        
        venda.getItens().add(item);
        venda.setValorTotal(calcularValorTotal(venda));
        return vendaRepository.save(venda);
    }
}
