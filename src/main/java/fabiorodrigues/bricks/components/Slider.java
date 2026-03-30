package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;

import java.util.function.Consumer;

/**
 * Barra deslizante para selecionar um valor numerico. Renderiza como um {@link javafx.scene.control.Slider} JavaFX.
 *
 * <pre>{@code
 * new Slider(0, 100).value(50).onChange(v -> System.out.println(v))
 * }</pre>
 *
 * <p>Com binding:</p>
 * <pre>{@code
 * State<Double> volume = state(0.5);
 * new Slider(0, 1).bindTo(volume)
 * }</pre>
 */
public class Slider implements Component {

    private final double min;
    private final double max;
    private double value;
    private boolean showTicks = false;
    private Modifier modifier;
    private Consumer<Double> onChange;
    private State<Double> boundState;

    /**
     * Cria um slider com os valores minimo e maximo dados.
     *
     * @param min valor minimo
     * @param max valor maximo
     */
    public Slider(double min, double max) {
        this.min = min;
        this.max = max;
        this.value = min;
    }

    /**
     * Define o valor inicial do slider.
     *
     * @param value o valor inicial (deve estar entre min e max)
     * @return este componente para encadeamento
     */
    public Slider value(double value) {
        this.value = value;
        return this;
    }

    /**
     * Mostra marcas de escala no slider.
     *
     * @return este componente para encadeamento
     */
    public Slider showTicks() {
        this.showTicks = true;
        return this;
    }

    /**
     * Liga este slider a um {@link State}. O valor reflecte o state
     * e actualiza-o quando o utilizador desliza.
     *
     * @param state o state a ligar
     * @return este componente para encadeamento
     */
    public Slider bindTo(State<Double> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Define um callback chamado quando o valor muda.
     *
     * @param callback funcao que recebe o novo valor
     * @return este componente para encadeamento
     */
    public Slider onChange(Consumer<Double> callback) {
        this.onChange = callback;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Slider modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        double initialValue = boundState != null ? boundState.get() : value;
        javafx.scene.control.Slider slider = new javafx.scene.control.Slider(min, max, initialValue);
        slider.getStyleClass().add("bricks-slider");
        slider.setShowTickMarks(showTicks);
        slider.setShowTickLabels(showTicks);

        if (boundState != null) {
            slider.valueProperty().addListener((obs, oldVal, newVal) -> boundState.set(newVal.doubleValue()));
        }

        if (onChange != null) {
            slider.valueProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal.doubleValue()));
        }

        if (modifier != null) {
            modifier.applyTo(slider);
        }

        return slider;
    }
}
