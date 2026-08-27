# Dockerfile genérico e multi-stage, parametrizado pelo build-arg MODULE.
# Usado pelos 4 serviços (ConfigServer, ClienteService, ProdutoService, VendasService)
# no docker-compose.yml, cada um passando seu próprio MODULE.
#
# Precisa ser buildado com o contexto na raiz do projeto (onde está o pom.xml pai),
# porque o Maven Reactor exige o pom pai + os poms de todos os módulos pra resolver
# as dependências internas do multi-módulo.

# ---------- Etapa 1: build com Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE
WORKDIR /workspace

# Copia primeiro só os poms, pra cachear a resolução de dependências
# em camadas do Docker (só reprocessa se um pom mudar).
COPY pom.xml .
COPY ConfigServer/pom.xml ConfigServer/pom.xml
COPY ClienteService/pom.xml ClienteService/pom.xml
COPY ProdutoService/pom.xml ProdutoService/pom.xml
COPY VendasService/pom.xml VendasService/pom.xml
RUN mvn -q -pl ${MODULE} -am dependency:go-offline || true

# Agora copia o código de verdade e builda só o módulo pedido (-pl) e o que ele precisar (-am)
COPY . .
RUN mvn -pl ${MODULE} -am clean package -DskipTests -q

# ---------- Etapa 2: imagem final, só com o JRE + o jar ----------
FROM eclipse-temurin:17-jre-alpine
ARG MODULE
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
