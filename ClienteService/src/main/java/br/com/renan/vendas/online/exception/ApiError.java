package br.com.renan.vendas.online.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// terceiro modelo de erro diferente no projeto - Produto e Vendas usam uma versão mais
// rica (subErrors polimórfico, vários construtores), esse aqui é mais simples
// (Map<String,String> pra validação). os três foram escritos em momentos diferentes,
// nenhum reaproveita o outro
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // sem isso todo erro simples viria com "errors": null no corpo
public class ApiError {

    private int status;
    private String message;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Map<String, String> errors;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public void addValidationError(String field, String message) {
        if (this.errors == null) {
            this.errors = new HashMap<>();
        }
        this.errors.put(field, message);
    }
}
