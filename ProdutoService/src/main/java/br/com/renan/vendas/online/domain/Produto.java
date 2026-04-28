package br.com.renan.vendas.online.domain;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "Produto", description = "Produto")
public class Produto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Identificador único")
        private Long id;

        @NotNull
        @Size(min = 1, max = 50)
        @Column(nullable = false, unique = true, length = 50)
        @Schema(description = "Código", minLength = 1, maxLength = 50, nullable = false)
        private String codigo;

        @NotNull
        @Size(min = 1, max = 50)
        @Column(nullable = false, length = 50)
        @Schema(description = "Nome", minLength = 1, maxLength = 50, nullable = false)
        private String nome;

        @NotNull
        @Size(min = 1, max = 150)
        @Column(nullable = false, length = 150)
        @Schema(description = "Descrição", minLength = 1, maxLength = 150, nullable = false)
        private String descricao;

        @NotNull
        @Column(nullable = false)
        @Schema(description = "Valor", nullable = false)
        private BigDecimal valor;

        @NotNull
        @Min(0)
        @Column(nullable = false)
        @Schema(description = "Quantidade em Estoque", nullable = false)
        private Integer quantidade;
}
