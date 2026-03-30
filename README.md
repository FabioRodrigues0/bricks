# Bricks

Biblioteca Java para construir interfaces gráficas desktop no estilo declarativo do Jetpack Compose, com backend JavaFX.

[![](https://jitpack.io/v/FabioRodrigues0/bricks.svg)](https://jitpack.io/#FabioRodrigues0/bricks)

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
- Maven 3.8+

---

## Instalação

### Maven

Primeiro, adicione o repositório JitPack ao seu `pom.xml`:

```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Depois, adicione a dependência do Bricks:

```xml
<dependency>
    <groupId>com.github.fabiorodrigues0</groupId>
    <artifactId>bricks</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Gradle

Primeiro, adicione o repositório JitPack ao seu `build.gradle`:


```kotlin
repositories {
    mavenLocal()
    maven {'https://jitpack.io'}
}
```
Depois, adicione a dependência do Bricks:

```kotlin
dependencies {
    implementation("com.github.fabiorodrigues0:bricks:0.3.0")
}
```

Para instalar localmente: `mvn install`

---

## Estrutura

```
fabiorodrigues.bricks
├── core/
│   ├── BricksApplication   — classe base da aplicação
│   ├── Component           — interface dos componentes
│   ├── State<T>            — estado reativo
│   └── DerivedState<T>     — estado calculado a partir de outros estados
├── components/             — componentes de UI
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

| Componente | Descrição |
|---|---|
| `Column` | Filhos dispostos verticalmente |
| `Row` | Filhos dispostos horizontalmente |
| `Box` | Filhos empilhados (StackPane) |
| `ScrollView` | Contentor com scroll |
| `Spacer` | Espaço flexível entre elementos |
| `Divider` | Linha separadora horizontal ou vertical |

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

| Componente | Descrição |
|---|---|
| `Text` | Texto estático |
| `TextField` | Campo de texto (simples ou multiline) |
| `Button` | Botão clicável |
| `Checkbox` | Caixa de seleção |
| `Dropdown` | Lista de seleção |
| `Slider` | Controlo deslizante |

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

| Componente | Descrição |
|---|---|
| `ProgressBar` | Barra de progresso (ou indeterminado) |
| `Image` | Imagem a partir de ficheiro ou URL |
| `Icon` | Ícone FontAwesome 5 |

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

| Categoria | Métodos |
|---|---|
| Dimensões | `width`, `height`, `size`, `fillMaxWidth`, `fillMaxHeight` |
| Espaçamento | `padding`, `margin`, `gap` |
| Layout | `alignment(Pos)` |
| Texto | `fontSize`, `fontFamily`, `bold`, `italic`, `textColor` |
| Visual | `background`, `border`, `borderRadius`, `opacity`, `visible` |

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
