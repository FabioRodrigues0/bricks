package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * Contentor com scroll. Renderiza como um {@link ScrollPane} JavaFX.
 *
 * <pre>{@code
 * new ScrollView(
 *     new Column().gap(8).children(
 *         new Text("Item 1"),
 *         new Text("Item 2"),
 *         // ... muitos items
 *     )
 * )
 * }</pre>
 *
 * <p>Apenas scroll vertical (por defeito ambos estao activos):</p>
 * <pre>{@code
 * new ScrollView(conteudo).vertical()
 * }</pre>
 */
public class ScrollView implements Component {

    private final Component content;
    private boolean scrollH = true;
    private boolean scrollV = true;
    private Modifier modifier;

    /**
     * Cria um ScrollView com o componente dado como conteudo.
     *
     * @param content o componente a envolver com scroll
     */
    public ScrollView(Component content) {
        this.content = content;
    }

    /**
     * Ativa apenas scroll vertical (desativa horizontal).
     *
     * @return este componente para encadeamento
     */
    public ScrollView vertical() {
        this.scrollH = false;
        this.scrollV = true;
        return this;
    }

    /**
     * Ativa apenas scroll horizontal (desativa vertical).
     *
     * @return este componente para encadeamento
     */
    public ScrollView horizontal() {
        this.scrollH = true;
        this.scrollV = false;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public ScrollView modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        ScrollPane scrollPane = new ScrollPane(content.render());
        scrollPane.getStyleClass().add("bricks-scroll-view");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(scrollH
            ? ScrollPane.ScrollBarPolicy.AS_NEEDED
            : ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(scrollV
            ? ScrollPane.ScrollBarPolicy.AS_NEEDED
            : ScrollPane.ScrollBarPolicy.NEVER);

        if (modifier != null) {
            modifier.applyTo(scrollPane);
        }

        return scrollPane;
    }
}
