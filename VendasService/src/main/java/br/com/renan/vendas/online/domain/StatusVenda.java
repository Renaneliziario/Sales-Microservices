package br.com.renan.vendas.online.domain;

// toda venda nasce INICIADA (CadastroVenda.cadastrar). CONCLUIDA e CANCELADA só chegam
// via /finalizar e /cancelar - não tem transição automática nem validação impedindo
// pular estado (ex: cancelar uma venda já concluída passa direto, sem checagem)
public enum StatusVenda {
    INICIADA, CONCLUIDA, CANCELADA;
}
