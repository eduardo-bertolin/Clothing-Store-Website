# Clothing Store — Backend

API REST para um e-commerce de roupas, desenvolvida com Spring Boot 3.5.5 e arquitetura hexagonal.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.5 | Framework web |
| Spring Security + JWT | — | Autenticação e autorização |
| MySQL | 8.0 | Banco de dados relacional |
| Flyway | 11.13.1 | Migrations do banco |
| Cloudinary | 2.3.2 | Armazenamento de imagens |
| Docker + Docker Compose | — | Containerização |

---

## Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Java 21](https://adoptium.net/) _(apenas para rodar localmente sem Docker)_
- [Maven](https://maven.apache.org/) _(apenas para rodar localmente sem Docker)_
- Conta no [Cloudinary](https://cloudinary.com/) _(gratuita)_

---

## Configuração do arquivo `.env`

Crie um arquivo `.env` na raiz do projeto (`backend/clothing_store/`):

```env
# MySQL
MYSQL_ROOT_PASSWORD=sua_senha_root
MYSQL_DATABASE=vertice_database
MYSQL_USER=clothing_user
MYSQL_PASSWORD=sua_senha

# Conexão com o banco (usado pelo Spring)
DATABASE_URL=jdbc:mysql://mysql:3306/vertice_database
DATABASE_USERNAME=clothing_user
DATABASE_PASSWORD=sua_senha

# JWT
JWT_SECRET_KEY=sua_chave_base64_de_256bits
ACCESS_TOKEN_EXPIRATION=900000
REFRESH_TOKEN_EXPIRATION=604800000

# Servidor
SERVER_PORT=8080

# Cloudinary
CLOUDINARY_CLOUD_NAME=seu_cloud_name
CLOUDINARY_API_KEY=sua_api_key
CLOUDINARY_SECRET_KEY=seu_api_secret
```

### Gerando a chave JWT

Execute o comando abaixo para gerar uma chave segura:

```bash
openssl rand -base64 32
```

### Credenciais do Cloudinary

Acesse o [Cloudinary Dashboard](https://console.cloudinary.com/) e copie os valores de:
- Cloud Name
- API Key
- API Secret

---

## Como executar

### Opção 1 — Docker (recomendado)

Sobe o MySQL e o backend em containers com um único comando:

```bash
docker compose up -d --build
```

A API estará disponível em `http://localhost:8080`.

Para acompanhar os logs em tempo real:

```bash
docker compose logs -f backend
```

Para parar e remover os containers:

```bash
docker compose down
```

> **Atenção:** Para resetar o banco de dados completamente (apaga todos os dados), execute:
> ```bash
> docker compose down
> rm -rf ./mysql-data
> docker compose up -d --build
> ```

---

### Opção 2 — Spring Boot local + MySQL no Docker

Use esta opção quando quiser reiniciar a aplicação rapidamente sem rebuildar a imagem Docker.

**1. Suba apenas o MySQL:**

```bash
docker compose up -d mysql
```

**2. Rode o Spring Boot localmente:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

O profile `local` sobrescreve o host do banco para `localhost`, já que fora do Docker o hostname `mysql` não existe.

---

## Conexão com o banco via MySQL Workbench

| Campo | Valor |
|---|---|
| Hostname | `127.0.0.1` |
| Porta | `3336` |
| Usuário | `clothing_user` |
| Senha | `sua_senha` |
| Schema padrão | `vertice_database` |

> Para acesso root completo, use usuário `root` e a senha definida em `MYSQL_ROOT_PASSWORD`.

---

## Papéis de usuário

| Papel | Descrição |
|---|---|
| `ROLE_USER` | Atribuído automaticamente no cadastro. Acesso às rotas de carrinho, pedidos e perfil. |
| `ROLE_ADMIN` | Gerenciamento de produtos, categorias, coleções e pedidos. |
| `ROLE_SUPER_ADMIN` | Acesso total, incluindo exclusão e edição de categorias e coleções. |

---

## Como criar um usuário Admin

Não existe endpoint público para promover um usuário a Admin. O processo é feito em duas etapas.

### Etapa 1 — Cadastrar o usuário via API

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_loja",
    "email": "admin@loja.com",
    "password": "Senha@123"
  }'
```

> **Regras de senha:** mínimo 8 caracteres, ao menos 1 letra maiúscula, 1 número e 1 caractere especial.

A resposta retorna um `accessToken` e o `id` do usuário criado:

```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "...",
  "user": {
    "id": 1,
    "username": "admin_loja",
    "email": "admin@loja.com"
  }
}
```

### Etapa 2 — Promover a Admin no banco de dados

Conecte-se ao banco (via MySQL Workbench ou terminal) e execute o SQL abaixo substituindo pelo `id` do usuário retornado no passo anterior.

**Para ROLE_ADMIN:**

```sql
INSERT INTO usuarios_papeis (id_usuario, id_papel)
SELECT 1, id FROM papeis WHERE nome_papel = 'ROLE_ADMIN';
```

**Para ROLE_SUPER_ADMIN:**

```sql
INSERT INTO usuarios_papeis (id_usuario, id_papel)
SELECT 1, id FROM papeis WHERE nome_papel = 'ROLE_SUPER_ADMIN';
```

> Substitua `1` pelo `id` real do usuário.

**Via terminal Docker (sem precisar do Workbench):**

```bash
docker exec -it mysql_db mysql -u root -prootsecret vertice_database \
  -e "INSERT INTO usuarios_papeis (id_usuario, id_papel) SELECT 1, id FROM papeis WHERE nome_papel = 'ROLE_ADMIN';"
```

### Verificar os papéis do usuário

```sql
SELECT u.username, p.nome_papel
FROM usuarios u
JOIN usuarios_papeis up ON u.id = up.id_usuario
JOIN papeis p ON up.id_papel = p.id
WHERE u.id = 1;
```

---

## Endpoints da API

### Autenticação — `/api/auth` (público)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Cadastro de novo usuário |
| `POST` | `/api/auth/login` | Login e geração de tokens |
| `POST` | `/api/auth/refresh` | Renovação do access token |
| `POST` | `/api/auth/logout` | Invalidação do refresh token |

#### Exemplo de login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_loja",
    "password": "Senha@123"
  }'
```

---

### Catálogo — `/api/catalog` (público)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/catalog/variations` | Listar variações com filtros |
| `GET` | `/api/catalog/variations/{id}` | Buscar variação por ID |

**Filtros disponíveis em `/api/catalog/variations`:**

```
?categoryId=1&collectionId=2&colorId=3&sizeId=4&inStock=true&page=0&size=20
```

---

### Admin — Produtos (requer `ROLE_ADMIN` ou `ROLE_SUPER_ADMIN`)

Todas as rotas abaixo exigem o header `Authorization: Bearer <accessToken>`.

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/admin/products` | Criar produto |
| `GET` | `/api/admin/products` | Listar todos os produtos |
| `GET` | `/api/admin/products/{id}` | Buscar produto por ID |
| `PATCH` | `/api/admin/products/{id}` | Atualizar produto (JSON Patch) |
| `DELETE` | `/api/admin/products/{id}` | Deletar produto |
| `POST` | `/api/admin/products/images` | Upload de imagens (multipart/form-data) |

#### Exemplo — Criar produto

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{
    "name": "Camiseta Básica",
    "description": "Camiseta 100% algodão",
    "price": 59.90,
    "categoryId": 1,
    "collectionId": 1
  }'
```

#### Exemplo — Atualizar produto (JSON Patch)

```bash
curl -X PATCH http://localhost:8080/api/admin/products/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '[
    { "op": "replace", "path": "/price", "value": 49.90 },
    { "op": "replace", "path": "/name", "value": "Camiseta Premium" }
  ]'
```

---

### Admin — Variações de Produto

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/admin/products/{productId}/variations` | Criar variação |
| `PATCH` | `/api/admin/products/{productId}/variations/{variationId}` | Atualizar variação |
| `DELETE` | `/api/admin/products/{productId}/variations/{variationId}` | Deletar variação |

#### Exemplo — Criar variação

```bash
curl -X POST http://localhost:8080/api/admin/products/1/variations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{
    "colorId": 1,
    "sizeId": 2,
    "stock": 50
  }'
```

---

### Admin — Categorias

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/admin/categories` | ADMIN | Criar categoria |
| `GET` | `/api/admin/categories` | ADMIN | Listar categorias |
| `GET` | `/api/admin/categories/{id}` | ADMIN | Buscar por ID |
| `PATCH` | `/api/admin/categories/{id}` | SUPER_ADMIN | Atualizar categoria |
| `DELETE` | `/api/admin/categories/{id}` | SUPER_ADMIN | Deletar categoria |

#### Exemplo — Criar categoria

```bash
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{ "name": "Camisetas" }'
```

---

### Admin — Coleções

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/admin/collections` | ADMIN | Criar coleção |
| `GET` | `/api/admin/collections` | ADMIN | Listar coleções |
| `GET` | `/api/admin/collections/{id}` | ADMIN | Buscar por ID |
| `PATCH` | `/api/admin/collections/{id}` | SUPER_ADMIN | Atualizar coleção |
| `DELETE` | `/api/admin/collections/{id}` | SUPER_ADMIN | Deletar coleção |

#### Exemplo — Criar coleção

```bash
curl -X POST http://localhost:8080/api/admin/collections \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{
    "name": "Verão 2025",
    "description": "Coleção de verão",
    "releaseDate": "2025-10-01"
  }'
```

---

## Migrations do banco

As migrations ficam em `src/main/resources/db/migrations/` e são executadas automaticamente pelo Flyway na inicialização.

| Versão | Descrição |
|---|---|
| V1 | Criação das tabelas principais |
| V2 | Chaves estrangeiras |
| V3–V5 | Estados e cidades brasileiras |
| V6 | Papéis de usuário (ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN) |
| V7 | Tabela de refresh tokens |
| V8–V9 | Cores e tamanhos padrão |
| V10–V11 | Variações de produto, carrinho e pedidos |
| V12 | Tabela de imagens de produto |

---

## Estrutura do projeto

```
src/main/java/com/fhcs/clothing_store/
├── application/
│   ├── port/in/service/       # Interfaces dos casos de uso
│   └── port/out/              # Interfaces de saída (repositórios, JWT, etc.)
├── core/domain/
│   ├── bo/                    # Objetos de negócio (Business Objects)
│   └── exception/             # Exceções de domínio
├── infrastructure/
│   ├── in/rest/
│   │   ├── controller/        # Controllers REST (adapters de entrada)
│   │   ├── dto/               # DTOs de request e response
│   │   ├── mapper/            # Mapeadores DTO ↔ BO
│   │   └── security/          # Filtro JWT e UserDetails
│   └── out/
│       ├── external/          # Integrações externas (ViaCEP, Cloudinary)
│       └── persistence/       # Entidades JPA, repositórios, mappers
└── config/                    # Configurações de segurança, CORS e beans
```
