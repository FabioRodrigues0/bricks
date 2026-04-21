package fabiorodrigues.bricks.core;

import java.util.List;

/**
 * Classe base para scenes da aplicacao Bricks.
 * Permite separar a UI em ficheiros independentes em vez de ter tudo no root().
 *
 * <pre>{@code
 * public class LobbyScene extends BricksScene {
 *
 *     private final State<String> filtro = state("");
 *
 *     public LobbyScene(BricksApplication app) {
 *         super(app);
 *     }
 *
 *     @Override
 *     public Component render() {
 *         return new Column().children(
 *             new Button("Professor").onClick(() -> app.navigateTo(new ProfessorScene(app)))
 *         );
 *     }
 * }
 * }</pre>
 */
public abstract class BricksScene {

    protected final BricksApplication app;

    public BricksScene(BricksApplication app) {
        this.app = app;
    }

    /**
     * Cria um State LOCAL a esta scene.
     * Nao esta ligado ao sistema de re-render da app —
     * para re-render usar os states da app ou chamar app.rerender() manualmente.
     * Reset quando a scene e recriada.
     *
     * @param initial o valor inicial
     * @param <T>     o tipo do valor
     * @return o estado criado
     */
    protected <T> State<T> state(T initial) {
        return new State<>(initial);
    }

    /**
     * Cria um StateList LOCAL a esta scene, ligado ao re-render da app.
     * Reset quando a scene e recriada.
     *
     * @param initial lista com os valores iniciais
     * @param <T>     o tipo dos elementos
     * @return o StateList criado
     */
    protected <T> StateList<T> stateList(List<T> initial) {
        StateList<T> sl = new StateList<>(initial);
        sl.addListener(() -> javafx.application.Platform.runLater(app::rerender));
        return sl;
    }

    /**
     * Define a UI desta scene. Chamado pelo BricksApplication em cada re-render.
     *
     * @return o componente raiz da scene
     */
    public abstract Component render();
}
