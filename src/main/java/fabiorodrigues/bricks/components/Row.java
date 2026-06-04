package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * Layout horizontal. Dispoe os filhos da esquerda para a direita.
 * Renderiza como um {@link HBox} JavaFX.
 *
 * <pre>{@code
 * new Row()
 *     .gap(8)
 *     .children(
 *         new Button("A"),
 *         new Button("B")
 *     )
 * }</pre>
 */
public class Row implements Component {

    private final List<Component> children = new ArrayList<>();
    private Modifier modifier;
    private double gap;
    private double padding;
    private final List<String> styleClasses = new ArrayList<>();

    /**
     * Define o espacamento entre filhos.
     *
     * @param gap espacamento em pixels
     * @return este componente para encadeamento
     */
    public Row gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Define o padding interno uniforme.
     *
     * @param padding padding em pixels
     * @return este componente para encadeamento
     */
    public Row padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Row modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Adiciona classes CSS ao layout renderizado.
     *
     * @param classes classes CSS a adicionar
     * @return este componente para encadeamento
     */
    public Row styleClass(String... classes) {
        Arrays.stream(classes)
            .filter(c -> c != null && !c.isBlank())
            .forEach(this.styleClasses::add);
        return this;
    }

    /**
     * Define os componentes filhos deste layout.
     *
     * @param children os componentes a dispor horizontalmente
     * @return este componente para encadeamento
     */
    public Row children(Component... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public Node render() {
        HBox hbox = new HBox(this.gap);
        hbox.getStyleClass().add("bricks-row");
        hbox.getStyleClass().addAll(styleClasses);
        hbox.setPadding(new Insets(this.padding));
        hbox.setMaxWidth(Double.MAX_VALUE);

        if (modifier != null) {
            modifier.applyTo(hbox);
            if (modifier.getAlignment() != null) {
                hbox.setAlignment(modifier.getAlignment());
            }
        }

        for (Component child : children) {
            Node node = child.render();
            if (node != null) {
                hbox.getChildren().add(node);
            }
        }

        return hbox;
    }
}
