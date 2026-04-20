package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "venda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name="Venda", description="Venda")
public class Venda {

	@Id
	@Schema(description="Identificador único")
	private String id;

	@Schema(description="Código da venda")
	private String codigo;

	@Schema(description="ID do cliente")
	private String clienteId;

	@Schema(description="Lista de itens da venda")
	private List<ItemVenda> itens;

	@Schema(description="Valor total da venda")
	private BigDecimal valorTotal;

	@Schema(description="Data da realização da venda")
	private Instant dataVenda;

	@Schema(description="Status da venda")
	private StatusVenda status;
}
