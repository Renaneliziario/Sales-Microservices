package br.com.renan.vendas.online.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// numero é o único campo sem validação nenhuma aqui embaixo - a entidade tem @NotNull,
// então null só estoura na hora do save (constraint do banco), não como 400 de validação
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
