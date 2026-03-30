package fabiorodrigues.bricks.core;

import javafx.scene.Node;

/**
 * Interface base de todos os componentes Bricks.
 *
 * <p>Cada componente sabe converter-se num {@link Node} JavaFX
 * atraves do metodo {@link #render()}.</p>
 *
 * <pre>{@code
 * Component titulo = new Text("Ola Mundo").fontSize(24);
 * Node node = titulo.render(); // devolve um Label JavaFX
 * }</pre>
 */
public interface Component {
    /**
     * Converte este componente num {@link Node} JavaFX pronto a ser adicionado ao scene graph.
     *
     * @return o Node JavaFX correspondente a este componente
     */
    Node render();
}
