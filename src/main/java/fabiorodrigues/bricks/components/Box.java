package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Layout empilhado. Dispoe os filhos uns sobre os outros (o ultimo fica por cima).
 * Renderiza como um {@link StackPane} JavaFX.
 *
 * <pre>{@code
 * new Box()
 *     .padding(10)
 *     .children(
 *         new Text("Fundo"),
 *         new Text("Topo")
 *     )
 * }</pre>
 */
public class Box implements Component {

    private final List<Component> children = new ArrayList<>();
    private Modifier modifier;
    private double padding;

    /**
     * Define o padding interno uniforme.
     *
     * @param padding padding em pixels
     * @return este componente para encadeamento
     */
    public Box padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Box modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Define os componentes filhos deste layout.
     *
     * @param children os componentes a empilhar
     * @return este componente para encadeamento
     */
    public Box children(Component... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public Node render() {
        StackPane stack = new StackPane();
        stack.getStyleClass().add("bricks-box");
        stack.setPadding(new Insets(this.padding));

        if (modifier != null) {
            modifier.applyTo(stack);
        }

        for (Component child : children) {
            stack.getChildren().add(child.render());
        }

        return stack;
    }
}
