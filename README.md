# 🛒 Sales Microservices Ecosystem

Este projeto é um ecossistema de microsserviços desenvolvido para gestão de vendas, focado em alta disponibilidade, isolamento de domínios e escalabilidade. A arquitetura utiliza as tecnologias mais recentes do ecossistema Spring e segue as melhores práticas de engenharia de software.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)

## 🏗️ Arquitetura

```
                    ┌─────────────────────┐
                    │    Config Server    │  :8888
                    │  (Spring Cloud)     │
                    └────────┬────────────┘
                             │ fornece configs
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ Cliente Service │ │ Produto Service │ │ Vendas Service  │
│    :8081        │ │    :8082        │ │    :8083        │
│  Swagger UI ✓   │ │  Swagger UI ✓   │ │  Swagger UI ✓   │
└────────┬────────┘ └────────┬────────┘ └────────┬────────┘
         │                   │                   │
         ▼                   ▼                   ▼
    [clientedb]         [produtodb]          [vendadb]
    PostgreSQL          PostgreSQL           PostgreSQL
    (isolado)           (isolado)            (isolado)
```

Cada serviço possui seu próprio banco de dados PostgreSQL — sem compartilhamento de schema entre domínios.

---

## 📋 Sobre o Projeto

O sistema é composto por 4 módulos principais que trabalham de forma coordenada:
1.  **Config Server**: Centralizador de configurações para os 3 serviços de domínio.
2.  **Cliente Service**: Gestão de dados cadastrais de clientes.
3.  **Produto Service**: Controle de catálogo e estoque de produtos.
4.  **Vendas Service**: Orquestrador de pedidos que integra clientes e produtos.

### Principais Funcionalidades
- ✅ Cadastro completo de Clientes e Produtos.
- ✅ Gestão de estoque com baixa automática e reposição.
- ✅ Fluxo de venda completo com cálculo de totais e validação de disponibilidade.
- ✅ Configurações centralizadas para facilitar a manutenção e deploy.

---

## 🚀 Como Iniciar

### Pré-requisitos
- Docker e Docker Compose instalados.
- Java 17 instalado (para execução local).
- Maven instalado.

### Passo 1: Infraestrutura (PostgreSQL)
O projeto utiliza bancos de dados isolados para cada serviço. Suba o container do banco:
```bash
docker compose up -d
```
*Nota: Este comando já cria automaticamente os bancos `clientedb`, `produtodb` e `vendadb`.*

### Passo 2: Config Server (Obrigatório primeiro)
Todos os outros serviços buscam suas configurações aqui.

**No Linux/macOS:**
```bash
cd ConfigServer
./mvnw spring-boot:run
```

**No Windows (Command Prompt ou PowerShell):**
```cmd
cd ConfigServer
mvnw.cmd spring-boot:run
```

### Passo 3: Serviços de Domínio
Abra um terminal para cada serviço e execute o comando correspondente ao seu sistema:

**No Linux/macOS:**
```bash
# Exemplo para o serviço de Vendas
cd VendasService && ./mvnw spring-boot:run
```

**No Windows:**
```cmd
# Exemplo para o serviço de Vendas
cd VendasService
mvnw.cmd spring-boot:run
```

---

## 🔗 Documentação das APIs (Swagger)
Após iniciar os serviços, as APIs podem ser testadas via Swagger UI:
- **Clientes**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **Produtos**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
- **Vendas**: [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)

---

## 🧪 Qualidade e Testes
O projeto possui uma suíte de testes que cobre desde regras unitárias até fluxos integrados entre serviços. Os testes utilizam banco H2 em memória — nenhuma dependência externa necessária:
```bash
# Em cada serviço (Linux/macOS):
./mvnw test

# No Windows:
mvnw.cmd test
```

---

## 📌 Contexto no Portfólio

Este é o projeto de maior complexidade técnica de uma trilha de evolução deliberada:

`UserControl (POO)` → `QualityGuard (Testes)` → `SalesSystem-JDBC` → `SalesPersistence-JPA` → **`Sales-Microservices`**

---

*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*
