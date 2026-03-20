# Arquitetura — Sales-Microservices

## Visão Geral

Ecossistema de microserviços para gestão comercial (Clientes, Produtos, Vendas), construído com Spring Boot 3.4 e Spring Cloud. Cada serviço é independente, com sua própria base de dados e ciclo de deploy — comunicando-se via APIs REST e compartilhando configurações através de um Config Server centralizado.

> **Status:** Projeto finalizado. Todos os serviços implementados com CRUD completo, testes unitários e comunicação resiliente entre serviços.

---

## Mapa de Serviços

```
Sales-Microservices/
│
├── ConfigServer/       → Gerenciamento centralizado de configurações (Spring Cloud Config)
├── ClienteService/     → CRUD de clientes com persistência MongoDB  ✅
├── ProdutoService/     → CRUD de produtos com persistência MongoDB  ✅
└── VendasService/      → Orquestração de vendas, comunica com ProdutoService via Feign  ✅
```

---

## Arquitetura de Alto Nível

```
                    ┌─────────────────┐
                    │   Config Server  │  ← Porta 8888
                    │  (Spring Cloud)  │  ← Lê configs do classpath
                    └────────┬────────┘
                             │ fornece configurações na inicialização
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ClienteService│  │ProdutoService│  │VendasService │
    │  Porta 8081  │  │  Porta 8082  │  │  Porta 8083  │
    └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
           │                 │                 │
           ▼                 ▼        Feign Client (HTTP)
       MongoDB            MongoDB     ├── valida cliente → ClienteService
                                      ├── busca produto → ProdutoService
                                      ├── baixa estoque → ProdutoService
                                      └── repõe estoque (rollback) → ProdutoService
```

---

## Decisões de Arquitetura

### 1. Por que microserviços?

O objetivo principal deste projeto é **aprendizado de arquitetura distribuída**, não resolver um problema de escala real. A separação em serviços demonstra na prática:

- Como serviços se comunicam via HTTP/REST
- Como configurações são gerenciadas de forma centralizada
- Como o Docker permite subir a infraestrutura local (MongoDB) com um único comando
- Como tratar falhas de comunicação entre serviços sem derrubar o sistema inteiro

---

### 2. Por que Spring Cloud Config Server?

Em uma arquitetura monolítica, há um único `application.properties`. Com múltiplos serviços, cada um teria suas próprias configurações — e alterar a URL de um banco exigiria mudança em múltiplos repositórios com redeploy de cada serviço.

O Config Server resolve isso centralizando todas as propriedades:

```
ConfigServer (porta 8888)
    ├── serve: cliente-service.yaml  → configurações do ClienteService
    ├── serve: produto-service.yaml  → configurações do ProdutoService
    └── serve: venda-service.yaml    → configurações do VendasService
```

Cada serviço ao iniciar busca suas configurações no Config Server via:
```yaml
# application.properties de cada serviço
spring:
  config:
    import: "configserver:http://localhost:8888"
```

**Benefício:** Alterar a string de conexão do MongoDB exige mudança em um único lugar — sem redeploy de nenhum serviço.

---

### 3. Por que MongoDB?

Cadastros de clientes, produtos e vendas têm natureza flexível: campos opcionais, estrutura que evolui com o tempo. O MongoDB permite:

- Documentos sem schema fixo — novos campos sem `ALTER TABLE`
- Alta performance em leitura de documentos completos
- `@Indexed(unique = true)` para garantir unicidade de campos como CPF e email

```java
@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;  // ObjectId gerado automaticamente pelo MongoDB
    @Indexed(unique = true)
    private String cpf;
    // novos campos podem ser adicionados sem migration
}
```

---

### 4. Estrutura interna de cada serviço

Todos os serviços seguem a mesma organização de pacotes, inspirada em **Clean Architecture**:

```
src/main/java/br/com/renan/vendas/online/
├── domain/           → Entidades/documentos MongoDB (@Document)
├── repository/       → Interface de acesso ao banco (Spring Data)
├── usecase/          → Regras de negócio (BuscaCliente, CadastroCliente, etc.)
├── resources/        → Endpoints REST (@RestController)
├── errorhandling/    → Tratamento global de erros (@ControllerAdvice)
├── onlineconfig/     → Configurações (Swagger/OpenAPI)
└── dto/              → Objetos de transferência de dados (apenas no VendasService)
```

**Por que `usecase/` ao invés de `service/`?**

O nome `service/` é genérico demais. Usar `usecase/` torna a intenção explícita: cada classe representa uma **ação de negócio** com nome descritivo — `BuscaCliente` busca clientes, `CadastroVenda` registra vendas. É possível ler o pacote e entender o que o sistema faz sem abrir nenhum arquivo.

---

### 5. Comunicação entre serviços: Feign + Circuit Breaker

Quando o VendasService recebe uma requisição de nova venda, ele precisa consultar o ProdutoService para validar se os produtos existem e obter seus preços. Isso é feito com **OpenFeign** — uma biblioteca que transforma uma interface Java em uma chamada HTTP:

```java
@FeignClient(
    name = "produto-service",
    url = "${services.produto.url:http://localhost:8082}",
    fallback = ProdutoClientFallback.class
)
public interface IProdutoClient {
    @GetMapping("/produto/{id}")
    ProdutoDTO buscarPorId(@PathVariable String id);
}
```

**O que é Circuit Breaker?**

Imagine um disjuntor elétrico: quando a fiação sobrecarrega, ele desliga antes de causar um incêndio. O Circuit Breaker de software faz o mesmo com chamadas HTTP — se o ProdutoService estiver fora do ar, ao invés de o VendasService ficar esperando indefinidamente (e potencialmente derrubar junto), o circuit breaker detecta a falha e aciona o **fallback** — um comportamento alternativo previamente definido:

```java
public class ProdutoClientFallback implements IProdutoClient {
    @Override
    public ProdutoDTO buscarPorId(String id) {
        throw new IllegalStateException(
            "ProdutoService indisponível. Tente novamente mais tarde."
        );
    }
}
```

Resultado: o VendasService responde com erro `503 Service Unavailable` de forma controlada, sem travar.

---

### 6. Fluxo de criação de venda com consistência de dados

Em microsserviços não existe transação distribuída automática. O `VendasService` resolve isso com um **rollback manual** (Compensating Transaction):

```
POST /venda
│
├── 1. Valida cliente → GET ClienteService /cliente/isCadastrado/{id}
│        Se não existir → lança EntityNotFoundException (404)
│
├── 2. Para cada produto no carrinho:
│       a. Busca produto → GET ProdutoService /produto/{codigo}
│       b. Baixa estoque → POST ProdutoService /produto/{codigo}/estoque/baixa
│       c. Adiciona item à lista de processados
│
├── 3. Se QUALQUER erro ocorrer:
│       → Itera pelos itens JÁ processados
│       → Repõe estoque → POST ProdutoService /produto/{codigo}/estoque/reposicao
│       → Lança exceção (venda não é salva)
│
└── 4. Salva venda no MongoDB com status INICIADA
```

O mesmo padrão de estorno é aplicado no cancelamento de venda (`PUT /venda/{id}/cancelar`).

---

### 7. Tratamento de erros padronizado

Todos os serviços compartilham a mesma estrutura de resposta de erro via `@ControllerAdvice`:

```json
// Exemplo de erro 400 Bad Request (validação)
{
  "status": 400,
  "message": "Erro de validação",
  "errors": [
    { "field": "nome", "message": "não deve estar em branco" },
    { "field": "cpf", "message": "CPF inválido" }
  ]
}
```

```json
// Exemplo de erro 404 Not Found
{
  "status": 404,
  "message": "Cliente não encontrado com id: abc123"
}
```

**Benefício:** O frontend (ou outro serviço) recebe sempre o mesmo formato de erro, independente de qual serviço respondeu — facilitando o tratamento no lado do consumidor.

---

### 8. Por que Docker?

O Docker foi utilizado para orquestrar a **infraestrutura local** via `docker-compose` — apenas o MongoDB — sem containerizar a aplicação Java em si. Isso resolve o problema de instalar e configurar manualmente dependências, garantindo um ambiente reproduzível com um único comando.

```bash
# Sobe toda a infraestrutura necessária
docker-compose up -d
```

A aplicação Spring Boot continua rodando diretamente na JVM local, conectando-se ao container do MongoDB.

**Próximo passo natural:** Containerizar os próprios microserviços com `Dockerfile` em cada serviço, permitindo subir tudo — app + infra — com um único `docker-compose up`.

---

### 9. Documentação com Swagger/OpenAPI

Cada serviço expõe sua documentação de API automaticamente via SpringDoc:

| Serviço | URL |
|:---|:---|
| ClienteService | `http://localhost:8081/swagger-ui.html` |
| ProdutoService | `http://localhost:8082/swagger-ui.html` |
| VendasService  | `http://localhost:8083/swagger-ui.html` |

**Motivo:** Em um ecossistema com múltiplos serviços, o contrato de API precisa ser claro e testável sem depender de ferramentas externas como Postman.

---

## Roadmap de Desenvolvimento

| Funcionalidade | Status |
|---|---|
| Config Server | ✅ Implementado |
| ClienteService — CRUD completo + MongoDB | ✅ Implementado |
| ClienteService — Swagger, testes, tratamento de erros | ✅ Implementado |
| ProdutoService — CRUD completo + MongoDB | ✅ Implementado |
| ProdutoService — Swagger, testes, tratamento de erros | ✅ Implementado |
| VendasService — Orquestração de vendas | ✅ Implementado |
| Comunicação entre serviços via Feign (ProdutoService + ClienteService) | ✅ Implementado |
| Controle de estoque com rollback manual (Compensating Transaction) | ✅ Implementado |
| Circuit Breaker com Resilience4j | ✅ Implementado |
| Spring Actuator (health check) em todos os serviços | ✅ Implementado |
| Credenciais via variáveis de ambiente | ✅ Implementado |
| Service Discovery (Eureka) | 📋 Fora do escopo atual |
| Containerizar os microserviços (Dockerfile) | 📋 Próximo passo |
| API Gateway (Spring Cloud Gateway) | 📋 Próximo passo |

---

## O que eu faria diferente em produção

- **API Gateway:** Os clientes não deveriam chamar cada serviço diretamente. Um API Gateway (Spring Cloud Gateway) centralizaria as chamadas, autenticação e rate limiting em um único ponto de entrada.
- **Mensageria assíncrona:** A comunicação entre VendasService e ProdutoService é síncrona (Feign). Para operações críticas como registro de venda, uma fila assíncrona (RabbitMQ/Kafka) seria mais resiliente — a venda seria registrada mesmo que o ProdutoService estivesse temporariamente fora.
- **Service Discovery (Eureka):** Atualmente a URL do ProdutoService é configurada manualmente. Com Eureka, os serviços se registrariam automaticamente e o Feign descobriria o endereço sem configuração manual.
- **Containerização completa:** Adicionar `Dockerfile` em cada serviço para que toda a aplicação possa ser levantada com `docker-compose up` — sem precisar instalar JDK localmente.
