package br.com.renan.vendas.online.dto;

// só o que a API expõe pra fora - telefone e endereço ficam de fora de propósito
public record ClienteResponseDTO(
    Long id,
    String nome,
    String email,
    String cpf
) {}
