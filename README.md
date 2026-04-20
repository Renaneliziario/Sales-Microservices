# Sales-Microservices

![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0-6DB33F?style=flat&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/SQL-PostgreSQL-336791?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Infra-Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20%7C%20OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=black)

> Ecossistema de microserviços para gestão comercial construído com **Spring Boot 3.4.5**, **Java 21** e **PostgreSQL**. A arquitetura foi migrada de um banco NoSQL (MongoDB) para um banco Relacional (PostgreSQL) para fortalecer a integridade dos dados e alinhar com padrões corporativos consolidados.

> 📐 Quer entender as decisões técnicas e de arquitetura? Veja o guia detalhado em [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

---

## ✨ Destaques Técnicos

- **Arquitetura em Camadas (Controller, Service, Repository)** — Separação clara de responsabilidades em todos os serviços.
- **DTO Pattern com Records (Java 21)** — Contratos de API seguros e imutáveis, isolando a camada de persistência.
- **`@ControllerAdvice` estruturado** — Respostas de erro padronizadas e previsíveis em toda a aplicação.
- **Bean Validation completo** — Validação de dados de entrada na borda da API.
- **Injeção de Dependências via Construtor** — Código testável e com baixo acoplamento.
- **Paginação nativa** com `Pageable` do Spring Data JPA.
- **OpenAPI/Swagger** configurado para documentação de API interativa.
- **Spring Data JPA e Hibernate** para persistência de dados relacional.
- **Spring Cloud Config Server** centralizando configurações externas.
- **Feign Client** com **Circuit Breaker (Resilience4j)** para comunicação inter-serviços resiliente.

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
│  :8081  ✅ Ativo    │  :8082         │ :8083         │
│  PostgreSQL         │  (Em Migração) │ (Em Migração) │
└─────────────────────┴────────────────┴───────┬───────┘
                                               │
                              consulta produto via Feign
                                               ↓
                                       ProdutoService
```

### Endpoints (Exemplo: ClienteService)

| Método | Endpoint | Descrição |
|:---|:---|:---|
| `GET` | `/clientes?page=0&size=10` | Lista paginada de clientes |
| `POST` | `/clientes` | Cadastra novo cliente — retorna `201 Created` |
| `GET` | `/clientes/{id}` | Busca por ID (404 estruturado se não encontrado) |
| `PUT` | `/clientes/{id}` | Atualiza cliente existente |
| `DELETE` | `/clientes/{id}` | Remove cliente — retorna `204 No Content` |

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|:---|:---|:---|
| Java | 21 LTS | Linguagem principal |
| Spring Boot | 3.4.5 | Framework base dos microserviços |
| Spring Cloud | 2024.0.0 | Config Server centralizado |
| Spring Data JPA | latest | Repositórios relacionais com Hibernate |
| OpenFeign | latest | Comunicação HTTP entre serviços |
| Resilience4j | latest | Circuit Breaker para resiliência |
| Spring Actuator | latest | Endpoints de monitoramento e saúde |
| PostgreSQL | latest | Banco de dados relacional |
| Docker | latest | Infraestrutura local (PostgreSQL) |
| Lombok | latest | Redução de boilerplate |
| Springdoc OpenAPI | latest | Documentação de API |
| Bean Validation | Jakarta EE | Validação de dados |
| JUnit 5 + Mockito | latest | Testes unitários |

---

## 🚀 Como Executar

**Pré-requisitos:** JDK 21+, Maven 3+, Docker

```bash
# 1. Suba o PostgreSQL via Docker Compose
docker-compose up -d
```

```yaml
# docker-compose.yml
services:
  postgres_db:
    image: postgres
    container_name: postgres_db
    ports:
      - "5432:5432"
    volumes:
      - ./postgres_data:/var/lib/postgresql/data
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_USER=postgres
      - POSTGRES_DB=clientedb
```

```bash
# 2. Inicie o Config Server (OBRIGATÓRIO primeiro)
cd ConfigServer
./mvnw spring-boot:run

# 3. Inicie o ClienteService
cd ClienteService
./mvnw spring-boot:run

# (Outros serviços em desenvolvimento)
```

---

## 📄 Documentação da API (Swagger UI)

Com o `ClienteService` rodando, acesse a documentação interativa:

| Serviço | Swagger UI |
|:---|:---|
| ClienteService | `http://localhost:8081/swagger-ui.html` |

---
*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*
