package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;
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

    @Column(nullable = false)
    private Long produtoId;

    @Column(nullable = false, length = 50)
    private String codigoProduto;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valorUnitario;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;
}
