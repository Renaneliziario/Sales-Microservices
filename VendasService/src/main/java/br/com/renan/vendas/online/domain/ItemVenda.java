package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemVenda {
    private String produtoId;
    private String codigoProduto;
    private String nome;
    private Integer quantidade;
    private BigDecimal valorUnitario;
}
