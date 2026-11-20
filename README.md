# EM DESENVOLVIMENTO
# Sistema de Avaliações: Pesquisa de Satisfação
Projeto da disciplina **Banco de Dados I (UEL)**, Prof. Dr. Anderson Paulo Avila Santos.

Sistema web de avaliações, com banco de dados PostgreSQL, em que é possível cadastrar questões, montar avaliações com essas questões, indicar quem deve responder cada avaliação, registrar as respostas e, no fim, ver relatórios com tabelas e gráficos sobre os resultados.

**Integrantes:** Ana Vitória Oliver Machado (Ciência da Computação)

## Sumário
- [Natureza escolhida](#natureza-escolhida)
- [Como rodar](#como-rodar)
- [Descrição dos relatórios](#descrição-dos-relatórios)
---
## Natureza escolhida
Pesquisa de satisfação ou de opinião.
**Particularidade:** Questões em escala de 1 a 5 agrupadas por tema. Respostas podem ser de escolha ou texto livre. Os relatórios comparam temas (infraestrutura, atendimento, conteúdo etc.), períodos e grupos de  respondentes.

As respostas ficam identificadas (ligadas ao usuário).

### Tecnologias

| Parte | Tecnologia |
|---|---|
| Banco de dados | PostgreSQL |
| Back-end | Java com Spring Boot, acesso ao banco por JDBC (`JdbcTemplate`), com DAOs sem JPA/Hibernate |
| Front-end | Thymeleaf (a confirmar com o professor) e Chart.js para os gráficos |
| Build | Maven |

### Estrutura do repositório
```
Bd1Projeto/
  |README.md
  |docs/ desenho do DER, esquema relacional e justificativa da 3FN
  |database/
      |schema.sql: cria o banco do zero, com todas as restrições
      |carga.sql: dados de teste
      |relatorios.sql: consultas dos relatórios
  |app/ projeto Spring Boot (controllers, model, DAOs, telas)
```
### Modelo dos dados
esquema relacional:
| Tabela | Papel |
|---|---|
| `grupo` | turma ou curso dos usuários |
| `tema` | tema ao qual cada questão pertence |
| `usuario` | quem responde (e-mail e senha com hash) |
| `questao` | questão de tipo `ESCALA` ou `TEXTO`, ligada a um tema |
| `avaliacao` | a pesquisa, com título, descrição, data e horários |
| `avaliacao_questao` | quais questões compõem cada avaliação, e em que ordem |
| `avaliacao_usuario` | quais usuários devem responder cada avaliação |
| `resposta` | resposta de um usuário a uma questão de uma avaliação (`valor` de 1 a 5 ou `texto`) |

As restrições de integridade (`PRIMARY KEY`, `FOREIGN KEY`, `NOT NULL`, `UNIQUE`, `CHECK`) estão no `database/schema.sql`.

---
## Como rodar
### Requisitos
- JDK 17 ou superior
- PostgreSQL 13 ou superior, com a extensão `pgcrypto`  (já vem com a instalação padrão)
- Maven não precisa ser instalado pois o projeto usa o `mvnw`

**Requisitos**
- JDK 17 ou superior
- PostgreSQL 13 ou superior, com a extensão `pgcrypto`  (já vem com a instalação padrão)
- Maven não precisa ser instalado pois o projeto usa o `mvnw`

### 1. Criar o banco
No pgAdmin (ou no `psql`), crie um banco vazio chamado `avaliacoes`:

```sql
CREATE DATABASE avaliacoes;
```


### 2. Rodar os scripts, nesta ordem

Com o banco `avaliacoes` selecionado, execute primeiro `database/schema.sql` e depois `database/carga.sql`. Pelo `psql`:

```bash
psql -U postgres -d avaliacoes -f database/schema.sql
psql -U postgres -d avaliacoes -f database/carga.sql
```
obs: sempre que rodar o `schema.sql` de novo, rode o `carga.sql` em seguida (o `schema.sql` apaga e recria as tabelas).


### 3. Configurar a conexão

Em `app/src/main/resources/application.properties`, ajuste o usuário e a senha do PostgreSQL da sua máquina:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/avaliacoes
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

### 4. Subir a aplicação

```bash
cd app
./mvnw spring-boot:run
```

No Windows: `mvnw spring-boot:run`. A aplicação fica em <http://localhost:8080>.

## Dados para teste
**login**
| E-mail | Senha |
|---|---|
| `usuario1@exemplo.com` | `123456` |

Todos os 24 usuários da carga (`usuario1@exemplo.com` até `usuario24@exemplo.com`) usam a mesma senha.


**dados**

  A carga cria 4 grupos, 5 temas, 24 usuários, 30 questões (25 de escala e 5 de texto livre), 5 avaliações e mais de mil respostas. As notas de escala são geradas com `random()` a partir de uma semente fixa (`setseed`), então a carga produz sempre os mesmos dados.

---
## Descrição dos relatórios

Cada relatório tem uma tabela e um gráfico, em [`database/relatorios.sql`](database/relatorios.sql).

### Relatório 1: média por tema em cada avaliação

- **Objetivo:** comparar a satisfação média de cada tema ao longo das avaliações.
- **Gráfico:** barras (média por tema, por avaliação).
- **Recursos SQL:** JOIN, agregação (`AVG`, `COUNT`), `GROUP BY`, `ORDER BY`.
- **Tela:** _(inserir imagem)_

```sql
SELECT a.titulo, t.nome AS tema,
       ROUND(AVG(r.valor), 2) AS media,
       COUNT(r.valor)         AS respostas
FROM resposta r
JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
JOIN questao q   ON q.id_questao   = r.id_questao
JOIN tema t      ON t.id_tema      = q.id_tema
GROUP BY a.id_avaliacao, a.titulo, a.data_aplicacao, t.id_tema, t.nome
ORDER BY a.data_aplicacao, t.nome;
```

### Relatório 2: quem ainda não respondeu cada avaliação

- **Objetivo:** listar os usuários designados a uma avaliação que ainda não responderam, e mostrar quantos são por avaliação.
- **Gráfico:** barras (pendentes por avaliação).
- **Recursos SQL:** `LEFT JOIN` (junção externa), JOIN, `GROUP BY`, `ORDER BY`.
- **Tela:** _(inserir imagem)_

Tabela (quem falta):

```sql
SELECT a.titulo, u.nome AS usuario, g.nome AS grupo
FROM avaliacao_usuario au
JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
JOIN usuario u   ON u.id_usuario   = au.id_usuario
JOIN grupo g     ON g.id_grupo     = u.id_grupo
LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao
                    AND r.id_usuario   = au.id_usuario
WHERE r.id_usuario IS NULL
ORDER BY a.titulo, u.nome;
```

Gráfico (pendentes por avaliação):

```sql
SELECT a.titulo, COUNT(*) AS pendentes
FROM avaliacao_usuario au
JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao
                    AND r.id_usuario   = au.id_usuario
WHERE r.id_usuario IS NULL
GROUP BY a.id_avaliacao, a.titulo, a.data_aplicacao
ORDER BY a.data_aplicacao;
```

### Relatório 3: média de cada grupo comparada com a média geral

- **Objetivo:** ver quais grupos estão acima ou abaixo da média geral num período, escolhendo o período e os grupos.
- **Gráfico:** barras lado a lado (média do grupo e média geral).
- **Recursos SQL:** subconsulta, `HAVING`, filtro com datas (`BETWEEN`) e com `IN`, JOIN, `GROUP BY`.
- **Tela:** _(inserir imagem)_

```sql
SELECT g.nome AS grupo,
       ROUND(AVG(r.valor), 2) AS media_do_grupo,
       (SELECT ROUND(AVG(valor), 2) FROM resposta) AS media_geral
FROM resposta r
JOIN usuario u   ON u.id_usuario   = r.id_usuario
JOIN grupo g     ON g.id_grupo     = u.id_grupo
JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
WHERE a.data_aplicacao BETWEEN '2026-01-01' AND '2026-12-31'
  AND g.nome IN ('CC-2026', 'Engenharia', 'Medicina')
GROUP BY g.nome
HAVING COUNT(r.valor) >= 10
ORDER BY media_do_grupo DESC;
```

### Relatório 4: ranking das 10 questões de escala mais bem avaliadas

- **Objetivo:** identificar os itens com melhor avaliação média.
- **Gráfico:** barras horizontais (média por questão).
- **Recursos SQL:** `RANK()`, `FETCH FIRST`, JOIN, `GROUP BY`, filtro com `LIKE`.
- **Tela:** _(inserir imagem)_

```sql
SELECT RANK() OVER (ORDER BY AVG(r.valor) DESC) AS posicao,
       q.enunciado,
       ROUND(AVG(r.valor), 2) AS media
FROM questao q
JOIN resposta r ON r.id_questao = q.id_questao
WHERE q.tipo = 'ESCALA'
  AND q.enunciado LIKE '%Pergunta%'
GROUP BY q.id_questao, q.enunciado
ORDER BY posicao
FETCH FIRST 10 ROWS ONLY;
```

