package br.com.renan.vendas.online.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// espelho local do Produto do ProdutoService, é o que volta no corpo do IProdutoClient.
// não tem um jar compartilhado entre os serviços pra isso - cada um mantém sua própria
// cópia do contrato, então se o Produto real ganhar campo novo isso aqui não acompanha sozinho
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoDTO {
    private Long id;
    private String codigo;
    private String nome;
    private BigDecimal valor;
}
