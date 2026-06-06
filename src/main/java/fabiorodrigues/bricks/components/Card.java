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
    private Modifier modifier;
    private Color gradientFrom = null;
    private Color gradientTo = null;
    private double gradientAngle = 135;
    private double cornerRadius = 8;
    private Runnable onClick;
    private String coverImagePath = null;
    private double coverImageHeight = 160;
    private boolean coverImagePreserveRatio = false;
    private String coverPlaceholderPath = null;
    private Component coverPlaceholderComponent = null;

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
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     * As propriedades do modifier sobrepõem-se às do card onde houver conflito.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Card modifier(Modifier modifier) {
        this.modifier = modifier;
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

    /**
     * Define uma imagem de capa no topo do card, sem padding.
     * A imagem vai ate as bordas do card respeitando o cornerRadius no topo.
     * O conteudo dos children fica abaixo com o padding normal.
     *
     * <pre>{@code
     * new Card()
     *     .coverImage("/car.png", 160)
     *     .padding(12)
     *     .children(new Text("Ferrari 458").bold())
     * }</pre>
     *
     * @param imagePath caminho da imagem (recurso no classpath ou URL)
     * @param height    altura da imagem em pixels
     * @return este componente para encadeamento
     */
    public Card coverImage(String imagePath, double height) {
        this.coverImagePath = imagePath;
        this.coverImageHeight = height;
        return this;
    }

    /**
     * Define se a imagem de capa preserva a proporcao original.
     * Por defeito false — a imagem preenche toda a largura do card.
     *
     * @param preserve true para preservar proporcao
     * @return este componente para encadeamento
     */
    public Card coverImagePreserveRatio(boolean preserve) {
        this.coverImagePreserveRatio = preserve;
        return this;
    }

    /**
     * Define uma imagem de fallback quando o {@link #coverImage} falha a carregar.
     *
     * <pre>{@code
     * new Card()
     *     .coverImage("/car.png", 160)
     *     .coverPlaceholder("/no-image.png")
     * }</pre>
     *
     * @param imagePath caminho da imagem placeholder (recurso no classpath ou URL)
     * @return este componente para encadeamento
     */
    public Card coverPlaceholder(String imagePath) {
        this.coverPlaceholderPath = imagePath;
        this.coverPlaceholderComponent = null;
        return this;
    }

    /**
     * Define um componente de fallback quando o {@link #coverImage} falha a carregar.
     * Util para construir placeholders custom (icone, texto, gradiente, etc).
     *
     * <pre>{@code
     * new Card()
     *     .coverImage("/car.png", 160)
     *     .coverPlaceholder(new Box()
     *         .modifier(new Modifier().background(Color.LIGHTGRAY).alignment(Pos.CENTER))
     *         .children(() -> new Icon("fas-image").size(48).render()))
     * }</pre>
     *
     * @param component componente a renderizar como placeholder
     * @return este componente para encadeamento
     */
    public Card coverPlaceholder(Component component) {
        this.coverPlaceholderComponent = component;
        this.coverPlaceholderPath = null;
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

        boolean hasCoverSlot = coverImagePath != null
            || coverPlaceholderComponent != null
            || coverPlaceholderPath != null;
        String paddingValue = hasCoverSlot ? "0" : String.format(java.util.Locale.US, "%.1f", padding);
        vbox.setStyle(String.format(java.util.Locale.US,
            "-fx-background-color: %s; -fx-background-radius: %.1f; -fx-border-radius: %.1f; -fx-padding: %s;",
            bgValue, cornerRadius, cornerRadius, paddingValue));

        if (modifier != null) {
            modifier.applyTo(vbox);
        }

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

        if (hasCoverSlot) {
            Node coverNode = buildCoverImage(coverImagePath, vbox);
            if (coverNode == null) {
                if (coverPlaceholderComponent != null) {
                    coverNode = buildCoverFromComponent(coverPlaceholderComponent.render());
                } else if (coverPlaceholderPath != null) {
                    coverNode = buildCoverImage(coverPlaceholderPath, vbox);
                }
            }
            if (coverNode != null) vbox.getChildren().add(coverNode);

            if (!children.isEmpty()) {
                VBox contentBox = new VBox(4);
                contentBox.setPadding(new javafx.geometry.Insets(padding));
                for (Component child : children) {
                    Node node = child.render();
                    if (node != null) {
                        contentBox.getChildren().add(node);
                    }
                }
                if (!contentBox.getChildren().isEmpty()) {
                    vbox.getChildren().add(contentBox);
                }
            }
        } else {
            for (Component child : children) {
                Node node = child.render();
                if (node != null) {
                    vbox.getChildren().add(node);
                }
            }
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

    private Node buildCoverImage(String path, VBox vbox) {
        String resolvedUrl = resolveCoverImageUrl(path);
        if (resolvedUrl == null) return null;

        javafx.scene.image.Image img;
        try {
            img = new javafx.scene.image.Image(resolvedUrl, false);
        } catch (Exception e) {
            return null;
        }
        if (img.isError() || img.getException() != null) return null;

        if (coverImagePreserveRatio) {
            javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
            imgView.setPreserveRatio(true);
            imgView.setFitHeight(coverImageHeight);
            applyCoverClip(imgView);
            return imgView;
        }

        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);

        Runnable updateViewport = () -> {
            double slotW = vbox.getWidth();
            double slotH = coverImageHeight;
            double imgW = img.getWidth();
            double imgH = img.getHeight();
            if (slotW <= 0 || imgW <= 0 || imgH <= 0) return;
            double slotRatio = slotW / slotH;
            double imgRatio = imgW / imgH;
            double vpW, vpH;
            if (imgRatio > slotRatio) {
                vpH = imgH;
                vpW = imgH * slotRatio;
            } else {
                vpW = imgW;
                vpH = imgW / slotRatio;
            }
            double vpX = (imgW - vpW) / 2;
            double vpY = (imgH - vpH) / 2;
            imgView.setViewport(new javafx.geometry.Rectangle2D(vpX, vpY, vpW, vpH));
            imgView.setFitWidth(slotW);
            imgView.setFitHeight(slotH);
        };

        vbox.widthProperty().addListener((obs, o, n) -> updateViewport.run());
        if (img.getProgress() >= 1) {
            updateViewport.run();
        } else {
            img.progressProperty().addListener((obs, o, n) -> {
                if (n.doubleValue() >= 1) updateViewport.run();
            });
        }

        applyCoverClip(imgView);
        return imgView;
    }

    private String resolveCoverImageUrl(String path) {
        if (path == null || path.isBlank()) return null;

        java.io.File file = new java.io.File(path);
        if (file.exists() && file.isFile()) {
            return file.toURI().toString();
        }

        if (path.startsWith("/")) {
            java.net.URL resource = getClass().getResource(path);
            if (resource == null) return null;
            return resource.toExternalForm();
        }

        return path;
    }

    private Node buildCoverFromComponent(Node node) {
        javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane(node);
        wrapper.setMinHeight(coverImageHeight);
        wrapper.setPrefHeight(coverImageHeight);
        wrapper.setMaxHeight(coverImageHeight);
        applyCoverClip(wrapper);
        return wrapper;
    }

    private void applyCoverClip(Node target) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.setArcWidth(cornerRadius * 2);
        clip.setArcHeight(cornerRadius * 2);
        target.layoutBoundsProperty().addListener((obs, old, bounds) -> {
            clip.setWidth(bounds.getWidth());
            clip.setHeight(bounds.getHeight() + cornerRadius);
        });
        target.setClip(clip);
    }
}
