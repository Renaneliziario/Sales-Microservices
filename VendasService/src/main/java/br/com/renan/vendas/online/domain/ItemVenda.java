package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_venda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // mesma história do clienteId em Venda: aponta pro Produto do ProdutoService, outro banco
    @Column(nullable = false)
    private Long produtoId;

    @Column(nullable = false, length = 50)
    private String codigoProduto;

    // nome e valorUnitario abaixo são snapshot do produto no momento da venda, não
    // referência ao dado atual - se o preço mudar depois, o histórico da venda não muda
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valorUnitario;

    // sem @JsonIgnore aqui o Jackson entra em loop: venda -> itens -> item -> venda -> ...
    // (relação bidirecional, Venda.itens aponta pra cá e esse campo aponta de volta)
    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonIgnore
    private Venda venda;
}
