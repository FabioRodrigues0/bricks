package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Campo de texto. Por defeito e de uma linha; usar {@link #multiline()} para multiplas linhas.
 * Equivalente ao {@code TextField} do Jetpack Compose.
 *
 * <p>Linha unica:</p>
 * <pre>{@code
 * new TextField().placeholder("Escreve o teu nome...");
 * }</pre>
 *
 * <p>Multiplas linhas:</p>
 * <pre>{@code
 * new TextField().multiline().rows(5).placeholder("Escreve as tuas notas...");
 * }</pre>
 *
 * <p>Com label:</p>
 * <pre>{@code
 * new TextField().label("Username:").placeholder("Escreve o teu nome...");
 * }</pre>
 *
 * <p>Para ler o valor atual usa {@link #getValue()}:</p>
 * <pre>{@code
 * TextField campo = new TextField().label("Nome:").value("inicial");
 * new Button("Guardar").onClick(() -> System.out.println(campo.getValue()));
 * }</pre>
 *
 */
public class TextField implements Component {

    /**
     * Tipo de input aceite pelo campo.
     */
    public enum Type {
        TEXT,
        NUMBER,
        FLOAT,
        DOUBLE
    }

    private String value = "";
    private String label = null;
    private String placeholder = "";
    private boolean isMultiline = false;
    private int rows = 3;
    private boolean wrapText = true;
    private Modifier modifier;
    private Consumer<String> onChange;
    private State<?> boundState;
    private TextInputControl control;

    private boolean autoFocus = false;
    private boolean hideCursor = false;
    private String inputFilter = null;
    private Type type = Type.TEXT;
    private Consumer<KeyEvent> onKeyPressed = null;

    /**
     * Adiciona um label acima do campo, alinhado a esquerda.
     *
     * @param label o texto do label
     * @return este componente para encadeamento
     */
    public TextField label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Define o valor inicial do campo.
     *
     * @param value o texto inicial
     * @return este componente para encadeamento
     */
    public TextField value(String value) {
        this.value = value;
        return this;
    }

    /**
     * Define o texto de placeholder mostrado quando o campo esta vazio.
     *
     * @param placeholder o texto de sugestao
     * @return este componente para encadeamento
     */
    public TextField placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Ativa o modo de multiplas linhas. Por defeito o campo e de uma so linha.
     *
     * @return este componente para encadeamento
     */
    public TextField multiline() {
        this.isMultiline = true;
        return this;
    }

    /**
     * Define o numero de linhas visiveis. So tem efeito no modo multiline.
     *
     * @param rows numero de linhas (por defeito: 3)
     * @return este componente para encadeamento
     */
    public TextField rows(int rows) {
        this.rows = rows;
        return this;
    }

    /**
     * Define se o texto faz wrap automatico ao chegar ao fim da linha.
     * So tem efeito no modo multiline (por defeito: true).
     *
     * @param wrap true para activar wrap, false para desactivar
     * @return este componente para encadeamento
     */
    public TextField wrapText(boolean wrap) {
        this.wrapText = wrap;
        return this;
    }

    /**
     * Ativa o foco automatico neste campo quando a janela ganha foco.
     * Util em ecras onde o utilizador deve comecar a escrever imediatamente.
     *
     * <pre>{@code
     * new TextField().autoFocus()
     * }</pre>
     *
     * @return este componente para encadeamento
     */
    public TextField autoFocus() {
        this.autoFocus = true;
        return this;
    }

    /**
     * Esconde o cursor de texto (caret). Util em calculadoras e campos
     * onde o cursor visual e indesejado.
     *
     * <pre>{@code
     * new TextField().hideCursor()
     * }</pre>
     *
     * @return este componente para encadeamento
     */
    public TextField hideCursor() {
        this.hideCursor = true;
        return this;
    }

    /**
     * Define o tipo de input aceite pelo campo.
     *
     * <p>{@link Type#NUMBER} aceita apenas numeros inteiros, com sinal negativo
     * opcional. Para valores decimais, usar {@link Type#FLOAT} ou {@link Type#DOUBLE}.</p>
     *
     * <pre>{@code
     * new TextField().type(TextField.Type.NUMBER)
     * }</pre>
     *
     * @param type tipo de input do campo
     * @return este componente para encadeamento
     */
    public TextField type(Type type) {
        this.type = type != null ? type : Type.TEXT;
        return this;
    }

    /**
     * Atalho para {@code type(TextField.Type.NUMBER)}.
     *
     * @return este componente para encadeamento
     */
    public TextField number() {
        return type(Type.NUMBER);
    }

    /**
     * Atalho para {@code type(TextField.Type.FLOAT)}.
     *
     * @return este componente para encadeamento
     */
    public TextField floating() {
        return type(Type.FLOAT);
    }

    /**
     * Atalho para {@code type(TextField.Type.FLOAT)}.
     *
     * @return este componente para encadeamento
     */
    public TextField floatNumber() {
        return floating();
    }

    /**
     * Atalho para {@code type(TextField.Type.DOUBLE)}.
     *
     * @return este componente para encadeamento
     */
    public TextField decimal() {
        return type(Type.DOUBLE);
    }

    /**
     * Atalho para {@code type(TextField.Type.DOUBLE)}.
     *
     * @return este componente para encadeamento
     */
    public TextField doubleNumber() {
        return decimal();
    }

    /**
     * Filtra o input aceite pelo campo. Apenas caracteres que correspondam
     * ao regex sao aceites — os restantes sao ignorados silenciosamente.
     *
     *
     * <p>Exemplo para calculadora: use o regex [0-9+\-*\/.] para aceitar numeros e operadores.</p>
     *
     * @param allowedCharsRegex expressao regular dos caracteres permitidos
     * @return este componente para encadeamento
     */
    public TextField inputFilter(String allowedCharsRegex) {
        this.inputFilter = allowedCharsRegex;
        return this;
    }

    /**
     * Define um callback chamado quando uma tecla e pressionada no campo.
     * Util para interceptar teclas especiais como operadores ou Enter.
     *
     * @param callback funcao que recebe o KeyEvent da tecla pressionada
     * @return este componente para encadeamento
     */
    public TextField onKeyPressed(Consumer<KeyEvent> callback) {
        this.onKeyPressed = callback;
        return this;
    }

    /**
     * Liga este campo a um {@link State}. O campo usa o valor atual do state como valor inicial,
     * e atualiza o state automaticamente quando o utilizador escreve.
     *
     * <pre>{@code
     * State<String> username = state("");
     * State<Integer> idade = state(18);
     * new TextField().label("Username:").bindTo(username);
     * new TextField().number().bindTo(idade);
     * }</pre>
     *
     * @param state o state a ligar
     * @return este componente para encadeamento
     */
    public <T> TextField bindTo(State<T> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Define um callback chamado sempre que o texto muda.
     *
     * @param callback funcao que recebe o novo valor do campo
     * @return este componente para encadeamento
     */
    public TextField onChange(Consumer<String> callback) {
        this.onChange = callback;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public TextField modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Devolve o valor atual do campo.
     *
     * @return o texto atual, ou o valor inicial se ainda nao foi renderizado
     */
    public String getValue() {
        return control != null ? control.getText() : value;
    }

    @Override
    public Node render() {
        String initialValue = boundState != null && boundState.get() != null
            ? boundState.get().toString()
            : value;

        if (isMultiline) {
            javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(initialValue);
            area.setPromptText(placeholder);
            area.setPrefRowCount(rows);
            area.setWrapText(wrapText);
            area.getStyleClass().add("bricks-text-area");
            control = area;
        } else {
            javafx.scene.control.TextField field = new javafx.scene.control.TextField(initialValue);
            field.setPromptText(placeholder);
            field.getStyleClass().add("bricks-text-field");
            control = field;
        }

        // Filtro de input — aceita o filtro manual ou o tipo predefinido.
        String pattern = inputFilter != null ? inputFilter : inputPatternForType();
        if (pattern != null) {
            final String finalPattern = pattern;
            final boolean fullValuePattern = inputFilter == null;
            control.setTextFormatter(
                new TextFormatter<>(change -> {
                    String textToValidate = fullValuePattern
                        ? change.getControlNewText()
                        : change.getText();
                    String patternToMatch = fullValuePattern
                        ? finalPattern
                        : finalPattern + "*";
                    if (textToValidate.matches(patternToMatch)) return change;
                    return null;
                })
            );
        }

        // Cursor invisivel
        if (hideCursor) {
            String existing = control.getStyle();
            control.setStyle(existing + "-fx-caret-color: transparent;");
        }

        // Foco automatico — pede foco imediatamente e sempre que a janela ganhar foco
        if (autoFocus) {
            Platform.runLater(control::requestFocus);
            control
                .sceneProperty()
                .addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene
                            .windowProperty()
                            .addListener((obs2, oldWin, newWin) -> {
                                if (newWin != null) {
                                    newWin
                                        .focusedProperty()
                                        .addListener((obs3, wasFocused, isFocused) -> {
                                            if (isFocused) Platform.runLater(control::requestFocus);
                                        });
                                }
                            });
                    }
                });
        }

        if (onKeyPressed != null) {
            control.setOnKeyPressed(e -> onKeyPressed.accept(e));
        }

        if (boundState != null) {
            control
                .textProperty()
                .addListener((obs, oldVal, newVal) -> updateBoundState(newVal));
        }

        if (onChange != null) {
            control.textProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
        }

        if (modifier != null) {
            modifier.applyTo(control);
        }

        if (label != null) {
            Node labelNode = new Text(label).styleClass("bricks-label").render();
            VBox container = new VBox(4, labelNode, control);
            container.setFillWidth(true);
            LayoutUtils.applyExpansionToWrapper(control, container);
            return container;
        }

        return control;
    }

    private String inputPatternForType() {
        if (type == Type.NUMBER) {
            return "-?\\d*";
        }
        if (type == Type.FLOAT || type == Type.DOUBLE) {
            return "-?\\d*([\\.,]\\d*)?";
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> void updateBoundState(String text) {
        if (boundState == null) return;
        State<T> state = (State<T>) boundState;
        state.setQuietly((T) valueForState(text, state.get()));
    }

    private Object valueForState(String text, Object currentValue) {
        if (shouldBindAsInteger(currentValue)) {
            return parseInteger(text);
        }
        if (shouldBindAsFloat(currentValue)) {
            return parseFloat(text);
        }
        if (shouldBindAsDouble(currentValue)) {
            return parseDouble(text);
        }
        return text;
    }

    private boolean shouldBindAsInteger(Object currentValue) {
        return currentValue instanceof Integer || (currentValue == null && type == Type.NUMBER);
    }

    private boolean shouldBindAsFloat(Object currentValue) {
        return currentValue instanceof Float || (currentValue == null && type == Type.FLOAT);
    }

    private boolean shouldBindAsDouble(Object currentValue) {
        return currentValue instanceof Double || (currentValue == null && type == Type.DOUBLE);
    }

    private Integer parseInteger(String text) {
        if (text == null || text.isBlank() || "-".equals(text)) return null;
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseFloat(String text) {
        if (isPartialDecimal(text)) return null;
        try {
            return Float.valueOf(normalizeDecimal(text));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String text) {
        if (isPartialDecimal(text)) return null;
        try {
            return Double.valueOf(normalizeDecimal(text));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isPartialDecimal(String text) {
        return text == null
            || text.isBlank()
            || "-".equals(text)
            || ".".equals(text)
            || ",".equals(text)
            || "-.".equals(text)
            || "-,".equals(text);
    }

    private String normalizeDecimal(String text) {
        return text.replace(',', '.');
    }
}
