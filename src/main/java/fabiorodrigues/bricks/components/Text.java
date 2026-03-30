package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Componente de texto. Renderiza como um {@link Label} JavaFX.
 *
 * <pre>{@code
 * new Text("Ola Mundo").fontSize(20);
 * new Text("Destaque").modifier(new Modifier().bold().textColor(Color.RED));
 * }</pre>
 */
public class Text implements Component {

    private final String content;
    private Modifier modifier;
    private double fontSize = -1;

    /**
     * Cria um componente de texto com o conteudo dado.
     *
     * @param content o texto a mostrar
     */
    public Text(String content) {
        this.content = content;
    }

    /**
     * Define o tamanho da fonte diretamente (atalho sem precisar de Modifier).
     *
     * @param size tamanho da fonte em pontos
     * @return este componente para encadeamento
     */
    public Text fontSize(double size) {
        this.fontSize = size;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Text modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        Label label = new Label(content);
        label.getStyleClass().add("bricks-text");

        // modifier aplica primeiro (pode definir fontSize entre outros)
        if (modifier != null) {
            modifier.applyTo(label);
        }

        // atalho fontSize como inline style — ganha sobre CSS do tema e sobre modifier
        if (fontSize > 0) {
            String existing = label.getStyle();
            String addition = "-fx-font-size: " + fontSize + "px;";
            label.setStyle(existing == null || existing.isBlank() ? addition : existing + " " + addition);
        }

        return label;
    }
}
