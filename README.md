# 🛒 Sales Microservices Ecosystem

Este projeto é um ecossistema de microsserviços desenvolvido para gestão de vendas, focado em alta disponibilidade, isolamento de domínios e escalabilidade. A arquitetura utiliza as tecnologias mais recentes do ecossistema Spring e segue as melhores práticas de engenharia de software.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot)
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
- Java 17 e Maven instalados (só necessário pra rodar/debugar um serviço fora do Docker).

### Opção A — Docker Compose completo (mais rápido)
Sobe Postgres, pgAdmin e os 4 serviços Java de uma vez, cada um buildado a partir do
[`Dockerfile`](./Dockerfile) multi-stage na raiz:
```bash
docker compose up -d --build
```
Primeira subida demora um pouco (build Maven de cada módulo). As próximas já reaproveitam
o cache de camadas do Docker. Se algum serviço subir antes do `ConfigServer` terminar de
bootar, `docker compose up -d --build <servico>` de novo resolve.

| Serviço | Porta |
|---|---|
| Config Server | 8888 |
| Cliente Service | 8081 |
| Produto Service | 8082 |
| Vendas Service | 8083 |
| PostgreSQL | 5432 |
| pgAdmin | 5050 (login padrão: `admin@admin.com` / `admin`) |

*Nota: o `init-db.sql` cria automaticamente os bancos `clientedb`, `produtodb` e `vendadb` na subida do Postgres.*

### Opção B — Manual, serviço por serviço (útil pra debugar um só)
Suba só a infraestrutura primeiro:
```bash
docker compose up -d postgres
```

Depois o Config Server, obrigatoriamente primeiro (os outros buscam config nele no boot):

**Linux/macOS:**
```bash
cd ConfigServer && ./mvnw spring-boot:run
```
**Windows:**
```cmd
cd ConfigServer
mvnw.cmd spring-boot:run
```

E por fim, um terminal por serviço de domínio:

**Linux/macOS:**
```bash
cd VendasService && ./mvnw spring-boot:run
```
**Windows:**
```cmd
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

## 📚 Documentação adicional
Decisões técnicas e padrões adotados estão detalhados em [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md).

---

## ⚠️ Limitações conhecidas
Projeto de portfólio em evolução ativa — alguns pontos documentados, ainda não corrigidos:
- `ProdutoService` não tem DTO de saída, expõe a entidade JPA direto na API (`ClienteService` já separou request/response, os outros dois não).
- O fallback do Feign (`@FeignClient(fallback = ...)`) está implementado nos dois clients do `VendasService`, mas o circuit breaker não está ativo (falta `feign.circuitbreaker.enabled=true`) — a degradação hoje é tratada via `try/catch` manual em `CadastroVenda`.
- Criação de venda usa uma saga manual (baixa estoque item a item, compensa em caso de falha) sem 2PC nem fila de retry — se o estorno também falhar, o estoque fica divergente até correção manual.
- `GestaoEstoque.baixarEstoque` faz check-then-act sem lock — duas baixas concorrentes no mesmo produto podem ambas passar da checagem de saldo antes de qualquer uma salvar.

---

## 📌 Contexto no Portfólio

Este é o projeto de maior complexidade técnica de uma trilha de evolução deliberada:

`UserControl (POO)` → `QualityGuard (Testes)` → `SalesSystem-JDBC` → `SalesPersistence-JPA` → **`Sales-Microservices`**

---

*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*
