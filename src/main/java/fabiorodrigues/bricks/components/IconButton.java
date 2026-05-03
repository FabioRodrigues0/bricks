package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Botao com icone Ikonli, opcionalmente com texto ao lado.
 * Usa o Button Bricks internamente para manter consistencia de estilos.
 *
 * <pre>{@code
 * // So icone
 * new IconButton("fas-trash")
 *     .onClick(() -> apagar())
 *     .tooltip("Eliminar")
 *
 * // Icone com texto
 * new IconButton("fas-plus", "Adicionar")
 *     .onClick(() -> adicionar())
 *
 * // Com tamanho e cor
 * new IconButton("fas-edit")
 *     .size(18)
 *     .color(Color.web("#fa8742"))
 *     .onClick(() -> editar())
 *     .tooltip("Editar")
 *
 * // Variante danger
 * new IconButton("fas-trash")
 *     .danger()
 *     .onClick(() -> apagar())
 * }</pre>
 */
public class IconButton implements Component {

    private final String iconCode;
    private String text = null;
    private int size = 16;
    private Color color = null;
    private boolean danger = false;
    private Runnable onClick;
    private String tooltip = null;
    private Modifier modifier;

    /**
     * Cria um botao so com icone.
     *
     * @param iconCode codigo do icone Ikonli (ex: "fas-trash", "fas-plus")
     */
    public IconButton(String iconCode) {
        this.iconCode = iconCode;
    }

    /**
     * Cria um botao com icone e texto ao lado.
     *
     * @param iconCode codigo do icone Ikonli
     * @param text     texto a mostrar ao lado do icone
     */
    public IconButton(String iconCode, String text) {
        this.iconCode = iconCode;
        this.text = text;
    }

    /**
     * Define o tamanho do icone em pixels (por defeito: 16).
     *
     * @param size tamanho em pixels
     * @return este componente para encadeamento
     */
    public IconButton size(int size) {
        this.size = size;
        return this;
    }

    /**
     * Define a cor do icone.
     *
     * @param color a cor
     * @return este componente para encadeamento
     */
    public IconButton color(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Aplica estilo de perigo ao botao (adiciona a classe CSS "bricks-button-danger").
     *
     * @return este componente para encadeamento
     */
    public IconButton danger() {
        this.danger = true;
        return this;
    }

    /**
     * Define a acao ao clicar no botao.
     *
     * @param handler {@code Runnable} — acao a executar
     * @return este componente para encadeamento
     */
    public IconButton onClick(Runnable handler) {
        this.onClick = handler;
        return this;
    }

    /**
     * Define o tooltip mostrado ao passar o rato por cima.
     *
     * @param tooltipText texto do tooltip
     * @return este componente para encadeamento
     */
    public IconButton tooltip(String tooltipText) {
        this.tooltip = tooltipText;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais adicionais.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public IconButton modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        Button bricksButton = new Button(text != null ? text : "");
        if (onClick != null) bricksButton.onClick(onClick);
        if (modifier != null) bricksButton.modifier(modifier);

        javafx.scene.control.Button fxButton =
                (javafx.scene.control.Button) bricksButton.render();

        if (danger) {
            fxButton.getStyleClass().add("bricks-button-danger");
        }

        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(size);
        if (color != null) icon.setIconColor(color);
        fxButton.setGraphic(icon);

        if (tooltip != null) {
            fxButton.setTooltip(new Tooltip(tooltip));
        }

        return fxButton;
    }
}
