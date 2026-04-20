package br.com.renan.vendas.online.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class VendaDTO {

    @NotNull
    private String codigo;

    @NotNull
    private String clienteId;

    @NotEmpty(message = "A venda deve ter ao menos um item")
    private List<ProdutoQuantidade> itens;
}
