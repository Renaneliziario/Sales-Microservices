package br.com.renan.vendas.online.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ApiValidationError extends ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    // construtor curto pra erro de objeto inteiro (global error), sem campo/valor
    // específico envolvido - o de 4 argumentos (Lombok) cobre o caso normal por campo
    ApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
