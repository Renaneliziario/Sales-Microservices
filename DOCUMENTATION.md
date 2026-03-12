# Documentação Técnica - Sistema de Vendas Online

Este documento fornece detalhes técnicos sobre a implementação e configuração do projeto de microserviços.

## 📁 Estrutura do Projeto

O projeto utiliza uma estrutura modular Maven (Multi-module Project).

```text
SistemaVendasSPRINGBOOT/
├── pom.xml (Parent)
├── ConfigServer/      - Spring Cloud Config
├── ClienteService/    - Microserviço de Clientes
├── ProdutoService/    - Microserviço de Produtos
└── VendasService/     - Microserviço de Vendas
```

---

## 🛠️ Detalhes dos Microserviços

### 1. ConfigServer (Porta: 8888)
O ConfigServer utiliza o sistema de arquivos local (native profile) para carregar as configurações. As configurações para cada serviço estão localizadas em:
`ConfigServer/src/main/resources/config/`

-   **cliente-service.yaml**: Configura a porta 8081 e conexão com MongoDB (banco: `cliente`).
-   **produto-service.yaml**: Configura a porta 8082 e conexão com MongoDB (banco: `produto`).
-   **venda-service.yaml**: Configura a porta 8083 e as URLs de integração com os outros serviços.

### 2. ClienteService (Porta: 8081)
-   **Banco de Dados**: MongoDB (coleção: `cliente`).
-   **Domain Model**: `Cliente` (id, nome, cpf, email, telefone).
-   **Padrão de Projeto**: Camadas (Controller -> UseCase -> Repository).
-   **Endpoints Principais**:
    -   `GET /cliente`: Lista clientes paginados.

### 3. ProdutoService (Porta: 8082)
-   **Banco de Dados**: MongoDB (coleção: `produto`).
-   *Em desenvolvimento inicial.*

### 4. VendasService (Porta: 8083)
-   **Banco de Dados**: MongoDB (coleção: `venda`).
-   **Integração**: Configurado para chamar endpoints de `ClienteService` e `ProdutoService`.
-   *Em desenvolvimento inicial.*

---

## 🗄️ Persistência de Dados (MongoDB)

O sistema utiliza MongoDB em cada serviço para garantir desacoplamento (Database per Service). 
As URIs de conexão são gerenciadas centralizadamente pelo ConfigServer:

```yaml
uri: mongodb://admin:admin@127.0.0.1:27017/ebac?authSource=admin&retryWrites=true&w=majority
```

---

## 🏗️ Padrões e Práticas Adotadas

1.  **Centralized Configuration**: Spring Cloud Config para facilitar mudanças de ambiente.
2.  **DTOs & Entities**: Uso de classes de domínio específicas.
3.  **Error Handling**: Implementação de tratamento de erros global (ApiError, ApiValidationError).
4.  **Pagination**: Uso de `Pageable` do Spring Data para listagens performáticas.
5.  **Documentation**: OpenAPI 3.0 para exploração das rotas de API.

---

## ✅ Checklist de Implementação (Progresso)

| Recurso | Status |
| :--- | :--- |
| Configuração do Parent POM | Concluído |
| Config Server (Nativo) | Concluído |
| ClienteService - Repositório | Concluído |
| ClienteService - Listagem | Concluído |
| ClienteService - Cadastro | Concluído |
| ProdutoService - Repositório | Concluído |
| VendaService - Configuração de Endpoints | Concluído |
| Dockerization (Compose) | Pendente |
| Testes Unitários | Pendente |
| Testes de Integração | Pendente |

---
*Atualizado em: 12 de Março de 2026*
