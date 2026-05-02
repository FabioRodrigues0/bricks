package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Container com estilo visual próprio: fundo, cantos arredondados e sombra.
 * Equivalente ao {@code Card} do Material 3 / Jetpack Compose.
 *
 * <p>Card simples:</p>
 * <pre>{@code
 * new Card()
 *     .padding(16)
 *     .elevation(2)
 *     .children(
 *         new Text("Título").fontSize(15),
 *         new Text("Conteúdo").fontSize(13)
 *     )
 * }</pre>
 *
 * <p>Card clicável:</p>
 * <pre>{@code
 * new Card()
 *     .padding(16)
 *     .elevation(2)
 *     .onClick(() -> System.out.println("card clicado"))
 *     .children(...)
 * }</pre>
 *
 * <p>Card sem sombra com cor de fundo personalizada:</p>
 * <pre>{@code
 * new Card()
 *     .elevation(0)
 *     .background(Color.LIGHTGRAY)
 *     .cornerRadius(4)
 *     .children(...)
 * }</pre>
 */
public class Card implements Component {

    private final List<Component> children = new ArrayList<>();
    private double padding = 0;
    private double marginTop = 0;
    private double marginRight = 0;
    private double marginBottom = 0;
    private double marginLeft = 0;
    private double elevation = 1;
    private double width = -1;
    private double height = -1;
    private Color background = Color.WHITE;
    // TODO: migrar para modifier(Modifier) quando Card suportar Modifier completo
    private Color gradientFrom = null;
    private Color gradientTo = null;
    private double gradientAngle = 135;
    private double cornerRadius = 8;
    private Runnable onClick;

    /**
     * Define o espaço interno uniforme.
     *
     * @param padding {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public Card padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Define a margin uniforme em todos os lados.
     *
     * @param margin {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public Card margin(double margin) {
        this.marginTop = margin;
        this.marginRight = margin;
        this.marginBottom = margin;
        this.marginLeft = margin;
        return this;
    }

    /**
     * Define a margin vertical e horizontal.
     *
     * @param vertical {@code double} — valor em pixels aplicado em cima e em baixo
     * @param horizontal {@code double} — valor em pixels aplicado a esquerda e a direita
     * @return este componente para encadeamento
     */
    public Card margin(double vertical, double horizontal) {
        this.marginTop = vertical;
        this.marginBottom = vertical;
        this.marginRight = horizontal;
        this.marginLeft = horizontal;
        return this;
    }

    /**
     * Define a intensidade da sombra. {@code 0} remove a sombra completamente.
     * Cada unidade corresponde a um raio de 4px e um offset vertical de 2px.
     *
     * @param elevation {@code double} — intensidade da sombra (ex: 1, 2, 4)
     * @return este componente para encadeamento
     */
    public Card elevation(double elevation) {
        this.elevation = elevation;
        return this;
    }

    /**
     * Define a largura fixa do card. {@code -1} para largura automática.
     *
     * @param width {@code double} — largura em pixels
     * @return este componente para encadeamento
     */
    public Card width(double width) {
        this.width = width;
        return this;
    }

    /**
     * Define a altura fixa do card. {@code -1} para altura automática.
     *
     * @param height {@code double} — altura em pixels
     * @return este componente para encadeamento
     */
    public Card height(double height) {
        this.height = height;
        return this;
    }

    /**
     * Define a cor de fundo do card.
     *
     * @param color {@code Color} — cor de fundo (ex: Color.WHITE, Color.web("#f5f5f5"))
     * @return este componente para encadeamento
     */
    public Card background(Color color) {
        this.background = color;
        return this;
    }

    /**
     * Define um gradiente linear como fundo do card.
     * Direcao por defeito: 135 graus (diagonal superior-esquerda para inferior-direita).
     *
     * <pre>{@code
     * new Card()
     *     .backgroundGradient(Color.web("#6c3483"), Color.web("#1a5276"))
     *     .children(...)
     * }</pre>
     *
     * @param from cor inicial do gradiente
     * @param to   cor final do gradiente
     * @return este componente para encadeamento
     */
    public Card backgroundGradient(Color from, Color to) {
        this.gradientFrom = from;
        this.gradientTo = to;
        this.gradientAngle = 135;
        return this;
    }

    /**
     * Define um gradiente linear como fundo do card com angulo personalizado.
     *
     * <pre>{@code
     * new Card()
     *     .backgroundGradient(Color.web("#6c3483"), Color.web("#1a5276"), 90)
     *     .children(...)
     * }</pre>
     *
     * @param from  cor inicial do gradiente
     * @param to    cor final do gradiente
     * @param angle angulo em graus (0 = esquerda para direita, 90 = cima para baixo, 135 = diagonal)
     * @return este componente para encadeamento
     */
    public Card backgroundGradient(Color from, Color to, double angle) {
        this.gradientFrom = from;
        this.gradientTo = to;
        this.gradientAngle = angle;
        return this;
    }

    /**
     * Define o raio dos cantos arredondados.
     *
     * @param radius {@code double} — raio em pixels (ex: 4, 8, 12)
     * @return este componente para encadeamento
     */
    public Card cornerRadius(double radius) {
        this.cornerRadius = radius;
        return this;
    }

    /**
     * Define um callback chamado ao clicar no card.
     * Quando definido, o cursor muda para mão ao passar por cima.
     *
     * @param callback {@code Runnable} — ação a executar ao clicar
     * @return este componente para encadeamento
     */
    public Card onClick(Runnable callback) {
        this.onClick = callback;
        return this;
    }

    /**
     * Define os componentes filhos do card.
     *
     * @param children os componentes a mostrar dentro do card
     * @return este componente para encadeamento
     */
    public Card children(Component... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public Node render() {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("bricks-card");

        String bgValue;
        if (gradientFrom != null && gradientTo != null) {
            double rad = Math.toRadians(gradientAngle);
            double x1 = 50 - 50 * Math.sin(rad);
            double y1 = 50 - 50 * Math.cos(rad);
            double x2 = 50 + 50 * Math.sin(rad);
            double y2 = 50 + 50 * Math.cos(rad);
            bgValue = String.format(java.util.Locale.US,
                "linear-gradient(from %.0f%% %.0f%% to %.0f%% %.0f%%, #%02x%02x%02x, #%02x%02x%02x)",
                x1, y1, x2, y2,
                (int) (gradientFrom.getRed() * 255), (int) (gradientFrom.getGreen() * 255), (int) (gradientFrom.getBlue() * 255),
                (int) (gradientTo.getRed() * 255), (int) (gradientTo.getGreen() * 255), (int) (gradientTo.getBlue() * 255));
        } else {
            bgValue = String.format("#%02x%02x%02x",
                (int) (background.getRed() * 255),
                (int) (background.getGreen() * 255),
                (int) (background.getBlue() * 255));
        }

        vbox.setStyle(String.format(java.util.Locale.US,
            "-fx-background-color: %s; -fx-background-radius: %.1f; -fx-border-radius: %.1f; -fx-padding: %.1f;",
            bgValue, cornerRadius, cornerRadius, padding));

        if (width >= 0) {
            vbox.setPrefWidth(width);
            vbox.setMinWidth(width);
            vbox.setMaxWidth(width);
        }
        if (height >= 0) {
            vbox.setPrefHeight(height);
            vbox.setMinHeight(height);
            vbox.setMaxHeight(height);
        }

        if (elevation > 0) {
            DropShadow shadow = new DropShadow();
            shadow.setRadius(elevation * 4);
            shadow.setOffsetY(elevation * 2);
            shadow.setColor(Color.rgb(0, 0, 0, 0.15));
            vbox.setEffect(shadow);
        }

        if (onClick != null) {
            vbox.setCursor(Cursor.HAND);
            vbox.setOnMouseClicked(e -> onClick.run());
        }

        for (Component child : children) {
            vbox.getChildren().add(child.render());
        }

        boolean hasMargin = marginTop != 0 || marginRight != 0 || marginBottom != 0 || marginLeft != 0;
        if (hasMargin) {
            return new Box()
                    .modifier(new Modifier().padding(marginTop, marginRight, marginBottom, marginLeft))
                    .children(() -> vbox)
                    .render();
        }

        return vbox;
    }
}
