package fabiorodrigues.bricks.core;

import java.util.List;

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

    /**
     * Ligado ao BricksApplication — chamado automaticamente por BricksScene.use().
     * Nao chamar diretamente.
     */
    final void attach(BricksApplication app) {
        this.app = app;
    }

    /**
     * Cria um State ligado ao re-render da app.
     * Quando o valor muda, a UI atualiza automaticamente.
     */
    protected <T> State<T> state(T initial) {
        State<T> s = new State<>(initial);
        s.addListener(() -> javafx.application.Platform.runLater(app::rerender));
        return s;
    }

    /**
     * Cria um StateList ligado ao re-render da app.
     * Quando a lista muda, a UI atualiza automaticamente.
     */
    protected <T> StateList<T> stateList(List<T> initial) {
        StateList<T> sl = new StateList<>(initial);
        sl.addListener(() -> javafx.application.Platform.runLater(app::rerender));
        return sl;
    }
}
