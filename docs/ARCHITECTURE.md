# Arquitetura — Sales-Microservices

## Visão Geral
Ecossistema de microserviços para gestão comercial (Clientes, Produtos, Vendas), construído com Spring Boot 3.4.5 e Java 21. A arquitetura utiliza **PostgreSQL** como banco de dados relacional, garantindo consistência e integridade, e **Spring Cloud Config** para gerenciamento centralizado de configurações. Os serviços são independentes e se comunicam via APIs REST.

> **Status:** O `ClienteService` foi totalmente migrado para a nova arquitetura com PostgreSQL e pacotes padronizados. Os demais serviços estão em processo de modernização.

---

## Mapa de Serviços
```
Sales-Microservices/
│
├── ConfigServer/       → Gerenciamento centralizado de configurações (Spring Cloud Config)
├── ClienteService/     → CRUD de clientes com persistência PostgreSQL ✅
├── ProdutoService/     → CRUD de produtos (em migração para PostgreSQL) 🚧
└── VendasService/      → Orquestração de vendas (em migração para PostgreSQL) 🚧
```

---

## Arquitetura de Alto Nível
```
                    ┌─────────────────┐
                    │   Config Server  │  ← Porta 8888
                    │  (Spring Cloud)  │
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
      PostgreSQL        PostgreSQL    ├── valida cliente → ClienteService
                                      ├── busca produto → ProdutoService
                                      ├── baixa estoque → ProdutoService
                                      └── repõe estoque (rollback) → ProdutoService
```
---

## Decisões de Arquitetura

### 1. Migração para PostgreSQL: A Busca por Consistência
O projeto foi inicialmente concebido com MongoDB (NoSQL), mas evoluiu para **PostgreSQL (SQL)** para atender a requisitos de consistência e integridade de dados, fundamentais em sistemas transacionais, além de ser uma excelente oportunidade de aprendizado. As principais motivações técnicas foram:
- **Conformidade ACID:** PostgreSQL garante que as transações (como uma venda) sejam Atômicas, Consistentes, Isoladas e Duráveis. Isso impede cenários como uma venda ser registrada sem que o estoque do produto seja debitado.
- **Integridade Referencial:** O uso de chaves estrangeiras (`foreign keys`) garante que não seja possível criar uma venda para um cliente que não existe, ou registrar um item de venda para um produto inexistente. Essa validação é feita pelo próprio banco de dados, adicionando uma camada robusta de segurança.
- **Padrão Corporativo:** Bancos de dados relacionais são a espinha dorsal da maioria dos sistemas corporativos. Dominar JPA/Hibernate com PostgreSQL é uma habilidade essencial para o mercado de trabalho.

### 2. Estrutura de Pacotes Padronizada (Clean Architecture)
Para promover clareza, testabilidade e manutenibilidade, todos os serviços seguirão uma estrutura de pacotes uniforme, baseada em responsabilidades:
```
src/main/java/br/com/renan/vendas/online/
├── domain/           → Entidades JPA (@Entity) que representam as tabelas do banco.
├── repository/       → Interfaces Spring Data JPA para acesso ao banco.
├── service/          → Camada de serviço que contém a lógica de negócio principal.
├── controller/       → Endpoints REST (@RestController) que expõem a API.
├── exception/        → Tratamento global de erros da aplicação (@ControllerAdvice).
├── dto/              → Data Transfer Objects (Records) para desacoplar a API da persistência.
└── config/           → Configurações de Beans do Spring (ex: OpenAPI).
```
Essa separação garante que a lógica de negócio (`service`) não está acoplada à tecnologia de exposição (web/`controller`), e que a camada de persistência (`domain`/`repository`) pode ser trocada sem impactar o resto da aplicação.

### 3. Comunicação entre Serviços (Feign e Circuit Breaker)
A comunicação síncrona entre serviços (ex: `VendasService` consultando `ProdutoService`) é realizada via **OpenFeign**. Para proteger o sistema contra falhas em cascata, o **Resilience4j** atua como um *Circuit Breaker*: se um serviço chamado estiver indisponível, o Circuit Breaker interrompe a chamada e aciona um método de *fallback*, evitando que o serviço chamador fique travado e garantindo uma resposta controlada ao usuário.

---

## Roadmap de Desenvolvimento
| Funcionalidade | Status |
|---|---|
| Config Server com Spring Cloud | ✅ Implementado |
| `ClienteService` — Migração para PostgreSQL e DTOs | ✅ Implementado |
| `ClienteService` — Documentação Swagger e tratamento de erros | ✅ Implementado |
| Padronização de estrutura de pacotes (`controller`, `service`) | 🚧 Em Andamento |
| `ProdutoService` — Migração para PostgreSQL | 📋 Próximo passo |
| `VendasService` — Migração para PostgreSQL | 📋 Próximo passo |
| Comunicação inter-serviços com Feign e Circuit Breaker | ✅ Implementado |
| Spring Actuator (health check) | ✅ Implementado |
| Service Discovery (Eureka) | 📋 Fora do escopo atual |
| Containerizar os microserviços (Dockerfile) | 📋 Próximo passo |
| API Gateway (Spring Cloud Gateway) | 📋 Próximo passo |
