package br.com.renan.vendas.online.usecase;

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

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CadastroVenda {

	private final IVendaRepository vendaRepository;
	private final IProdutoClient produtoClient;

	public CadastroVenda(IVendaRepository vendaRepository, IProdutoClient produtoClient) {
		this.vendaRepository = vendaRepository;
		this.produtoClient = produtoClient;
	}

	public Venda cadastrar(@Valid VendaDTO vendaDTO) {
		Venda venda = Venda.builder()
				.codigo(vendaDTO.getCodigo())
				.clienteId(vendaDTO.getClienteId())
				.status(StatusVenda.INICIADA)
				.dataVenda(Instant.now())
				.itens(new ArrayList<>())
				.build();

		vendaDTO.getItens().forEach(itemDTO -> {
			try {
				var response = produtoClient.buscarPorCodigo(itemDTO.getCodigoProduto());
				ProdutoDTO produto = response.getBody();
				if (produto == null) {
					throw new EntityNotFoundException("Produto não encontrado: " + itemDTO.getCodigoProduto());
				}
				ItemVenda item = ItemVenda.builder()
						.produtoId(produto.getId())
						.codigoProduto(produto.getCodigo())
						.nome(produto.getNome())
						.quantidade(itemDTO.getQuantidade())
						.valorUnitario(produto.getValor())
						.build();
				venda.getItens().add(item);
			} catch (FeignException.NotFound e) {
				throw new EntityNotFoundException("Produto não encontrado: " + itemDTO.getCodigoProduto());
			} catch (FeignException e) {
				throw new IllegalStateException("Serviço de produtos indisponível ao buscar: " + itemDTO.getCodigoProduto(), e);
			}
		});

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

	public Venda finalizar(String codigo) {
		Venda venda = vendaRepository.findByCodigo(codigo)
				.orElseThrow(() -> new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo código: " + codigo));
		venda.setStatus(StatusVenda.CONCLUIDA);
		return vendaRepository.save(venda);
	}

	public Venda cancelar(String codigo) {
		Venda venda = vendaRepository.findByCodigo(codigo)
				.orElseThrow(() -> new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo código: " + codigo));
		venda.setStatus(StatusVenda.CANCELADA);
		return vendaRepository.save(venda);
	}

	public Venda atualizar(@Valid Venda venda) {
		if (venda.getId() == null) {
			throw new IllegalArgumentException("ID não pode ser nulo para atualização");
		}
		if (!vendaRepository.existsById(venda.getId())) {
			throw new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo id: " + venda.getId());
		}
		venda.setValorTotal(calcularValorTotal(venda));
		return vendaRepository.save(venda);
	}

	public void remover(String id) {
		if (!vendaRepository.existsById(id)) {
			throw new EntityNotFoundException(Venda.class.getSimpleName() + " não encontrada pelo id: " + id);
		}
		vendaRepository.deleteById(id);
	}
}
