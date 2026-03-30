package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;

/**
 * Linha separadora horizontal ou vertical.
 * Equivalente ao {@code Divider} do Jetpack Compose.
 *
 * <p>Horizontal (por defeito):</p>
 * <pre>{@code
 * new Divider()
 * }</pre>
 *
 * <p>Vertical:</p>
 * <pre>{@code
 * new Divider().vertical()
 * }</pre>
 */
public class Divider implements Component {

    private Orientation orientation = Orientation.HORIZONTAL;

    /**
     * Torna o divider vertical.
     *
     * @return este componente para encadeamento
     */
    public Divider vertical() {
        this.orientation = Orientation.VERTICAL;
        return this;
    }

    /**
     * Torna o divider horizontal (comportamento por defeito).
     *
     * @return este componente para encadeamento
     */
    public Divider horizontal() {
        this.orientation = Orientation.HORIZONTAL;
        return this;
    }

    @Override
    public Node render() {
        Separator sep = new Separator(orientation);
        sep.getStyleClass().add("bricks-divider");
        return sep;
    }
}
