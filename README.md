# Sales-Microservices

![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0-6DB33F?style=flat&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/NoSQL-MongoDB-47A248?style=flat&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Infra-Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20%7C%20OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=black)
![Status](https://img.shields.io/badge/Status-Funcional-brightgreen?style=flat)

> Ecossistema de microserviços para gestão comercial construído com **Spring Boot 3.4**, **Spring Cloud Config**, **MongoDB** e **Docker**. Todos os serviços estão implementados com CRUD completo, testes unitários, tratamento de erros estruturado e comunicação entre serviços via Feign com Circuit Breaker.

> 📐 Quer entender as decisões técnicas e de arquitetura? Veja [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

---

## ✨ Destaques Técnicos

- **Arquitetura Use Case** (`BuscaCliente`, `CadastroCliente`, `BuscaProduto`...) — cada intenção de negócio tem sua própria classe, inspirado em Clean Architecture
- **`@ControllerAdvice` estruturado** (`ApiError`, `ApiValidationError`) — respostas de erro padronizadas em todos os endpoints de todos os serviços
- **Bean Validation completo** — `@NotNull`, `@Size`, `@Pattern` com mensagens customizadas diretamente no domínio
- **Constructor Injection** em todos os componentes — sem `@Autowired` em campos, código testável e sem acoplamento implícito
- **Paginação nativa** com `Pageable` do Spring Data
- **OpenAPI/Swagger** configurado com metadados e versionamento via `@Value` em cada serviço
- **`@Indexed(unique = true)`** no MongoDB para campos únicos — consistência garantida no banco
- **Spring Cloud Config Server** centralizando configurações de todos os serviços
- **Feign Client** com **Circuit Breaker (Resilience4j)** — VendasService consulta ProdutoService de forma resiliente; se o serviço cair, o fallback assume sem derrubar o sistema
- **26 testes unitários** distribuídos entre ClienteService, ProdutoService e VendasService

---

## 🏗️ Arquitetura dos Serviços

```
┌──────────────────────────────────────────────────────┐
│           Config Server  :8888                       │
│   Centraliza application.yml de todos os serviços   │
└──────────────────────────────────────────────────────┘
           ↑ busca config na inicialização
┌──────────┴──────────┬────────────────┬───────────────┐
│  ClienteService     │  ProdutoService│ VendasService │
│  :8081  ✅ Ativo    │  :8082 ✅ Ativo │ :8083 ✅ Ativo │
│  MongoDB            │  MongoDB       │  MongoDB      │
└─────────────────────┴────────────────┴───────┬───────┘
                                               │
                              consulta produto via Feign
                                               ↓
                                       ProdutoService
```

### ClienteService — Endpoints

| Método | Endpoint | Descrição |
|:---|:---|:---|
| `GET` | `/cliente?page=0&size=10` | Lista paginada de clientes |
| `POST` | `/cliente` | Cadastra novo cliente — retorna `201 Created` |
| `GET` | `/cliente/{id}` | Busca por ID (404 estruturado se não encontrado) |
| `GET` | `/cliente/isCadastrado/{id}` | Verifica existência por ID — retorna `true/false` |
| `PUT` | `/cliente` | Atualiza cliente existente |
| `DELETE` | `/cliente/{id}` | Remove cliente — retorna `204 No Content` |

### ProdutoService — Endpoints

| Método | Endpoint | Descrição |
|:---|:---|:---|
| `GET` | `/produto?page=0&size=10` | Lista paginada de produtos |
| `POST` | `/produto` | Cadastra novo produto — retorna `201 Created` |
| `GET` | `/produto/id/{id}` | Busca por ID |
| `GET` | `/produto/{codigo}` | Busca por código |
| `GET` | `/produto/isCadastrado/{id}` | Verifica existência — retorna `true/false` |
| `PUT` | `/produto` | Atualiza produto existente |
| `DELETE` | `/produto/{id}` | Remove produto — retorna `204 No Content` |
| `POST` | `/produto/{codigo}/estoque/baixa` | Baixa estoque do produto |
| `POST` | `/produto/{codigo}/estoque/reposicao` | Repõe estoque do produto |

### VendasService — Endpoints

| Método | Endpoint | Descrição |
|:---|:---|:---|
| `GET` | `/venda?page=0&size=10` | Lista paginada de vendas |
| `POST` | `/venda` | Registra nova venda (valida cliente e produtos via Feign, reserva estoque) |
| `PUT` | `/venda/{id}/{codigoProduto}/{quantidade}/addProduto` | Adiciona produto a uma venda em aberto |
| `PUT` | `/venda/{id}/finalizar` | Finaliza uma venda em aberto |
| `PUT` | `/venda/{id}/cancelar` | Cancela uma venda e estorna o estoque |

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|:---|:---|:---|
| Java | 17 LTS | Linguagem principal |
| Spring Boot | 3.4.3 | Framework base dos microserviços |
| Spring Cloud | 2024.0.0 | Config Server centralizado |
| Spring Data MongoDB | latest | Repositórios NoSQL |
| OpenFeign | latest | Comunicação HTTP entre serviços (VendasService → ProdutoService) |
| Resilience4j | latest | Circuit Breaker — proteção contra falhas em cascata |
| Spring Actuator | latest | Endpoint `/actuator/health` em todos os serviços |
| MongoDB | latest | Banco de dados orientado a documentos |
| Docker | latest | Infraestrutura local |
| Lombok | latest | Redução de boilerplate |
| Springdoc OpenAPI | latest | Swagger UI e documentação de API |
| Bean Validation | Jakarta EE | Validação de entrada |
| JUnit 5 + Mockito | latest | Testes unitários |

---

## 🚀 Como Executar

**Pré-requisitos:** JDK 17+, Maven 3+, Docker

```bash
# 1. Suba o MongoDB via Docker Compose
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
```

```bash
# 2. Inicie o Config Server (OBRIGATÓRIO primeiro — os demais dependem dele)
cd ConfigServer
mvn spring-boot:run

# 3. Inicie o ClienteService
cd ClienteService
mvn spring-boot:run

# 4. Inicie o ProdutoService
cd ProdutoService
mvn spring-boot:run

# 5. Inicie o VendasService (depende do ProdutoService para validar itens)
cd VendasService
mvn spring-boot:run
```

> ⚠️ **Ordem importa!** O Config Server deve estar rodando antes de qualquer outro serviço. O VendasService depende do ProdutoService para validar os itens de uma venda.

---

## 📄 Documentação da API (Swagger UI)

Com os serviços rodando, acesse a documentação interativa:

| Serviço | Swagger UI | OpenAPI JSON |
|:---|:---|:---|
| ClienteService | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/v3/api-docs` |
| ProdutoService | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/v3/api-docs` |
| VendasService | `http://localhost:8083/swagger-ui.html` | `http://localhost:8083/v3/api-docs` |

### Health Check (Actuator)

Todos os serviços expõem um endpoint de saúde:

```
GET http://localhost:8081/actuator/health  → ClienteService
GET http://localhost:8082/actuator/health  → ProdutoService
GET http://localhost:8083/actuator/health  → VendasService
```

---

## 📌 Contexto no Portfólio

Este é o **projeto 5 de 5** da trilha de evolução técnica:

`UserControl (POO)` → `QualityGuard (Testes)` → `SalesSystem-JDBC` → `SalesPersistence-JPA` → **`Sales-Microservices`**

> *Este projeto aplica na prática os conceitos acumulados na trilha: OO sólida, testes, SQL, ORM — agora dentro de uma arquitetura distribuída com Spring Boot e NoSQL.*

---

*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*

