package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

    private String value = "";
    private String label = null;
    private String placeholder = "";
    private boolean isMultiline = false;
    private int rows = 3;
    private boolean wrapText = true;
    private Modifier modifier;
    private Consumer<String> onChange;
    private State<String> boundState;
    private TextInputControl control;

    private boolean autoFocus = false;
    private boolean hideCursor = false;
    private String inputFilter = null;
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
     * new TextField().label("Username:").bindTo(username);
     * }</pre>
     *
     * @param state o state a ligar
     * @return este componente para encadeamento
     */
    public TextField bindTo(State<String> state) {
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
        String initialValue = boundState != null ? boundState.get() : value;

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

        // Filtro de input — apenas aceita caracteres que correspondam ao regex
        if (inputFilter != null) {
            final String pattern = inputFilter;
            control.setTextFormatter(
                new TextFormatter<>(change -> {
                    if (change.getText().matches(pattern + "*")) return change;
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
                .addListener((obs, oldVal, newVal) -> boundState.setQuietly(newVal));
        }

        if (onChange != null) {
            control.textProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
        }

        if (modifier != null) {
            modifier.applyTo(control);
        }

        if (label != null) {
            Label labelNode = new Label(label);
            VBox container = new VBox(4, labelNode, control);
            container.setFillWidth(true);
            return container;
        }

        return control;
    }
}
