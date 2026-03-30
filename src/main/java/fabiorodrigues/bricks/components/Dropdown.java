package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * Lista de opcoes. Renderiza como um {@link ComboBox} JavaFX.
 *
 * <pre>{@code
 * new Dropdown<>(List.of("Opcao A", "Opcao B", "Opcao C"))
 *     .selected("Opcao A")
 *     .onChange(opcao -> System.out.println("Selecionado: " + opcao))
 * }</pre>
 *
 * <p>Com label e binding:</p>
 * <pre>{@code
 * State<String> pais = state("Portugal");
 * new Dropdown<>(List.of("Portugal", "Brasil", "Angola"))
 *     .label("Pais:")
 *     .bindTo(pais)
 * }</pre>
 *
 * @param <T> o tipo dos items da lista
 */
public class Dropdown<T> implements Component {

    private final List<T> options;
    private T selected;
    private String label;
    private Modifier modifier;
    private Consumer<T> onChange;
    private State<T> boundState;

    /**
     * Cria um dropdown com a lista de opcoes dada.
     *
     * @param options a lista de opcoes
     */
    public Dropdown(List<T> options) {
        this.options = options;
    }

    /**
     * Define a opcao selecionada inicialmente.
     *
     * @param selected a opcao inicial
     * @return este componente para encadeamento
     */
    public Dropdown<T> selected(T selected) {
        this.selected = selected;
        return this;
    }

    /**
     * Adiciona um label acima do dropdown, alinhado a esquerda.
     *
     * @param label o texto do label
     * @return este componente para encadeamento
     */
    public Dropdown<T> label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Liga este dropdown a um {@link State}. A selecao reflecte o valor do state
     * e actualiza-o quando o utilizador escolhe uma opcao.
     *
     * @param state o state a ligar
     * @return este componente para encadeamento
     */
    public Dropdown<T> bindTo(State<T> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Define um callback chamado quando a selecao muda.
     *
     * @param callback funcao que recebe o novo valor selecionado
     * @return este componente para encadeamento
     */
    public Dropdown<T> onChange(Consumer<T> callback) {
        this.onChange = callback;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Dropdown<T> modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("bricks-dropdown");
        comboBox.getItems().addAll(options);

        T initialValue = boundState != null ? boundState.get() : selected;
        if (initialValue != null) {
            comboBox.setValue(initialValue);
        } else if (!options.isEmpty()) {
            comboBox.setValue(options.get(0));
        }

        if (boundState != null) {
            comboBox.valueProperty().addListener((obs, oldVal, newVal) -> boundState.set(newVal));
        }

        if (onChange != null) {
            comboBox.valueProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
        }

        if (modifier != null) {
            modifier.applyTo(comboBox);
        }

        if (label != null) {
            Label labelNode = new Label(label);
            VBox container = new VBox(4, labelNode, comboBox);
            container.setFillWidth(true);
            return container;
        }

        return comboBox;
    }
}
