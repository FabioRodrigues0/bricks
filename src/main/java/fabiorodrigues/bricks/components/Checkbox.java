package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

/**
 * Componente de selecao booleana. Renderiza como um {@link CheckBox} JavaFX.
 *
 * <pre>{@code
 * new Checkbox("Aceito os termos").checked(true)
 * }</pre>
 *
 * <p>Com binding a um state:</p>
 * <pre>{@code
 * State<Boolean> aceito = state(false);
 * new Checkbox("Aceito os termos").bindTo(aceito)
 * }</pre>
 */
public class Checkbox implements Component {

    private String label = "";
    private boolean checked = false;
    private Modifier modifier;
    private Consumer<Boolean> onChange;
    private State<Boolean> boundState;

    /**
     * Cria uma checkbox com o texto dado.
     *
     * @param label o texto da checkbox
     */
    public Checkbox(String label) {
        this.label = label;
    }

    /**
     * Define o estado inicial da checkbox.
     *
     * @param checked true para marcada, false para desmarcada
     * @return este componente para encadeamento
     */
    public Checkbox checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    /**
     * Liga esta checkbox a um {@link State}. O estado da checkbox reflecte
     * o valor do state e actualiza-o quando o utilizador clica.
     *
     * @param state o state booleano a ligar
     * @return este componente para encadeamento
     */
    public Checkbox bindTo(State<Boolean> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Define um callback chamado sempre que o estado da checkbox muda.
     *
     * @param callback funcao que recebe o novo valor (true/false)
     * @return este componente para encadeamento
     */
    public Checkbox onChange(Consumer<Boolean> callback) {
        this.onChange = callback;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Checkbox modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        boolean initialValue = boundState != null ? boundState.get() : checked;
        CheckBox checkBox = new CheckBox(label);
        checkBox.getStyleClass().add("bricks-checkbox");
        checkBox.setSelected(initialValue);

        if (boundState != null) {
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> boundState.set(newVal));
        }

        if (onChange != null) {
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
        }

        if (modifier != null) {
            modifier.applyTo(checkBox);
        }

        return checkBox;
    }
}
