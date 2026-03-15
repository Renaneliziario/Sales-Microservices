# Arquitetura — Sales-Microservices

## Visão Geral

Ecossistema de microserviços para gestão comercial (Clientes, Produtos, Vendas), construído com Spring Boot 3.4 e Spring Cloud. Cada serviço é independente, com sua própria base de dados e ciclo de deploy — comunicando-se via APIs REST e compartilhando configurações através de um Config Server centralizado.

> **Status:** Projeto em desenvolvimento ativo. ConfigServer e ClienteService implementados. ProdutoService e VendasService em progresso.

---

## Mapa de Serviços

```
Sales-Microservices/
│
├── ConfigServer/       → Gerenciamento centralizado de configurações (Spring Cloud Config)
├── ClienteService/     → CRUD de clientes com persistência MongoDB
├── ProdutoService/     → CRUD de produtos (em desenvolvimento)
└── VendasService/      → Orquestração de vendas (em desenvolvimento)
```

---

## Arquitetura de Alto Nível

```
                    ┌─────────────────┐
                    │   Config Server  │  ← Porta 8888
                    │  (Spring Cloud)  │  ← Lê configs do Git/classpath
                    └────────┬────────┘
                             │ fornece configurações
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ClienteService│  │ProdutoService│  │VendasService │
    │  Porta 8081  │  │  Porta 8082  │  │  Porta 8083  │
    └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
           │                 │                 │
           ▼                 ▼                 ▼
       MongoDB            MongoDB            MongoDB
```

---

## Decisões de Arquitetura

### 1. Por que microserviços?

O objetivo principal deste projeto é **aprendizado de arquitetura distribuída**, não resolver um problema de escala real. A separação em serviços demonstra na prática:

- Como serviços se comunicam via HTTP/REST
- Como configurações são gerenciadas de forma centralizada
- Como bancos de dados diferentes (SQL vs NoSQL) coexistem no mesmo ecossistema
- Como o Docker permite subir a infraestrutura local (MongoDB) via docker-compose

---

### 2. Por que Spring Cloud Config Server?

Em uma arquitetura monolítica, há um único `application.properties`. Com múltiplos serviços, cada um teria suas próprias configurações — e alterar a URL de um banco exigiria mudança em múltiplos repositórios com redeploy de cada serviço.

O Config Server resolve isso centralizando todas as propriedades:

```
ConfigServer (porta 8888)
    └── serve: clienteservice.properties
    └── serve: produtoservice.properties
    └── serve: vendasservice.properties
```

Cada serviço ao iniciar busca suas configurações no Config Server via:
```yaml
# bootstrap.yml de cada serviço
spring:
  config:
    import: "configserver:http://localhost:8888"
```

**Benefício:** Alterar a string de conexão do MongoDB exige mudança em um único lugar — sem redeploy do ClienteService.

---

### 3. Por que MongoDB no ClienteService?

Cadastros de clientes têm natureza flexível: endereços variados, campos opcionais, estrutura que evolui com o tempo. Um schema relacional exigiria `ALTER TABLE` a cada nova necessidade.

O MongoDB permite:
- Documentos sem schema fixo
- Evolução de estrutura sem migração
- Alta performance em leitura de documentos completos

```java
@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;  // ObjectId do MongoDB
    private String nome;
    private String cpf;
    // novos campos podem ser adicionados sem migration
}
```

---

### 4. Estrutura interna de cada serviço

Cada microserviço segue a mesma arquitetura interna em camadas:

```
src/main/java/
└── br/com/renan/{servico}/
    ├── controller/   → Endpoints REST (@RestController)
    ├── service/      → Regras de negócio
    ├── repository/   → Acesso a dados (Spring Data)
    ├── domain/       → Entidades/Documentos
    └── dto/          → Objetos de transferência de dados
```

**Motivo:** Manter a mesma estrutura em todos os serviços reduz o custo cognitivo — um desenvolvedor que conhece um serviço consegue navegar nos outros sem dificuldade.

---

### 5. Por que Docker?

O Docker foi utilizado exclusivamente para orquestrar a **infraestrutura local** via `docker-compose` — MongoDB e pgAdmin — sem containerizar a aplicação Java em si. Isso resolve o problema de instalar e configurar manualmente cada dependência, garantindo um ambiente reproduzível com um único comando.

```bash
# Sobe toda a infraestrutura necessária
docker-compose up -d
```

```yaml
# docker-compose.yml
services:
  mongodb:
    image: mongo:latest
    container_name: mongodb_db
    ports:
      - "27017:27017"
    volumes:
      - ./mongo_data:/data/db

  pgadmin:
    image: dpage/pgadmin4
    container_name: pgadmin_ui
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    volumes:
      - ./pgadmin_data:/var/lib/pgadmin
```

A aplicação Spring Boot continua rodando diretamente na JVM local, conectando-se aos containers de infraestrutura.

**Próximo passo:** Containerizar os próprios microserviços com `Dockerfile` em cada serviço, permitindo subir tudo com um único `docker-compose up`.

---

### 6. Documentação com Swagger/OpenAPI

Cada serviço expõe sua documentação de API automaticamente via SpringDoc:

```
http://localhost:8081/swagger-ui.html  → ClienteService
http://localhost:8082/swagger-ui.html  → ProdutoService
```

**Motivo:** Em um ecossistema com múltiplos serviços, o contrato de API precisa ser claro e testável sem depender de ferramentas externas como Postman.

---

## Roadmap de Desenvolvimento

| Funcionalidade | Status |
|---|---|
| Config Server | ✅ Implementado |
| ClienteService — CRUD completo | ✅ Implementado |
| ClienteService — MongoDB | ✅ Implementado |
| ClienteService — Swagger | ✅ Implementado |
| ProdutoService — CRUD | 🔄 Em desenvolvimento |
| VendasService — Orquestração | 🔄 Em desenvolvimento |
| Comunicação entre serviços (RestTemplate/Feign) | 📋 Planejado |
| Service Discovery (Eureka) | 📋 Planejado |
| docker-compose.yml completo (containerizar os serviços) | 📋 Planejado |

---

## O que eu faria diferente hoje

- **API Gateway:** Em produção, os clientes não deveriam chamar cada serviço diretamente. Um API Gateway (Spring Cloud Gateway) centralizaria as chamadas, autenticação e rate limiting.
- **Circuit Breaker:** Sem resiliência implementada — se o ProdutoService cair, o VendasService falha em cascata. Resilience4j resolveria isso.
- **Mensageria:** A comunicação síncrona via REST é simples, mas para operações críticas como registro de venda, uma fila assíncrona (RabbitMQ/Kafka) seria mais resiliente.
