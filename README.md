# Sales-Microservices

![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0-6DB33F?style=flat&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/NoSQL-MongoDB-47A248?style=flat&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Infra-Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20%7C%20OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=black)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=flat)

> Ecossistema de microserviços para gestão comercial construído com **Spring Boot 3.4**, **Spring Cloud Config**, **MongoDB** e **Docker**. O `ClienteService` está em produção com CRUD completo, paginação, validações e tratamento de erros estruturado. Os demais serviços estão em desenvolvimento ativo.

> 📐 Quer entender as decisões técnicas e de arquitetura? Veja [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

---

## ✨ Destaques Técnicos

- **Arquitetura Use Case** (`BuscaCliente`, `CadastroCliente`) — separação de intenção de negócio, inspirada em Clean Architecture
- **`@ControllerAdvice` estruturado** (`ApiError`, `ApiValidationError`) — respostas de erro padronizadas e descritivas para todos os endpoints
- **Bean Validation completo** — `@NotNull`, `@Size`, `@Pattern` com mensagens customizadas diretamente no domínio
- **Constructor Injection** em todos os componentes — sem `@Autowired` em campos, código testável e sem acoplamento implícito
- **Paginação nativa** com `Pageable` do Spring Data
- **OpenAPI/Swagger** configurado com metadados e versionamento via `@Value`
- **`@Indexed(unique = true)`** no MongoDB para CPF e email — consistência garantida no banco
- **Spring Cloud Config Server** centralizando configurações de todos os serviços

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
│  :8081  ✅ Ativo    │  :8082 🚧 WIP  │ :8083 🚧 WIP  │
│  MongoDB            │                │               │
└─────────────────────┴────────────────┴───────────────┘
```

### ClienteService — Endpoints disponíveis

| Método | Endpoint | Descrição |
|:---|:---|:---|
| `GET` | `/cliente?page=0&size=10` | Lista paginada de clientes |
| `POST` | `/cliente` | Cadastra novo cliente (validação completa) |
| `GET` | `/cliente/{id}` | Busca por ID (404 estruturado se não encontrado) |
| `GET` | `/cliente/isCadastrado/{id}` | Verifica existência por ID |
| `PUT` | `/cliente` | Atualiza cliente existente |
| `DELETE` | `/cliente/{id}` | Remove cliente (retorna `204 No Content`) |

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|:---|:---|:---|
| Java | 17 LTS | Linguagem principal |
| Spring Boot | 3.4.3 | Framework base dos microserviços |
| Spring Cloud | 2024.0.0 | Config Server centralizado |
| Spring Data MongoDB | latest | Repositórios NoSQL |
| MongoDB | latest | Banco de dados orientado a documentos |
| Docker | latest | Infraestrutura local |
| Lombok | latest | Redução de boilerplate |
| Springdoc OpenAPI | latest | Swagger UI e documentação |
| Bean Validation | Jakarta EE | Validação de entrada |

---

## 🚀 Como Executar

**Pré-requisitos:** JDK 17+, Maven 3+, Docker

```bash
# 1. Suba a infraestrutura (MongoDB + pgAdmin) via Docker Compose
docker-compose up -d

# 2. Inicie o Config Server primeiro (os demais dependem dele)
cd ConfigServer
mvn spring-boot:run

# 3. Inicie o ClienteService
cd ClienteService
mvn spring-boot:run

# 4. Acesse a documentação interativa (Swagger UI):
http://localhost:8081/swagger-ui.html
```

---

## 📄 Documentação da API

Com o `ClienteService` rodando, acesse:

- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8081/v3/api-docs`

---

## 📌 Contexto no Portfólio

Este é o **projeto 5 de 5** da trilha de evolução técnica:

`UserControl (POO)` → `QualityGuard (Testes)` → `SalesSystem-JDBC` → `SalesPersistence-JPA` → **`Sales-Microservices`**

> *Este projeto aplica na prática os conceitos acumulados na trilha: OO sólida, testes, SQL, ORM — agora dentro de uma arquitetura distribuída com Spring Boot e NoSQL.*

---

*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*

