package br.com.renan.vendas.online.dto;

/**
 * Record para saída de dados de Cliente (Outbound).
 * Isola a entidade JPA e decide o que o mundo externo pode ver.
 */
public record ClienteResponseDTO(
    Long id,
    String nome,
    String email,
    String cpf
) {}
