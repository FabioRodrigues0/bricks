<p align="center">
  <img src="assets/bricks-logo.svg" alt="Bricks" width="800">
</p>

<p align="center">
  <a href="https://jitpack.io/#FabioRodrigues0/bricks"><img src="https://jitpack.io/v/FabioRodrigues0/bricks.svg" alt="Latest Version"></a>
  <a href="https://jitci.com/gh/FabioRodrigues0/bricks"><img src="https://jitpack.io/v/FabioRodrigues0/bricks.svg" alt="Latest Version"></a>
  <a href="https://github.com/FabioRodrigues0/bricks/blob/master/LICENSE.md"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <a href="https://fabiorodrigues0.github.io/bricks/javadoc/"><img src="https://img.shields.io/badge/Javadoc-API-orange.svg" alt="Javadoc"></a>
</p>

---

Biblioteca Java para construir interfaces gráficas desktop no estilo declarativo do Jetpack Compose, com backend JavaFX.

## Conceito

Em vez de construir UIs com XML ou arrastando componentes, o Bricks usa uma API fluente em Java puro — semelhante ao Compose do Android, mas para aplicações desktop.

```java
public class MinhaApp extends BricksApplication {

    private final State<Integer> contador = state(0);

    { setTitle("Contador"); }

    @Override
    public Component root() {
        return new Column()
            .padding(20)
            .gap(12)
            .children(
                new Text("Valor: " + contador.get()).fontSize(24),
                new Button("Incrementar").onClick(() -> contador.set(contador.get() + 1))
            );
    }

    public static void main(String[] args) { launch(args); }
}
```

---

## Requisitos

- Java 17+
- Maven 3.8+ ou Gradle 8+

---

## Instalação e configuração

### Maven

**`pom.xml` completo de exemplo:**

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.exemplo</groupId>
    <artifactId>minha-app</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <!-- 1. Repositório JitPack -->
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <!-- 2. Dependência do Bricks -->
    <dependencies>
        <dependency>
            <groupId>com.github.fabiorodrigues0</groupId>
            <artifactId>bricks</artifactId>
            <version>0.4.3</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 3. Copiar dependências para target/dependency (necessário para o module-path) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>generate-sources</phase>
                        <goals><goal>copy-dependencies</goal></goals>
                    </execution>
                </executions>
            </plugin>

            <!-- 4. Compilador com module-path -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                    <compilerArgs>
                        <arg>--module-path</arg>
                        <arg>${project.build.directory}/dependency</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <!-- 5. Plugin para correr: mvn javafx:run -->
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.exemplo/com.exemplo.MinhaApp</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Para correr: `mvn javafx:run`

---

### Gradle

**`build.gradle.kts` completo de exemplo:**

```kotlin
plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.exemplo"
version = "1.0.0"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

// 1. Repositório JitPack
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

// 2. Módulos JavaFX necessários
javafx {
    version = "21"
    modules("javafx.controls", "javafx.graphics")
}

// 3. Dependência do Bricks
dependencies {
    implementation("com.github.fabiorodrigues0:bricks:0.4.3")
}

application {
    mainModule.set("com.exemplo")           // nome do módulo em module-info.java
    mainClass.set("com.exemplo.MinhaApp")
}
```

Para correr: `gradle run`

---

## Estrutura do projeto

```
meu-projeto/
├── pom.xml  (ou build.gradle.kts)
├── config/
│   ├── checkstyle/                        ← regras de estilo (opcional)
│   ├── formatter/                         ← regras de formatação (opcional)
│   └── database/
│       └── DatabaseConfig.java           ← ligação à BD (SQLite por defeito, trocar aqui)
├── database/
│   ├── schema/
│   │   └── DatabaseSchema.java           ← definição das tabelas
│   └── seeds/
│       └── DatabaseSeeder.java           ← dados iniciais
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java           ← declara o módulo da app
        │   └── com/exemplo/
        │       └── App.java              ← extends BricksApplication
        └── resources/
            └── imagens/                   ← assets estáticos
```

### `module-info.java`

Necessário para que o Java encontre os módulos do JavaFX e do Bricks:

```java
module com.exemplo {
    requires fabiorodrigues.bricks;
}
```

### `config/database/DatabaseConfig.java`

Para usar MySQL, PostgreSQL ou SQL Server em vez de SQLite, cria este ficheiro em `config/database/`. O Bricks detecta-o automaticamente via `DB.autoConfig()`. Se o ficheiro não existir, usa SQLite e cria `./data/database.db` na raiz do projeto.

```java
package config.database;

import fabiorodrigues.bricks.data.config.*;

public class DatabaseConfig {
    public DbConfig getConfig() {
        return new MySQLConfig()
            .host("localhost")
            .database("minha_bd")
            .user("root")
            .password("pass");
    }
}
```

#### SQL Server (Docker ou Azure SQL)

```java
// Azure SQL (serverless): cada query abre e fecha a ligação para a base poder pausar
return SQLServerConfig.azure("nome-do-servidor", "escola")
    .credentialsFromEnv("DB_USER", "DB_PASSWORD");

// Desenvolvimento local em Docker (localhost:1433, utilizador sa)
return SQLServerConfig.localDocker("escola")
    .password(System.getenv("MSSQL_SA_PASSWORD"));
```

| Modo | Ligação | Certificado | Utilizador |
| --- | --- | --- | --- |
| `azure(servidor, bd)` | Abre e fecha por query (a base serverless pode pausar); repete automaticamente nos erros transitórios `40613`/`40501`/`40197` (base a acordar) | Validado (`*.database.windows.net`) | Definir com `.user()`/`.password()` ou `.credentialsFromEnv()` |
| `localDocker(bd)` | Persistente (`localhost:1433`) | Auto-assinado aceite | `sa` |

O nome do servidor Azure pode ser só o nome (`"meu-servidor"`) ou o host completo. Nunca escrever passwords no código — usar variáveis de ambiente.

Para arrancar um SQL Server local em Docker:

```bash
docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=$MSSQL_SA_PASSWORD" \
    -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest
```

O driver não vem com o Bricks — adiciona-o à app (versão `.jre11` para Java 17+):

```xml
<!-- Maven -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>13.6.0.jre11</version>
    <scope>runtime</scope>
</dependency>
```

```kotlin
// Gradle
runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:13.6.0.jre11")
```

Notas SQL Server:
- paginar com `orderBy`: sem ele a ordem é indefinida (`ORDER BY (SELECT NULL)`), e `SELECT DISTINCT` paginado lança erro;
- upsert gera `MERGE` e precisa de `.conflictOn("coluna_unique")` (aceita várias colunas);
- usar `ColumnType.STRING`/`TEXT` (dão `NVARCHAR`) em vez de escrever `TEXT` à mão;
- `limit`/`offset` em `UPDATE`/`DELETE` lançam erro (não suportado em todos os dialetos).

### `database/schema/DatabaseSchema.java`

Define a estrutura das tabelas. Chamado uma vez no arranque da app via `Effect`:

```java
package database.schema;

import fabiorodrigues.bricks.data.DB;
import fabiorodrigues.bricks.data.dialect.ColumnType;

public class DatabaseSchema {
    public static void run() {
        // ColumnType gera o tipo certo para SQLite, MySQL, PostgreSQL e SQL Server
        DB.query()
            .createTableIfNotExists("utilizadores")
            .column("id", ColumnType.ID)
            .column("nome", ColumnType.STRING, "NOT NULL")
            .column("email", ColumnType.STRING, "NOT NULL UNIQUE")
            .execute();
    }
}
```

### `database/seeds/DatabaseSeeder.java`

Insere dados iniciais. Chamado após o schema estar criado:

```java
package database.seeds;

import fabiorodrigues.bricks.data.DB;
import java.util.Map;

public class DatabaseSeeder {
    public static void run() {
        DB.query()
            .insertInto("utilizadores")
            .values(Map.of("nome", "Admin", "email", "admin@exemplo.com"))
            .execute();
    }
}
```

### Ligar tudo em `App.java`

```java
import fabiorodrigues.bricks.core.*;
import fabiorodrigues.bricks.data.DB;
import database.schema.DatabaseSchema;
import database.seeds.DatabaseSeeder;

public class App extends BricksApplication {

    {
        setTitle("A Minha App");
        DB.autoConfig();  // lê config/database/DatabaseConfig se existir, SQLite por defeito
    }

    private final Effect initDb = effect(() -> {
        DatabaseSchema.run();
        DatabaseSeeder.run();
    });

    @Override
    public Component root() {
        // ...
    }

    public static void main(String[] args) { launch(args); }
}
```

---

## Base de dados

### Query builder

O mesmo código funciona em SQLite, MySQL, PostgreSQL e SQL Server — o dialeto trata das diferenças (aspas, paginação, upsert, tipos de coluna).

```java
// SELECT para objetos
List<Aluno> alunos = DB.query()
    .select("id", "nome", "turma")
    .from("alunos")
    .where("ativo", "=", 1)
    .when(filtroTurma != null, q -> q.where("turma", "=", filtroTurma))
    .orderBy("nome", "ASC")
    .limit(20)
    .execute(Aluno.class);

// INSERT (devolve o id gerado)
int id = DB.query()
    .insertInto("alunos")
    .values(Map.of("nome", "Fabio", "turma", 1))
    .execute();

// Upsert — conflictOn é obrigatório em SQL Server e PostgreSQL (MySQL ignora-o)
DB.query()
    .insertInto("alunos")
    .values(Map.of("email", "a@b.pt", "nome", "Ana"))
    .onDuplicateUpdate("nome")
    .conflictOn("email")
    .execute();
```

### SQL raw

Para o que o builder não cobre (`GROUP BY`, `HAVING`, subqueries, `OR`, `JOIN`s complexos...), `raw()` aceita SQL escrito à mão. Os valores vão sempre por `?` (nunca concatenar strings — evita SQL injection). O SQL **não** é traduzido entre dialetos.

```java
// Com linhas — mapear para objetos
List<TurmaTotal> totais = DB.query()
    .raw("SELECT turma, COUNT(*) AS total FROM alunos WHERE nome LIKE ? GROUP BY turma HAVING COUNT(*) > ?",
         "J%", 2)
    .execute(TurmaTotal.class);

// Com linhas — Map por coluna
QueryResult r = DB.query().raw("SELECT COUNT(*) AS total FROM alunos").executeRaw();
int total = ((Number) r.first().get("total")).intValue();

// Sem linhas — devolve linhas afetadas (ou id gerado num INSERT)
int afetados = DB.query()
    .raw("UPDATE alunos SET turma = turma + 1 WHERE turma < ?", 3)
    .execute();
```

| Método | Uso |
| --- | --- |
| `execute(Classe.class)` | SELECT → `List<Classe>` |
| `executeRaw()` | SELECT → `QueryResult` (linhas como `Map`) |
| `execute()` | INSERT/UPDATE/DELETE → id gerado ou linhas afetadas |
| `executeResult()` | INSERT/UPDATE/DELETE → `QueryResult` com detalhes |

---

## Estrutura interna da lib

```
fabiorodrigues.bricks
├── core/
│   ├── BricksApplication   — classe base da aplicação
│   ├── Component           — interface dos componentes
│   ├── State<T>            — estado reativo
│   ├── ValidatedState<T>   — estado com regras de validação
│   ├── ValidationRule<T>   — regra de validação personalizada
│   └── DerivedState<T>     — estado calculado a partir de outros estados
├── components/             — componentes de UI
├── data/                   — acesso a base de dados
│   ├── DB, Query           — ligação e query builder (+ SQL raw)
│   ├── config/             — SQLite, MySQL, PostgreSQL, SQL Server
│   └── dialect/            — sintaxe de cada base de dados (ColumnType, SqlDialect)
└── style/
    ├── Modifier            — propriedades visuais reutilizáveis
    ├── BricksTheme         — sistema de temas Material 3
    └── ThemeRegistry       — acesso global ao tema ativo
```

---

## Estado reativo

O estado é criado com `state()` dentro de `BricksApplication`. Quando o valor muda, a UI faz re-render automaticamente.

```java
private final State<String> nome = state("");
private final State<Boolean> ativo = state(false);
```

### DerivedState

Estado calculado automaticamente a partir de outros estados, com cache — só recalcula quando uma dependência muda.

```java
private final State<String> filtro = state("");
private final State<List<String>> lista = state(List.of("Ana", "Bruno", "Carlos"));

private final DerivedState<List<String>> filtrados = derived(
    () -> lista.get().stream()
        .filter(n -> n.toLowerCase().contains(filtro.get().toLowerCase()))
        .toList(),
    lista, filtro
);
```

### ValidatedState — validação declarativa

Inspirado no Livewire (Laravel): as regras ficam junto do estado (no ViewModel ou na app), não espalhadas pela UI. `ValidatedState<T>` estende `State<T>`, por isso funciona em qualquer sítio que aceite um `State`.

```java
public class RegistoViewModel extends BricksViewModel {

    public final ValidatedState<String> nome = validatedState("")
        .required("Campo obrigatório")
        .minLength(3, "Mínimo 3 caracteres");

    public final ValidatedState<String> email = validatedState("")
        .required("Campo obrigatório")
        .email("Email inválido");

    public final ValidatedState<Integer> idade = validatedState(null)
        .required("Campo obrigatório")
        .min(18, "Tem de ser maior de idade");

    public void guardar() {
        // só é chamado se todos os campos forem válidos
    }
}
```

`validatedState()` existe em `BricksViewModel`, `BricksScene` e `BricksApplication`.

| Regra | Aplica-se a | Falha quando |
| --- | --- | --- |
| `required(msg)` | qualquer tipo | `null`, ou String vazia/em branco |
| `minLength(n, msg)` / `maxLength(n, msg)` | String | comprimento fora do limite |
| `email(msg)` | String | formato de email inválido |
| `matches(regex, msg)` | String | não corresponde ao regex |
| `min(v, msg)` / `max(v, msg)` | Number | valor fora do limite |
| `rule(v -> ...)` | qualquer tipo | a lambda devolve uma mensagem (≠ `null`) |

`email` e `matches` ignoram valores vazios — combinar com `required` se o campo for obrigatório. A validação para na primeira regra que falha.

| Método | Descrição |
| --- | --- |
| `validate()` | Valida, torna o erro visível e faz re-render. Devolve `true` se válido |
| `isValid()` | Valida em silêncio (não mostra erros) |
| `getError()` | Mensagem de erro visível, ou `null` |
| `clearError()` | Esconde o erro (ex: depois de limpar o form) |

### Form

Agrupa campos, valida tudo ao submeter e só chama `onSubmit` se todos forem válidos. Um `TextField` ligado a um `ValidatedState` com `bindTo` mostra o erro por baixo do campo (com borda vermelha) — os erros só aparecem depois da primeira tentativa de submeter.

```java
new Form()
    .field(new TextField().label("Nome:").bindTo(vm.nome))
    .field(new TextField().label("Email:").bindTo(vm.email))
    .field(new TextField().label("Idade:").number().bindTo(vm.idade))
    .field(new Dropdown<>(...).bindTo(vm.categoria), vm.categoria) // outros campos: passar o state
    .submitLabel("Guardar")
    .gap(12)
    .onSubmit(vm::guardar)
```

`form.submit()` faz o mesmo que clicar no botão (útil para ligar ao Enter). O erro mantém-se até ao próximo submit; depois de guardar, chamar `clearError()` nos states para limpar.

---

## Componentes

### Layout

| Componente   | Descrição                               |
| ------------ | --------------------------------------- |
| `Column`     | Filhos dispostos verticalmente          |
| `Row`        | Filhos dispostos horizontalmente        |
| `Box`        | Filhos empilhados (StackPane)           |
| `ScrollView` | Contentor com scroll                    |
| `Spacer`     | Espaço flexível entre elementos         |
| `Divider`    | Linha separadora horizontal ou vertical |

```java
new Column()
    .padding(16)
    .gap(8)
    .modifier(new Modifier().alignment(Pos.CENTER))
    .children(
        new Row().gap(8).children(
            new Button("A"),
            new Button("B")
        ),
        new Divider(),
        new Text("Rodapé")
    )
```

### Texto e entrada

| Componente  | Descrição                             |
| ----------- | ------------------------------------- |
| `Text`      | Texto estático                        |
| `TextField` | Campo de texto (simples ou multiline) |
| `Button`    | Botão clicável                        |
| `Checkbox`  | Caixa de seleção                      |
| `Dropdown`  | Lista de seleção                      |
| `Slider`    | Controlo deslizante                   |
| `Form`      | Agrupa campos com validação (ver [Form](#form)) |

```java
new TextField()
    .placeholder("Escreve algo...")
    .bindTo(texto)

new Checkbox()
    .label("Aceito os termos")
    .onChange(valor -> aceite.set(valor))

new Dropdown()
    .options("Opção 1", "Opção 2", "Opção 3")
    .bindTo(selecao)

new Slider().min(0).max(100).bindTo(volume)
```

### Botão com estado de desativado

```java
new Button("Guardar")
    .enabled(formularioValido)      // State<Boolean>
    .onDisabledClick(() -> Alert.warning("Atenção", "Preenche todos os campos"))
    .onClick(() -> guardar())
```

### Progresso e media

| Componente    | Descrição                             |
| ------------- | ------------------------------------- |
| `ProgressBar` | Barra de progresso (ou indeterminado) |
| `Image`       | Imagem a partir de ficheiro ou URL    |
| `Icon`        | Ícone FontAwesome 5                   |

```java
new ProgressBar().value(0.75)
new ProgressBar().indeterminate()

new Icon("fas-check").size(24)
```

### Diálogos

```java
// Diálogos estáticos
Alert.info("Título", "Mensagem");
Alert.warning("Atenção", "Texto de aviso");
Alert.error("Erro", "Algo correu mal");
boolean confirmado = Alert.confirm("Apagar?", "Esta ação é irreversível.");

// Ou com API fluente
new Alert("Título", "Mensagem").type(Alert.Type.WARNING).show();
```

### FilePicker

```java
new FilePicker()
    .title("Escolher imagem")
    .filter("Imagens", "*.png", "*.jpg", "*.jpeg")
    .onSelect(ficheiro -> caminho.set(ficheiro.getAbsolutePath()))
```

### Imagens da app e do utilizador

```java
{
    setTitle("Clientes");
    setPathUserData(System.getProperty("user.home") + "/.clientes-app/uploads");
}

// Imagem interna da app: src/main/resources/logo.png
new Image("/logo.png").width(120);

// Guarda imagem escolhida pelo utilizador em ~/.clientes-app/uploads/clientes/<id>/<nome>
new FilePicker()
    .title("Escolher foto")
    .filter("Imagens", "*.png", "*.jpg", "*.jpeg")
    .pathToUserData()
    .saveTo(file -> "clientes/" + id + "/" + file.getName());

// Carrega imagem relativa a pasta configurada por setPathUserData(...)
Image.userData("clientes/" + id + "/foto.png").size(64);

new Card()
    .coverImageUserData("clientes/" + id + "/foto.png", 160);
```

---

## Modifier

O `Modifier` define propriedades visuais reutilizáveis entre componentes.

```java
Modifier titulo = new Modifier()
    .fontSize(28)
    .bold()
    .textColor(Color.web("#1a1a2e"));

Modifier cartao = new Modifier()
    .background(Color.WHITE)
    .borderRadius(12)
    .padding(16)
    .border(Color.LIGHTGRAY, 1);

new Text("Boas-vindas").modifier(titulo)
new Column().modifier(cartao).children(...)
```

### Propriedades disponíveis

| Categoria   | Métodos                                                      |
| ----------- | ------------------------------------------------------------ |
| Dimensões   | `width`, `height`, `size`, `fillMaxWidth`, `fillMaxHeight`   |
| Espaçamento | `padding`, `margin`, `gap`                                   |
| Layout      | `alignment(Pos)`                                             |
| Texto       | `fontSize`, `fontFamily`, `bold`, `italic`, `textColor`      |
| Visual      | `background`, `border`, `borderRadius`, `opacity`, `visible` |

---

## Tema

O tema Material 3 light é aplicado automaticamente. Para personalizar:

```java
{ setTitle("App"); }  // inicializador de instância, antes de root()

// Tema escuro
setTheme(BricksTheme.dark());

// Tema personalizado — alterar cor primária
setTheme(BricksTheme.material()
    .colorScheme()
        .primary(Color.web("#6750A4"))
        .secondary(Color.web("#958DA5"))
    .and());
```

### Aceder ao tema nos componentes

```java
BricksTheme tema = BricksTheme.current();
Color primaria = tema.colorScheme().primary();
double raioMedio = tema.shapes().medium();
```

---

## Executar testes

```bash
mvn test
```
