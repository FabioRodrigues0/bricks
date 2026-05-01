package fabiorodrigues.bricks.style;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Define propriedades visuais e de layout reutilizaveis entre componentes.
 * Usa uma API fluent com encadeamento de metodos.
 *
 * <p>Pode ser partilhado entre varios componentes para manter estilos consistentes:</p>
 *
 * <pre>{@code
 * Modifier titleStyle = new Modifier().fontSize(24).bold().textColor(Color.BLUE);
 *
 * new Text("Titulo").modifier(titleStyle);
 * new Text("Outro Titulo").modifier(titleStyle);
 * }</pre>
 *
 * <p>Propriedades disponiveis:</p>
 * <ul>
 *   <li><b>Layout:</b> {@link #padding}, {@link #margin}, {@link #width}, {@link #height}, {@link #size}, {@link #gap}, {@link #fillMaxWidth}, {@link #fillMaxHeight}, {@link #alignment}</li>
 *   <li><b>Texto:</b> {@link #fontSize}, {@link #fontFamily}, {@link #bold}, {@link #italic}, {@link #textColor}</li>
 *   <li><b>Visual:</b> {@link #background}, {@link #backgroundGradient(Color, Color)}, {@link #border}, {@link #borderRadius}, {@link #opacity}, {@link #visible}</li>
 * </ul>
 */
public class Modifier {

    private double paddingTop, paddingRight, paddingBottom, paddingLeft;
    private double marginTop, marginRight, marginBottom, marginLeft;
    private double width = -1;
    private double height = -1;
    private double gap;
    private boolean fillMaxWidth;
    private boolean fillMaxHeight;
    private Pos alignment;

    private double fontSize = -1;
    private String fontFamily;
    private boolean bold;
    private boolean italic;
    private Color textColor;

    private Color backgroundColor;
    private Color gradientFrom = null;
    private Color gradientTo = null;
    private double gradientAngle = 135;
    private Color borderColor;
    private double borderWidth = -1;
    private double borderRadius = -1;
    private double opacity = -1;
    private boolean visible = true;
    private boolean visibleSet;

    /**
     * Define padding igual em todos os lados.
     *
     * @param all {@code double} — valor em pixels (ex: 8, 16, 24)
     * @return este modifier para encadeamento
     */
    public Modifier padding(double all) {
        this.paddingTop = all;
        this.paddingRight = all;
        this.paddingBottom = all;
        this.paddingLeft = all;
        return this;
    }

    /**
     * Define padding vertical e horizontal.
     *
     * @param vertical {@code double} — valor em pixels aplicado em cima e em baixo
     * @param horizontal {@code double} — valor em pixels aplicado a esquerda e a direita
     * @return este modifier para encadeamento
     */
    public Modifier padding(double vertical, double horizontal) {
        this.paddingTop = vertical;
        this.paddingBottom = vertical;
        this.paddingRight = horizontal;
        this.paddingLeft = horizontal;
        return this;
    }

    /**
     * Define padding individual para cada lado.
     *
     * @param top {@code double} — valor em pixels aplicado em cima
     * @param right {@code double} — valor em pixels aplicado a direita
     * @param bottom {@code double} — valor em pixels aplicado em baixo
     * @param left {@code double} — valor em pixels aplicado a esquerda
     * @return este modifier para encadeamento
     */
    public Modifier padding(double top, double right, double bottom, double left) {
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.paddingLeft = left;
        return this;
    }

    /**
     * Define margin igual em todos os lados.
     * Equivalente ao espaco exterior ao componente.
     *
     * @param all {@code double} — valor em pixels (ex: 8, 16, 24)
     * @return este modifier para encadeamento
     */
    public Modifier margin(double all) {
        this.marginTop = all;
        this.marginRight = all;
        this.marginBottom = all;
        this.marginLeft = all;
        return this;
    }

    /**
     * Define margin vertical e horizontal.
     *
     * @param vertical {@code double} — valor em pixels aplicado em cima e em baixo
     * @param horizontal {@code double} — valor em pixels aplicado a esquerda e a direita
     * @return este modifier para encadeamento
     */
    public Modifier margin(double vertical, double horizontal) {
        this.marginTop = vertical;
        this.marginBottom = vertical;
        this.marginRight = horizontal;
        this.marginLeft = horizontal;
        return this;
    }

    /**
     * Define margin individual para cada lado.
     *
     * @param top {@code double} — valor em pixels aplicado em cima
     * @param right {@code double} — valor em pixels aplicado a direita
     * @param bottom {@code double} — valor em pixels aplicado em baixo
     * @param left {@code double} — valor em pixels aplicado a esquerda
     * @return este modifier para encadeamento
     */
    public Modifier margin(double top, double right, double bottom, double left) {
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
        this.marginLeft = left;
        return this;
    }

    /**
     * Define a largura fixa do componente.
     *
     * @param width {@code double} — largura em pixels
     * @return este modifier para encadeamento
     */
    public Modifier width(double width) {
        this.width = width;
        return this;
    }

    /**
     * Define a altura fixa do componente.
     *
     * @param height {@code double} — altura em pixels
     * @return este modifier para encadeamento
     */
    public Modifier height(double height) {
        this.height = height;
        return this;
    }

    /**
     * Define largura e altura fixas do componente.
     *
     * @param width {@code double} — largura em pixels
     * @param height {@code double} — altura em pixels
     * @return este modifier para encadeamento
     */
    public Modifier size(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Faz o componente ocupar toda a largura disponivel no layout pai.
     *
     * @return este modifier para encadeamento
     */
    public Modifier fillMaxWidth() {
        this.fillMaxWidth = true;
        return this;
    }

    /**
     * Faz o componente ocupar toda a altura disponivel no layout pai.
     *
     * @return este modifier para encadeamento
     */
    public Modifier fillMaxHeight() {
        this.fillMaxHeight = true;
        return this;
    }

    /**
     * Define o espacamento entre filhos em layouts (Column, Row).
     *
     * @param gap {@code double} — espacamento em pixels
     * @return este modifier para encadeamento
     */
    public Modifier gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Define o alinhamento do conteudo dentro do componente.
     *
     * <pre>{@code
     * new Column().modifier(new Modifier().alignment(Pos.CENTER))
     * }</pre>
     *
     * @param alignment {@code Pos} — alinhamento a aplicar (ex: Pos.CENTER, Pos.TOP_LEFT)
     * @return este modifier para encadeamento
     */
    public Modifier alignment(Pos alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Define o tamanho da fonte.
     *
     * @param size {@code double} — tamanho em pontos (ex: 12, 16, 24)
     * @return este modifier para encadeamento
     */
    public Modifier fontSize(double size) {
        this.fontSize = size;
        return this;
    }

    /**
     * Define a familia da fonte.
     *
     * <pre>{@code
     * new Modifier().fontFamily("Arial")
     * }</pre>
     *
     * @param family {@code String} — nome da familia da fonte (ex: "Arial", "Roboto")
     * @return este modifier para encadeamento
     */
    public Modifier fontFamily(String family) {
        this.fontFamily = family;
        return this;
    }

    /**
     * Torna o texto a negrito.
     *
     * @return este modifier para encadeamento
     */
    public Modifier bold() {
        this.bold = true;
        return this;
    }

    /**
     * Torna o texto em italico.
     *
     * @return este modifier para encadeamento
     */
    public Modifier italic() {
        this.italic = true;
        return this;
    }

    /**
     * Define a cor do texto.
     *
     * @param color {@code Color} — cor do texto (ex: Color.RED, Color.web("#ff0000"))
     * @return este modifier para encadeamento
     */
    public Modifier textColor(Color color) {
        this.textColor = color;
        return this;
    }

    /**
     * Define a cor de fundo do componente.
     *
     * @param color {@code Color} — cor de fundo (ex: Color.LIGHTGRAY, Color.web("#f0f0f0"))
     * @return este modifier para encadeamento
     */
    public Modifier background(Color color) {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Define um gradiente linear como fundo do componente.
     * Direcao por defeito: 135 graus (diagonal superior-esquerda para inferior-direita).
     *
     * <pre>{@code
     * new Modifier().backgroundGradient(Color.web("#6c3483"), Color.web("#1a5276"))
     * }</pre>
     *
     * @param from cor inicial do gradiente
     * @param to   cor final do gradiente
     * @return este modifier para encadeamento
     */
    public Modifier backgroundGradient(Color from, Color to) {
        this.gradientFrom = from;
        this.gradientTo = to;
        this.gradientAngle = 135;
        return this;
    }

    /**
     * Define um gradiente linear como fundo com angulo personalizado.
     *
     * <pre>{@code
     * new Modifier().backgroundGradient(Color.web("#6c3483"), Color.web("#1a5276"), 90)
     * }</pre>
     *
     * @param from  cor inicial do gradiente
     * @param to    cor final do gradiente
     * @param angle angulo em graus (0 = esquerda para direita, 90 = cima para baixo, 135 = diagonal)
     * @return este modifier para encadeamento
     */
    public Modifier backgroundGradient(Color from, Color to, double angle) {
        this.gradientFrom = from;
        this.gradientTo = to;
        this.gradientAngle = angle;
        return this;
    }

    /**
     * Define uma borda com cor e espessura.
     *
     * <pre>{@code
     * new Modifier().border(Color.GRAY, 1)
     * }</pre>
     *
     * @param color {@code Color} — cor da borda (ex: Color.GRAY, Color.web("#cccccc"))
     * @param width {@code double} — espessura da borda em pixels (ex: 1, 2)
     * @return este modifier para encadeamento
     */
    public Modifier border(Color color, double width) {
        this.borderColor = color;
        this.borderWidth = width;
        return this;
    }

    /**
     * Define o raio dos cantos arredondados.
     *
     * <pre>{@code
     * new Modifier().background(Color.LIGHTGRAY).borderRadius(8)
     * }</pre>
     *
     * @param radius {@code double} — raio dos cantos em pixels (ex: 4, 8, 16)
     * @return este modifier para encadeamento
     */
    public Modifier borderRadius(double radius) {
        this.borderRadius = radius;
        return this;
    }

    /**
     * Define a opacidade do componente.
     *
     * @param opacity {@code double} — valor entre 0.0 (transparente) e 1.0 (opaco)
     * @return este modifier para encadeamento
     */
    public Modifier opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    /**
     * Define se o componente esta visivel.
     * Quando oculto, o componente tambem deixa de ocupar espaco no layout.
     *
     * @param visible {@code boolean} — true para visivel, false para oculto
     * @return este modifier para encadeamento
     */
    public Modifier visible(boolean visible) {
        this.visible = visible;
        this.visibleSet = true;
        return this;
    }

    /** @return o valor de gap configurado */
    public double getGap() {
        return gap;
    }

    /** @return o tamanho da fonte configurado, ou -1 se nao definido */
    public double getFontSize() {
        return fontSize;
    }

    /** @return true se o texto esta a negrito */
    public boolean isBold() {
        return bold;
    }

    /** @return a cor do texto configurada, ou null se nao definida */
    public Color getTextColor() {
        return textColor;
    }

    /** @return o alinhamento configurado, ou null se nao definido */
    public Pos getAlignment() {
        return alignment;
    }

    /** @return a margin como Insets, ou null se nao definida */
    public Insets getMargin() {
        if (marginTop == 0 && marginRight == 0 && marginBottom == 0 && marginLeft == 0) {
            return null;
        }
        return new Insets(marginTop, marginRight, marginBottom, marginLeft);
    }

    /**
     * Aplica todas as propriedades deste modifier a um {@link Region} JavaFX.
     * Usado internamente pelos componentes no metodo {@code render()}.
     *
     * @param node {@code Region} — o node JavaFX onde aplicar as propriedades
     */
    public void applyTo(Region node) {
        // padding
        if (paddingTop != 0 || paddingRight != 0 || paddingBottom != 0 || paddingLeft != 0) {
            node.setPadding(new Insets(paddingTop, paddingRight, paddingBottom, paddingLeft));
        }

        // dimensoes fixas
        if (width >= 0) {
            node.setPrefWidth(width);
            node.setMinWidth(width);
            node.setMaxWidth(width);
        }
        if (height >= 0) {
            node.setPrefHeight(height);
            node.setMinHeight(height);
            node.setMaxHeight(height);
        }

        // fill max
        if (fillMaxWidth) {
            node.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(node, Priority.ALWAYS);
        }
        if (fillMaxHeight) {
            node.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(node, Priority.ALWAYS);
        }

        // opacidade
        if (opacity >= 0) {
            node.setOpacity(opacity);
        }

        // visibilidade
        if (visibleSet) {
            node.setVisible(visible);
            node.setManaged(visible);
        }

        // estilos css (background, border, borderRadius)
        StringBuilder css = new StringBuilder(node.getStyle());

        if (backgroundColor != null) {
            css.append(String.format("-fx-background-color: #%02x%02x%02x;",
                (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255)));
        }

        if (gradientFrom != null && gradientTo != null) {
            double rad = Math.toRadians(gradientAngle);
            double x1 = 50 - 50 * Math.sin(rad);
            double y1 = 50 - 50 * Math.cos(rad);
            double x2 = 50 + 50 * Math.sin(rad);
            double y2 = 50 + 50 * Math.cos(rad);
            css.append(String.format(java.util.Locale.US,
                "-fx-background-color: linear-gradient(from %.0f%% %.0f%% to %.0f%% %.0f%%, %s, %s);",
                x1, y1, x2, y2,
                toHex(gradientFrom),
                toHex(gradientTo)));
        }

        if (borderRadius >= 0) {
            css.append(String.format("-fx-background-radius: %.1f;", borderRadius));
            css.append(String.format("-fx-border-radius: %.1f;", borderRadius));
        }

        if (borderColor != null && borderWidth >= 0) {
            css.append(String.format("-fx-border-color: #%02x%02x%02x;",
                (int) (borderColor.getRed() * 255),
                (int) (borderColor.getGreen() * 255),
                (int) (borderColor.getBlue() * 255)));
            css.append(String.format("-fx-border-width: %.1f;", borderWidth));
        }

        // propriedades de texto como inline CSS (ganha sobre stylesheets do tema)
        if (node instanceof Labeled) {
            if (fontSize > 0) {
                css.append(String.format("-fx-font-size: %.1fpx;", fontSize));
            }
            if (bold) {
                css.append("-fx-font-weight: bold;");
            }
            if (italic) {
                css.append("-fx-font-style: italic;");
            }
            if (fontFamily != null) {
                css.append(String.format("-fx-font-family: \"%s\";", fontFamily));
            }
            if (textColor != null) {
                css.append(String.format("-fx-text-fill: #%02x%02x%02x;",
                    (int) (textColor.getRed() * 255),
                    (int) (textColor.getGreen() * 255),
                    (int) (textColor.getBlue() * 255)));
            }
        }

        if (!css.isEmpty()) {
            node.setStyle(css.toString());
        }
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
