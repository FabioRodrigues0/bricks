package fabiorodrigues.bricks.core;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

/**
 * Classe base para ViewModels Bricks.
 * Contem os states e a logica de negocio separados da UI.
 * Usar em conjunto com {@link BricksScene}.
 *
 * <pre>{@code
 * public class ProfessorViewModel extends BricksViewModel {
 *
 *     public final StateList<Disciplina> disciplinas = stateList(List.of());
 *     public final State<Disciplina> disciplinaSelecionada = state(null);
 *
 *     public void carregarDisciplinas(int idProfessor) {
 *         List<Disciplina> lista = DB.query()
 *             .from("disciplinas")
 *             .where("id_professor", "=", idProfessor)
 *             .execute(Disciplina.class);
 *         disciplinas.clear();
 *         disciplinas.addAll(lista);
 *     }
 * }
 * }</pre>
 */
public abstract class BricksViewModel {

    private BricksApplication app;
    private final List<State<?>> pendingStates = new ArrayList<>();
    private final List<StateList<?>> pendingStateLists = new ArrayList<>();

    /**
     * Ligado ao BricksApplication — chamado automaticamente por BricksScene.use().
     * Nao chamar diretamente.
     */
    final void attach(BricksApplication app) {
        this.app = app;
        for (State<?> s : pendingStates) {
            s.addListener(() -> Platform.runLater(app::rerender));
        }
        pendingStates.clear();
        for (StateList<?> sl : pendingStateLists) {
            sl.addListener(() -> Platform.runLater(app::rerender));
        }
        pendingStateLists.clear();
    }

    /**
     * Cria um State ligado ao re-render da app.
     * Quando o valor muda, a UI atualiza automaticamente.
     *
     * @param initial o valor inicial
     * @param <T>     o tipo do valor
     * @return o estado criado
     */
    protected <T> State<T> state(T initial) {
        State<T> s = new State<>(initial);
        if (app != null) {
            s.addListener(() -> Platform.runLater(app::rerender));
        } else {
            pendingStates.add(s);
        }
        return s;
    }

    /**
     * Cria um StateList ligado ao re-render da app.
     * Quando a lista muda, a UI atualiza automaticamente.
     *
     * @param initial lista com os valores iniciais
     * @param <T>     o tipo dos elementos
     * @return o StateList criado
     */
    protected <T> StateList<T> stateList(List<T> initial) {
        StateList<T> sl = new StateList<>(initial);
        if (app != null) {
            sl.addListener(() -> Platform.runLater(app::rerender));
        } else {
            pendingStateLists.add(sl);
        }
        return sl;
    }
}
