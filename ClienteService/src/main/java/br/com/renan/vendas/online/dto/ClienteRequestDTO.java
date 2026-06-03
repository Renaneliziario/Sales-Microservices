package br.com.renan.vendas.online.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Record para entrada de dados de Cliente (Inbound).
 * Utiliza Bean Validation para garantir a integridade antes de chegar ao banco.
 */
public record ClienteRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    String nome,

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 números")
    String cpf,

    @NotBlank(message = "O telefone é obrigatório")
    String tel,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "O endereço é obrigatório")
    String endereco,

    Integer numero,

    @NotBlank(message = "A cidade é obrigatória")
    String cidade,

    @NotBlank(message = "O estado é obrigatório")
    String estado
) {}
