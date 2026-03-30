package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;

/**
 * Barra de progresso. Renderiza como um {@link javafx.scene.control.ProgressBar} JavaFX.
 *
 * <p>O valor deve estar entre 0.0 (0%) e 1.0 (100%).</p>
 *
 * <pre>{@code
 * new ProgressBar().value(0.75)  // 75%
 * new ProgressBar().indeterminate() // animacao de carregamento
 * }</pre>
 *
 * <p>Com binding:</p>
 * <pre>{@code
 * State<Double> progresso = state(0.0);
 * new ProgressBar().bindTo(progresso)
 *
 * // noutro sitio:
 * progresso.set(0.5); // atualiza para 50%
 * }</pre>
 */
public class ProgressBar implements Component {

    private double value = 0.0;
    private boolean indeterminate = false;
    private Modifier modifier;
    private State<Double> boundState;

    /**
     * Define o valor do progresso entre 0.0 (0%) e 1.0 (100%).
     *
     * @param value o valor de progresso
     * @return este componente para encadeamento
     */
    public ProgressBar value(double value) {
        this.value = value;
        return this;
    }

    /**
     * Mostra uma animacao de carregamento indeterminado (valor desconhecido).
     *
     * @return este componente para encadeamento
     */
    public ProgressBar indeterminate() {
        this.indeterminate = true;
        return this;
    }

    /**
     * Liga esta barra a um {@link State}. O progresso reflecte o valor do state
     * e actualiza-se automaticamente quando o state muda.
     *
     * @param state o state a ligar (valores entre 0.0 e 1.0)
     * @return este componente para encadeamento
     */
    public ProgressBar bindTo(State<Double> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public ProgressBar modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        javafx.scene.control.ProgressBar bar = new javafx.scene.control.ProgressBar();
        bar.getStyleClass().add("bricks-progress-bar");

        if (indeterminate) {
            bar.setProgress(javafx.scene.control.ProgressBar.INDETERMINATE_PROGRESS);
        } else {
            double initialValue = boundState != null ? boundState.get() : value;
            bar.setProgress(initialValue);
        }

        if (modifier != null) {
            modifier.applyTo(bar);
        }

        return bar;
    }
}
