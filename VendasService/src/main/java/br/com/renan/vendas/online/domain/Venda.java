package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "venda", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name="Venda", description="Venda")
public class Venda {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description="Identificador único")
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        @Schema(description="Código da venda")
        private String codigo;

        @Column(nullable = false)
        @Schema(description="ID do cliente")
        private Long clienteId;

        @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
        @Schema(description="Lista de itens da venda")
        private List<ItemVenda> itens;

        @Column(nullable = false)
        @Schema(description="Valor total da venda")
        private BigDecimal valorTotal;

        @Column(nullable = false)
        @Schema(description="Data da realização da venda")
        private Instant dataVenda;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        @Schema(description="Status da venda")
        private StatusVenda status;
}
