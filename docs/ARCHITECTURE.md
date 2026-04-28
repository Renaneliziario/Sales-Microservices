# 🏗️ Guia de Arquitetura

Este documento detalha as decisões técnicas e os padrões de design adotados para garantir que o sistema seja resiliente, escalável e de fácil manutenção.

## 🏛️ Padrão Arquitetural: Microsserviços

O sistema adota a decomposição baseada em domínios de negócio. Cada serviço é independente, possuindo sua própria lógica, infraestrutura e ciclo de vida.

### Componentes Chave da Infraestrutura

1.  **Configuração Centralizada (Spring Cloud Config):**
    - Para evitar que propriedades (como URLs de banco ou senhas) fiquem espalhadas no código, utilizamos um Servidor de Configuração.
    - Isso permite mudar o comportamento do sistema (ex: trocar de banco `dev` para `prod`) sem precisar recompilar as aplicações.

2.  **Isolamento de Dados (Database per Service):**
    - Cada microsserviço possui seu próprio banco de dados físico no PostgreSQL.
    - **Por que isso é importante?** Garante o desacoplamento. Um erro em uma tabela do `VendasService` não impede o `ClienteService` de continuar funcionando. Além disso, permite que cada banco cresça de forma independente.

---

## 🔗 Comunicação e Integração

Os serviços utilizam **OpenFeign** para realizar chamadas síncronas entre si de forma declarativa e limpa.

- **Fluxo de Agregação:** O `VendasService` atua como um orquestrador. Ao receber um pedido, ele consulta o `ClienteService` para validar o comprador e o `ProdutoService` para verificar e baixar o estoque.
- **Resiliência:** Implementamos o **Resilience4j** para lidar com falhas de rede ou lentidão em serviços externos, evitando que uma falha em um serviço derrube todo o ecossistema (Circuit Breaker).

---

## 💾 Modelo de Dados e Persistência

Utilizamos **Spring Data JPA** com **PostgreSQL**.

- **Migração MongoDB para PostgreSQL:** Optamos por um banco relacional para garantir a integridade dos dados (Transações ACID). Em um sistema de vendas, é crucial garantir que a baixa do estoque e o registro da venda aconteçam exatamente ao mesmo tempo ou nada seja salvo.
- **Identidade:** Utilizamos IDs do tipo `Long` com estratégia `IDENTITY`, otimizando a indexação e busca no banco de dados.
- **Relacionamentos:** O relacionamento entre `Venda` e `ItemVenda` é gerenciado via JPA com `CascadeType.ALL`, garantindo que os itens da venda sigam o mesmo ciclo de vida do pedido pai.

---

## 🛡️ Estratégia de Testes

A qualidade do código é validada em três níveis:

1.  **Testes Unitários:** Validam a lógica de negócio interna dos serviços sem dependências externas.
2.  **Testes de Integração:** Utilizam o banco **H2 em memória** para validar se o mapeamento do JPA e os repositórios estão funcionando corretamente sem precisar de um banco de dados real instalado.
3.  **Mocks de Serviços:** Usamos Mockito para simular a resposta de serviços externos (como Feign Clients), permitindo testar um serviço mesmo que os outros estejam desligados.

---

## 🛠️ Tecnologias Adotadas e Motivação

- **Java 17 (LTS):** Escolhida por ser a versão estável de suporte a longo prazo, trazendo melhorias de performance e sintaxes modernas como *Records*.
- **Spring Boot 3.4.x:** Aproveita o que há de mais moderno em autoconfiguração e suporte a microsserviços.
- **PostgreSQL 16:** Banco de dados relacional robusto para garantir consistência em operações financeiras e de estoque.
- **Docker:** Utilizado para garantir que qualquer desenvolvedor consiga rodar exatamente o mesmo ambiente, eliminando o clássico problema do "na minha máquina funciona".
