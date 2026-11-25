# Esquema relacional

Notação: sublinhado = chave primária · ↳ = chave estrangeira · NN = NOT NULL · UQ = UNIQUE.

**GRUPO**(i̲d̲_̲g̲r̲u̲p̲o̲, nome)

**TEMA**(i̲d̲_̲t̲e̲m̲a̲, nome)

**USUARIO**(i̲d̲_̲u̲s̲u̲a̲r̲i̲o̲, nome, email, senha_hash, administrador, id_grupo)

- ↳ id_grupo → Grupo (NN)

**QUESTAO**(i̲d̲_̲q̲u̲e̲s̲t̲a̲o̲, enunciado, tipo, id_tema)

- ↳ id_tema → Tema (NN)

**AVALIACAO**(i̲d̲_̲a̲v̲a̲l̲i̲a̲c̲a̲o̲, titulo, descricao, data_aplicacao, hora_inicio, hora_fim)

**AVALIACAO_QUESTAO**(i̲d̲_̲a̲v̲a̲l̲i̲a̲c̲a̲o̲, i̲d̲_̲q̲u̲e̲s̲t̲a̲o̲, ordem)

- ↳ id_avaliacao → Avaliacao
- ↳ id_questao → Questao

**AVALIACAO_USUARIO**(i̲d̲_̲a̲v̲a̲l̲i̲a̲c̲a̲o̲, i̲d̲_̲u̲s̲u̲a̲r̲i̲o̲)

- ↳ id_avaliacao → Avaliacao
- ↳ id_usuario → Usuario

**RESPOSTA**(i̲d̲_̲u̲s̲u̲a̲r̲i̲o̲, i̲d̲_̲a̲v̲a̲l̲i̲a̲c̲a̲o̲, i̲d̲_̲q̲u̲e̲s̲t̲a̲o̲, valor, texto)

- ↳ (id_avaliacao, id_usuario) → Avaliacao_Usuario
- ↳ (id_avaliacao, id_questao) → Avaliacao_Questao

## Como o DER virou essas tabelas

- Cada entidade virou uma tabela, e o identificador virou a chave primária.
- **pertence** (Grupo 1:N Usuario): a chave estrangeira `id_grupo` ficou em `USUARIO`, que é o lado N.
- **agrupa** (Tema 1:N Questao): a chave estrangeira `id_tema` ficou em `QUESTAO`.
- **compõe** (Avaliacao N:M Questao, a agregação do DER): virou a relação própria `AVALIACAO_QUESTAO`, com a chave duas vezes; `ordem` é atributo do relacionamento.
- **faz** (Usuario N:M Avaliacao): virou a relação própria `AVALIACAO_USUARIO`.
- **responde** (Usuario N:M a agregação `compõe`): virou `RESPOSTA`, com a chave do usuário e a chave da agregação (`id_avaliacao`, `id_questao`); `valor` e `texto` são atributos do relacionamento. As duas chaves estrangeiras compostas garantem que o usuário foi designado à avaliação e que a questão faz parte dela.
- O atributo `administrador` de `USUARIO` define o perfil (administrador ou respondente).

## Restrições além das chaves

- UQ: `USUARIO.email`, `GRUPO.nome`, `TEMA.nome`, `(id_avaliacao, ordem)` em `AVALIACAO_QUESTAO`.
- `QUESTAO.tipo` ∈ {`ESCALA`, `TEXTO`}; `RESPOSTA.valor` entre 1 e 5.
- Em `RESPOSTA`, exatamente um entre `valor` e `texto` é preenchido.
- Em `AVALIACAO`, `hora_fim` é maior que `hora_inicio`.

## Justificativa da 3FN

O esquema está na 3FN. Todas as relações têm chave primária e atributos atômicos (1FN). Nas relações de chave composta (`AVALIACAO_QUESTAO`, `AVALIACAO_USUARIO` e `RESPOSTA`), todo atributo não-chave depende da chave inteira: `ordem` depende do par avaliação e questão, e `valor` e `texto` dependem do trio usuário, avaliação e questão (2FN). Nenhum atributo não-chave depende de outro atributo não-chave: o nome do tema só existe em `TEMA` e o nome do grupo só em `GRUPO`, e `QUESTAO` guarda apenas o `id_tema` (3FN).

## DER

O desenho do DER está em [`DER.png`](DER.png).
