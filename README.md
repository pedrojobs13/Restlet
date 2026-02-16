# Tutorial de Configuração - Biblioteca Digital API (Restlet)

## Índice
1. [Pré-requisitos](#pré-requisitos)
2. [Configuração do Ambiente](#configuração-do-ambiente)
3. [Instalação e Execução](#instalação-e-execução)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Testes](#testes)
6. [Problemas Comuns](#problemas-comuns)

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+** (gerenciador de dependências)
- **MySQL 8.0** (banco de dados)
- **Docker e Docker Compose** (opcional, mas recomendado)
- **Git** (para clonar o repositório)

### Verificando as instalações

```bash
# Verificar versão do Java
java -version

# Verificar versão do Maven
mvn -version

# Verificar versão do Docker
docker --version
docker-compose --version

# Verificar versão do MySQL (se instalado localmente)
mysql --version
```

---

## Configuração do Ambiente

### 1. Clonar o Repositório

```bash
git clone https://github.com/pedrojobs13/Restlet.git
cd Restlet
```

### 2. Configurar o Banco de Dados

Você pode configurar o MySQL de duas formas:

#### **Opção A: Usando Docker (Recomendado)**

O projeto já vem com um arquivo `docker-compose.yml` configurado:

```bash
# Navegar até o diretório docker
cd docker

# Iniciar os containers do MySQL e phpMyAdmin
docker-compose up -d

# Verificar se os containers estão rodando
docker-compose ps
```

Após executar, você terá:
- **MySQL** rodando na porta `3306`
- **phpMyAdmin** acessível em `http://localhost:8081`

Credenciais:
- **Banco de dados**: `biblioteca`
- **Usuário**: `biblioteca_user`
- **Senha**: `biblioteca_pass`
- **Root Password**: `root`

#### **Opção B: MySQL Local**

Se preferir instalar o MySQL localmente:

```sql
-- Conectar ao MySQL como root
mysql -u root -p

-- Criar o banco de dados
CREATE DATABASE biblioteca;

-- Criar o usuário
CREATE USER 'biblioteca_user'@'localhost' IDENTIFIED BY 'biblioteca_pass';

-- Conceder permissões
GRANT ALL PRIVILEGES ON biblioteca.* TO 'biblioteca_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Arquivo de Propriedades

O projeto usa o arquivo `application.properties` localizado em `src/main/resources/application.properties`.

#### Estrutura do arquivo `application.properties`:

```properties
# Configurações do Banco de Dados
db.host=jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=UTC
db.user=biblioteca_user
db.pass=biblioteca_pass

# Configurações JWT
jwt=minhaChaveSecretaSuperSegura123456789012345678901234567890
expiration=86400000
```

**Nota importante**:
- O arquivo `application.properties` já existe no projeto com configurações padrão
- Se necessário, ajuste as credenciais do banco de dados
- A propriedade `jwt` deve ter no mínimo 32 caracteres para segurança
- A propriedade `expiration` está em milissegundos (86400000 = 24 horas)

#### Exemplo de configuração completa:

```properties
# Configurações do Banco de Dados
db.host=jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=UTC
db.user=biblioteca_user
db.pass=biblioteca_pass

# Configurações JWT para Autenticação
jwt=MinhaChaveSecretaSuperSeguraParaJWT2024ComMaisDe32Caracteres
expiration=86400000
```

**Se estiver usando Docker**: As configurações padrão já funcionam com o docker-compose.yml fornecido.

**Se estiver usando MySQL local**: Ajuste `db.user` e `db.pass` conforme as credenciais criadas no passo 2.

---

## Instalação e Execução

### 1. Instalar Dependências

```bash
# Voltar para o diretório raiz do projeto
cd ..

# Limpar e instalar dependências
mvn clean install
```

Este comando irá:
- Baixar todas as dependências do Maven
- Compilar o código
- Executar os testes unitários
- Gerar o arquivo JAR executável

### 2. Executar Migrações do Banco de Dados

As migrações são executadas automaticamente pelo Flyway quando a aplicação inicia pela primeira vez. O Flyway irá:

- Criar as tabelas: `usuarios`, `autores`, `categorias`, `livros`, `livros_categorias`
- Configurar as relações entre tabelas
- Aplicar constraints e índices

**Arquivo de migração**: `src/main/resources/db/migration/V1__create_table_livros.sql`

### 3. Executar a Aplicação

Existem duas formas de executar:

#### **Opção A: Usando Maven**

```bash
mvn exec:java -Dexec.mainClass="Main"
```

#### **Opção B: Usando o JAR compilado**

```bash
java -jar target/trabalhoRestlet-1.0-SNAPSHOT.jar
```

### 4. Verificar se a API está rodando

A API estará disponível em: `http://localhost:8182`

Teste com um comando curl:

```bash
# Verificar endpoints públicos (exemplo)
curl http://localhost:8182/api/autores
```

---

## Estrutura do Projeto

```
trabalhoRestlet/
├── docker/
│   └── docker-compose.yml          # Configuração Docker
├── docs/
│   ├── collection/                 # Coleção Postman para testes
│   └── modelagem/                  # Diagrama do banco de dados
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/            # Configurações (DB, Flyway)
│   │   │   ├── controller/        # Controllers REST
│   │   │   ├── filter/            # Filtros de autenticação
│   │   │   ├── model/             # Entidades do domínio
│   │   │   ├── repository/        # Camada de persistência
│   │   │   ├── routes/            # Definição de rotas
│   │   │   ├── service/           # Lógica de negócio
│   │   │   ├── utils/             # Utilitários (JWT, Password)
│   │   │   └── Main.java          # Classe principal
│   │   └── resources/
│   │       ├── application.properties  # Configurações da aplicação
│   │       └── db/migration/      # Scripts Flyway
│   └── test/                      # Testes unitários
├── pom.xml                        # Dependências Maven
└── README.md                      # Documentação
```

---

## Testes

### Executar Testes Unitários

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório detalhado
mvn test -Dtest=AuthServiceTest

# Executar todos os testes e gerar relatório
mvn clean test
```

### Testes Disponíveis

- `AuthServiceTest` - Testes de autenticação
- `AutorServiceTest` - Testes do serviço de autores
- `CategoriaServiceTest` - Testes do serviço de categorias
- `LivroServiceTest` - Testes do serviço de livros
- `UsuarioServiceTest` - Testes do serviço de usuários

### Importar Collection do Postman

O projeto inclui uma collection do Postman para facilitar os testes:

1. Abra o Postman
2. Clique em "Import"
3. Selecione o arquivo: `docs/collection/Biblioteca Digital API - Restlet.postman_collection.json`
4. A collection será importada com todos os endpoints configurados

---

## Problemas Comuns

### 1. Erro de Conexão com MySQL

**Sintoma**: `Communications link failure`

**Solução**:
```bash
# Verificar se o MySQL está rodando
docker-compose ps

# Ou se instalado localmente
sudo systemctl status mysql

# Verificar logs do container
docker-compose logs mysql
```

### 2. Porta 3306 já em uso

**Sintoma**: `Bind for 0.0.0.0:3306 failed: port is already allocated`

**Solução**:
```bash
# Opção 1: Parar o MySQL local
sudo systemctl stop mysql

# Opção 2: Alterar a porta no docker-compose.yml
# Trocar "3306:3306" por "3307:3306"
# E ajustar db.host no application.properties:
# db.host=jdbc:mysql://localhost:3307/biblioteca?useSSL=false&serverTimezone=UTC
```

### 3. Erro ao compilar com Maven

**Sintoma**: Erros de compilação ou dependências não encontradas

**Solução**:
```bash
# Limpar cache do Maven
mvn clean

# Forçar atualização das dependências
mvn clean install -U

# Verificar repositório Maven
cat ~/.m2/settings.xml
```

### 4. Migrações Flyway falhando

**Sintoma**: `Flyway migration failed`

**Solução**:
```sql
-- Conectar ao MySQL e resetar o schema do Flyway
USE biblioteca;
DROP TABLE IF EXISTS flyway_schema_history;

-- Reexecutar a aplicação
```

### 5. JWT Token inválido

**Sintoma**: Erro 401 ou 403 ao acessar endpoints protegidos

**Solução**:
- Verifique se a propriedade `jwt` está configurada em `src/main/resources/application.properties`
- Certifique-se de que tem pelo menos 32 caracteres
- Verifique se o token não expirou (padrão: 24 horas configurado em `expiration`)
- Exemplo:
```properties
jwt=MinhaChaveSecretaSuperSeguraComMaisDe32Caracteres
expiration=86400000
```

### 6. Java 17 não encontrado

**Sintoma**: `JAVA_HOME is not set`

**Solução**:
```bash
# Ubuntu/Debian
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

---

## Endpoints da API

### Autenticação
- `POST /api/auth/login` - Login de usuário
- `POST /api/auth/register` - Registro de novo usuário

### Usuários (requer autenticação)
- `GET /api/usuarios` - Listar usuários
- `GET /api/usuarios/{id}` - Buscar usuário por ID
- `PUT /api/usuarios/{id}` - Atualizar usuário
- `DELETE /api/usuarios/{id}` - Deletar usuário

### Autores (requer autenticação)
- `GET /api/autores` - Listar autores
- `POST /api/autores` - Criar autor
- `GET /api/autores/{id}` - Buscar autor por ID
- `GET /api/autores/{id}/livros` - Listar livros do autor
- `PUT /api/autores/{id}` - Atualizar autor
- `DELETE /api/autores/{id}` - Deletar autor

### Categorias (requer autenticação)
- `GET /api/categorias` - Listar categorias
- `POST /api/categorias` - Criar categoria
- `GET /api/categorias/{id}` - Buscar categoria por ID
- `PUT /api/categorias/{id}` - Atualizar categoria
- `DELETE /api/categorias/{id}` - Deletar categoria

### Livros (requer autenticação)
- `GET /api/livros` - Listar livros
- `POST /api/livros` - Criar livro
- `GET /api/livros/{id}` - Buscar livro por ID
- `PUT /api/livros/{id}` - Atualizar livro
- `DELETE /api/livros/{id}` - Deletar livro

---

## Suporte

Para mais informações, consulte:
- Documentação do Restlet: https://restlet.com/documentation/
- Documentação do Flyway: https://flywaydb.org/documentation/
- MySQL Documentation: https://dev.mysql.com/doc/

---

## Licença

Este projeto é parte de um trabalho acadêmico da UFES.