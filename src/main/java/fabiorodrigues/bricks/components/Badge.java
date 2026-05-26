package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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
    private boolean soft = false;
    private double softAlpha = 0.18;
    private boolean bordered = false;

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
     * Aplica fundo "soft" — cor da variante com transparencia, texto na cor base.
     * Equivalente ao estilo "subtle" / "muted" comum em design systems.
     *
     * <pre>{@code
     * new Badge("Ativo").variant(BadgeVariant.SUCCESS).soft()
     * }</pre>
     *
     * @return este componente para encadeamento
     */
    public Badge soft() {
        this.soft = true;
        return this;
    }

    /**
     * Aplica fundo "soft" com alpha personalizado.
     *
     * @param alpha valor entre 0.0 (totalmente transparente) e 1.0 (opaco). Default 0.18.
     * @return este componente para encadeamento
     */
    public Badge soft(double alpha) {
        this.soft = true;
        this.softAlpha = alpha;
        return this;
    }

    /**
     * Adiciona uma borda na cor da variante.
     * Combina com {@link #soft()} para estilo "outline" com fundo suave.
     *
     * <pre>{@code
     * new Badge("Pendente").variant(BadgeVariant.WARNING).soft().bordered()
     * }</pre>
     *
     * @return este componente para encadeamento
     */
    public Badge bordered() {
        this.bordered = true;
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

        if (soft || bordered) {
            int[] rgb = baseRgb();
            String baseHex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            StringBuilder style = new StringBuilder();
            if (soft) {
                style.append(String.format(java.util.Locale.US,
                    "-fx-background-color: rgba(%d,%d,%d,%.2f);", rgb[0], rgb[1], rgb[2], softAlpha));
                style.append("-fx-text-fill: ").append(baseHex).append(";");
            }
            if (bordered) {
                style.append("-fx-border-color: ").append(baseHex).append(";");
                style.append("-fx-border-width: 1;");
                style.append("-fx-border-radius: 12px;");
            }
            label.setStyle(style.toString());
        }

        if (modifier != null) modifier.applyTo(label);
        return label;
    }

    private int[] baseRgb() {
        return switch (variant) {
            case DEFAULT -> new int[]{0x6b, 0x72, 0x80};
            case PRIMARY -> {
                Color c = BricksTheme.current().colorScheme().primary();
                yield new int[]{(int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255)};
            }
            case SUCCESS -> new int[]{0x16, 0xa3, 0x4a};
            case WARNING -> new int[]{0xd9, 0x77, 0x06};
            case DANGER -> new int[]{0xdc, 0x26, 0x26};
            case INFO -> new int[]{0x25, 0x63, 0xeb};
        };
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
