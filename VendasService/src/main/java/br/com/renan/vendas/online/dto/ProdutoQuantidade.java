package br.com.renan.vendas.online.dto;

import jakarta.validation.constraints.Min;
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
// cada item da lista que chega no VendaDTO - só código + quantidade, o resto
// (nome, preço) o CadastroVenda busca no ProdutoService na hora de montar o ItemVenda
public class ProdutoQuantidade {

    @NotNull
    private String codigoProduto;

    @NotNull
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;
}
