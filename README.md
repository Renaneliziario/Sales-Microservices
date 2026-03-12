# Sistema de Vendas Online (Microserviços)

Este é um projeto de estudo desenvolvido para um curso, focado em uma arquitetura de microserviços utilizando o ecossistema Spring Boot e Spring Cloud. O sistema gerencia clientes, produtos e vendas, utilizando MongoDB como banco de dados NoSQL.

## 🏗️ Arquitetura do Sistema

O sistema é composto pelos seguintes módulos:

-   **ConfigServer**: Servidor centralizado de configurações que gerencia as propriedades de todos os microserviços.
-   **ClienteService**: Responsável pelo cadastro e gestão de clientes (MongoDB).
-   **ProdutoService**: Responsável pelo catálogo de produtos (MongoDB).
-   **VendaService**: Responsável pelo processamento de vendas, integrando informações de clientes e produtos (MongoDB).

### Fluxo de Comunicação
Atualmente, os serviços se comunicam via HTTP REST, com URLs configuradas centralizadamente no Config Server.

## 🛠️ Tecnologias Utilizadas

-   **Java 21**: Versão LTS mais recente para performance e novos recursos.
-   **Spring Boot 3.3.10**: Base para todos os microserviços.
-   **Spring Cloud (2023.0.3)**: Gerenciamento de configurações e ecossistema de microserviços.
-   **Spring Data MongoDB**: Integração com banco de dados NoSQL.
-   **MongoDB**: Banco de dados persistente para cada serviço.
-   **SpringDoc OpenAPI (Swagger)**: Documentação automática das APIs.
-   **Lombok**: Redução de código boilerplate.
-   **Maven**: Automação de builds e gerenciamento de dependências.

## 🚀 Como Executar

### Pré-requisitos
-   Java 21 instalado.
-   Maven 3.9+ instalado.
-   MongoDB rodando localmente (ou via Docker) na porta `27017` com as credenciais `admin:admin`.

### Ordem de Inicialização
Para que o sistema funcione corretamente, siga a ordem de inicialização abaixo:

1.  **ConfigServer**: Acesse a pasta `ConfigServer` e execute `mvn spring-boot:run`.
    -   *Aguarde o servidor subir na porta 8888.*
2.  **ClienteService**: Acesse a pasta `ClienteService` e execute `mvn spring-boot:run`.
3.  **ProdutoService**: Acesse a pasta `ProdutoService` e execute `mvn spring-boot:run`.
4.  **VendasService**: Acesse a pasta `VendasService` e execute `mvn spring-boot:run`.

## 📌 Status do Projeto
O projeto está em desenvolvimento ativo. 
-   [x] Estrutura de microserviços base.
-   [x] Configuração centralizada (Config Server).
-   [x] ClienteService (CRUD inicial).
-   [ ] ProdutoService (Implementação de endpoints).
-   [ ] VendaService (Implementação da lógica de vendas).

---
*Desenvolvido por Renan como projeto de curso/pessoal.*
