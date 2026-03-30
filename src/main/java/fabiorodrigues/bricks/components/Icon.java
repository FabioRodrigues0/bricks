package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Componente de icone usando FontAwesome 5 (via Ikonli).
 *
 * <p>Usa o codigo do icone em formato string:</p>
 * <ul>
 *   <li>{@code "fas-"} para FontAwesome Solid (ex: "fas-home", "fas-user", "fas-trash")</li>
 *   <li>{@code "far-"} para FontAwesome Regular (ex: "far-bell", "far-star")</li>
 *   <li>{@code "fab-"} para FontAwesome Brands (ex: "fab-github", "fab-google")</li>
 * </ul>
 *
 * <pre>{@code
 * new Icon("fas-home")
 * new Icon("fas-user").size(24).color(Color.BLUE)
 * new Icon("fas-trash").size(16).color(Color.RED)
 * }</pre>
 */
public class Icon implements Component {

    private final String iconCode;
    private double size = 16;
    private Color color;
    private Modifier modifier;

    /**
     * Cria um icone a partir do codigo em string.
     *
     * <pre>{@code
     * new Icon("fas-home")
     * new Icon("far-bell")
     * new Icon("fab-github")
     * }</pre>
     *
     * @param iconCode o codigo do icone (ex: "fas-home", "far-bell", "fab-github")
     */
    public Icon(String iconCode) {
        this.iconCode = iconCode;
    }

    /**
     * Define o tamanho do icone em pixels (por defeito: 16).
     *
     * @param size tamanho em pixels
     * @return este componente para encadeamento
     */
    public Icon size(double size) {
        this.size = size;
        return this;
    }

    /**
     * Define a cor do icone.
     *
     * @param color a cor
     * @return este componente para encadeamento
     */
    public Icon color(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Icon modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize((int) size);

        if (color != null) {
            icon.setIconColor(color);
        }

        return icon;
    }
}
