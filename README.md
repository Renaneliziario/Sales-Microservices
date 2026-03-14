# Sales-Microservices: Estudo de Arquitetura Distribuída com Spring Boot 3.4 e NoSQL

> 🚧 **Status do Projeto: Em Desenvolvimento e Estudo Ativo** 🚧
>
> Este repositório é um projeto de estudo focado na transição da arquitetura monolítica para microserviços. O objetivo principal é aprender a configurar e integrar componentes do ecossistema **Spring Cloud** e persistência **NoSQL**.

---

## 🎯 Objetivos de Aprendizado

Este projeto está sendo desenvolvido para explorar e praticar os seguintes conceitos:
1.  **Configuração Centralizada:** Uso do **Spring Cloud Config** para gerenciar propriedades de múltiplos serviços.
2.  **Bancos de Dados NoSQL:** Implementação de persistência com **MongoDB** em substituição ao modelo relacional tradicional.
3.  **Desacoplamento de Serviços:** Divisão de responsabilidades entre serviços de Cliente, Produto e Vendas.
4.  **Ambientes com Docker:** Uso de containers para agilizar o setup de infraestrutura local (MongoDB).

---

## 🏗️ Estrutura em Desenvolvimento

O ecossistema é composto por microserviços em diferentes fases de implementação:

*   **ConfigServer (Porta 8888):** Implementado. Centraliza as configurações via perfil `native`.
*   **ClienteService (Porta 8081):** Em desenvolvimento. Focado em operações CRUD com MongoDB.
*   **ProdutoService (Porta 8082):** Planejado. Gestão de catálogo.
*   **VendasService (Porta 8083):** Planejado. Orquestração de pedidos.

---

## 🛠️ Tecnologias e Ferramentas (Em Estudo)

*   **Java 17 (LTS):** Linguagem base para o desenvolvimento dos serviços.
*   **Spring Boot 3.4.3:** Framework para construção das aplicações.
*   **Spring Cloud (2024.0.0):** Ferramentas para sistemas distribuídos.
*   **MongoDB & Spring Data:** Estudo de persistência orientada a documentos.
*   **Docker:** Orquestração de banco de dados para ambiente de desenvolvimento.

---

## 🐳 Setup Local (MongoDB)

Para rodar os serviços que dependem de persistência, utilizamos o Docker:

```bash
docker run --name mongodb -p 27017:27017 -d mongo
```

---

## 📌 Cronograma de Evolução
1.  [x] Configuração inicial do Config Server.
2.  [x] Conexão do ClienteService com MongoDB.
3.  [ ] Desenvolvimento dos endpoints de Produto e Vendas.
4.  [ ] Exploração de Service Discovery (Eureka) e API Gateway.

---
*Projeto de estudo desenvolvido por Renan Queiroz Eliziario como parte da formação em Back-End Java.*
