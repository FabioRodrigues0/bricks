package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Layout vertical. Dispoe os filhos de cima para baixo.
 * Renderiza como um {@link VBox} JavaFX.
 *
 * <pre>{@code
 * new Column()
 *     .gap(12)
 *     .padding(20)
 *     .children(
 *         new Text("Primeiro"),
 *         new Text("Segundo")
 *     )
 * }</pre>
 */
public class Column implements Component {

    private final List<Component> children = new ArrayList<>();
    private Modifier modifier;
    private double gap;
    private double padding;

    /**
     * Define o espacamento entre filhos.
     *
     * @param gap espacamento em pixels
     * @return este componente para encadeamento
     */
    public Column gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Define o padding interno uniforme.
     *
     * @param padding padding em pixels
     * @return este componente para encadeamento
     */
    public Column padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Column modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Define os componentes filhos deste layout.
     *
     * @param children os componentes a dispor verticalmente
     * @return este componente para encadeamento
     */
    public Column children(Component... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public Node render() {
        VBox vbox = new VBox(this.gap);
        vbox.getStyleClass().add("bricks-column");
        vbox.setPadding(new Insets(this.padding));

        if (modifier != null) {
            modifier.applyTo(vbox);
            if (modifier.getAlignment() != null) {
                vbox.setAlignment(modifier.getAlignment());
            }
        }

        for (Component child : children) {
            vbox.getChildren().add(child.render());
        }

        return vbox;
    }
}
