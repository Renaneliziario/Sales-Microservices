package br.com.renan.vendas.online.exception;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// este handler é quem decide o status HTTP quando o CadastroVenda propaga uma falha de
// chamada cross-service. IllegalStateException (lançada manualmente nos catch de FeignException
// dentro do CadastroVenda) vira 503 direto aqui embaixo - não tem relação com o fallback
// do @FeignClient, que hoje não está ativo (falta feign.circuitbreaker.enabled=true)
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationErrorGlobal(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // pega FeignException crua só nos fluxos que não têm try/catch próprio (ex:
    // CadastroVenda.adicionarProduto) - em cadastrar() o try/catch já converte pra
    // IllegalStateException/EntityNotFoundException antes de chegar até aqui
    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<Object> handleFeignException(FeignException ex) {
        if (ex instanceof FeignException.NotFound) {
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
            apiError.setMessage("Recurso não encontrado no serviço externo");
            return buildResponseEntity(apiError);
        }
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE);
        apiError.setMessage("Serviço externo indisponível: " + ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Erro interno: " + ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
