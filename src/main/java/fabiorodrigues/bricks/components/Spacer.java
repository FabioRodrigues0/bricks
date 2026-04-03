package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Espaco flexivel que ocupa o espaco disponivel entre componentes.
 * Equivalente ao {@code Spacer} do Jetpack Compose.
 *
 * <p>Dentro de um {@code Row}, expande horizontalmente:</p>
 * <pre>{@code
 * new Row().children(
 *     new Text("Esquerda"),
 *     new Spacer(),
 *     new Text("Direita")
 * )
 * }</pre>
 *
 * <p>Dentro de um {@code Column}, expande verticalmente:</p>
 * <pre>{@code
 * new Column().children(
 *     new Text("Topo"),
 *     new Spacer(),
 *     new Text("Fundo")
 * )
 * }</pre>
 *
 * <p>Tamanho fixo (quando nao se quer expansao):</p>
 * <pre>{@code
 * new Spacer(16) // 16px fixos
 * }</pre>
 */
public class Spacer implements Component {

    private final double fixedSize;

    /**
     * Cria um spacer flexivel que ocupa todo o espaco disponivel.
     */
    public Spacer() {
        this.fixedSize = -1;
    }

    /**
     * Cria um spacer com tamanho fixo.
     *
     * @param size tamanho em pixels
     */
    public Spacer(double size) {
        this.fixedSize = size;
    }

    @Override
    public Node render() {
        Region region = new Region();

        if (fixedSize >= 0) {
            region.setMinWidth(fixedSize);
            region.setPrefWidth(fixedSize);
            region.setMaxWidth(fixedSize);
        } else {
            HBox.setHgrow(region, Priority.ALWAYS);
            VBox.setVgrow(region, Priority.ALWAYS);
        }

        return region;
    }
}
