package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Etiqueta visual para destacar informacao — contadores, estados, categorias.
 * Pode ser usado sozinho ou sobreposto a outro componente via {@link #on(Component)}.
 *
 * <pre>{@code
 * // Badge simples
 * new Badge("Novo").variant(BadgeVariant.SUCCESS)
 *
 * // Badge sobre um componente
 * new Badge("5").variant(BadgeVariant.DANGER).on(new IconButton("fas-bell"))
 * }</pre>
 */
public class Badge implements Component {

    private final String text;
    private BadgeVariant variant = BadgeVariant.DEFAULT;
    private Component target = null;
    private Modifier modifier;

    /**
     * Cria um badge com o texto dado.
     *
     * @param text texto a mostrar no badge
     */
    public Badge(String text) {
        this.text = text;
    }

    /**
     * Define a variante de cor do badge.
     *
     * @param variant a variante de cor
     * @return este componente para encadeamento
     */
    public Badge variant(BadgeVariant variant) {
        this.variant = variant;
        return this;
    }

    /**
     * Sobrepoe o badge no canto superior direito do componente dado.
     *
     * <pre>{@code
     * new Badge("3").variant(BadgeVariant.DANGER).on(new IconButton("fas-bell"))
     * }</pre>
     *
     * @param component o componente sobre o qual o badge aparece
     * @return este componente para encadeamento
     */
    public Badge on(Component component) {
        this.target = component;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais adicionais.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Badge modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    private Node buildBadgeLabel() {
        Label label = new Label(text);
        label.getStyleClass().add("bricks-badge");
        label.getStyleClass().add("bricks-badge-" + variant.name().toLowerCase());
        label.setPadding(new Insets(2, 8, 2, 8));
        if (modifier != null) modifier.applyTo(label);
        return label;
    }

    @Override
    public Node render() {
        Node badgeNode = buildBadgeLabel();

        if (target == null) {
            return badgeNode;
        }

        StackPane stack = new StackPane();
        stack.getChildren().add(target.render());
        stack.getChildren().add(badgeNode);
        StackPane.setAlignment(badgeNode, Pos.TOP_RIGHT);
        StackPane.setMargin(badgeNode, new Insets(-8, -8, 0, 0));

        return stack;
    }
}
