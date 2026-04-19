<p align="center">
  <img src="assets/bricks-logo.svg" alt="Bricks" width="800">
</p>

<p align="center">
  <a href="https://jitpack.io/#FabioRodrigues0/bricks"><img src="https://jitpack.io/v/FabioRodrigues0/bricks.svg" alt="Latest Version"></a>
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

Para usar MySQL ou PostgreSQL em vez de SQLite, cria este ficheiro em `config/database/`. O Bricks detecta-o automaticamente via `DB.autoConfig()`. Se o ficheiro não existir, usa SQLite e cria `./data/database.db` na raiz do projeto.

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

### `database/schema/DatabaseSchema.java`

Define a estrutura das tabelas. Chamado uma vez no arranque da app via `Effect`:

```java
package database.schema;

import fabiorodrigues.bricks.data.DB;

public class DatabaseSchema {
    public static void run() {
        DB.query()
            .createTableIfNotExists("utilizadores")
            .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
            .column("nome", "TEXT NOT NULL")
            .column("email", "TEXT NOT NULL")
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

## Estrutura interna da lib

```
fabiorodrigues.bricks
├── core/
│   ├── BricksApplication   — classe base da aplicação
│   ├── Component           — interface dos componentes
│   ├── State<T>            — estado reativo
│   └── DerivedState<T>     — estado calculado a partir de outros estados
├── components/             — componentes de UI
├── data/                   — acesso a base de dados (DB, Query, configs)
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
