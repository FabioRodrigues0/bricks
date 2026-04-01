package fabiorodrigues.bricks.core;

import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.ThemeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Classe base para aplicacoes Bricks. Gere o ciclo de vida da janela JavaFX,
 * o re-render automatico quando o estado muda, e fornece os metodos
 * {@link #state(Object)} e {@link #derived(Supplier, State[])} para gestao de estado.
 *
 * <p>O tema Material 3 light e aplicado automaticamente. Para personalizar:</p>
 *
 * <pre>{@code
 * public class MinhaApp extends BricksApplication {
 *
 *     private final State<Integer> count = state(0);
 *
 *     {
 *         setTitle("A Minha App");
 *         setTheme(BricksTheme.dark()); // opcional
 *     }
 *
 *     @Override
 *     public Component root() {
 *         return new Column()
 *             .padding(20)
 *             .children(
 *                 new Text("Contador: " + count.get()),
 *                 new Button("+").onClick(() -> count.update(c -> c + 1))
 *             );
 *     }
 *
 *     public static void main(String[] args) { launch(args); }
 * }
 * }</pre>
 */
public abstract class BricksApplication extends Application {

    private StackPane container;
    private String title = "Bricks App";
    private final List<State<?>> states = new ArrayList<>();
    private BricksTheme theme = BricksTheme.material();
    private Scene scene;

    /**
     * Define a arvore de componentes da aplicacao. Chamado a cada re-render.
     *
     * @return o componente raiz da interface
     */
    public abstract Component root();

    /**
     * Define o titulo da janela.
     *
     * @param title {@code String} — o titulo a mostrar na barra da janela
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Define o tema visual da aplicacao.
     * Se chamado antes de {@code start()}, e aplicado no inicio.
     * Se chamado em runtime (ex: toggle dark mode), atualiza imediatamente.
     *
     * <pre>{@code
     * // dark mode
     * setTheme(BricksTheme.dark());
     *
     * // custom
     * setTheme(BricksTheme.material().colorScheme().primary(Color.web("#E91E63")).and());
     * }</pre>
     *
     * @param theme {@code BricksTheme} — o tema a aplicar (null repoe o Material light)
     */
    public void setTheme(BricksTheme theme) {
        this.theme = theme != null ? theme : BricksTheme.material();
        ThemeRegistry.set(this.theme);
        if (scene != null) {
            applyThemeToScene(scene);
        }
    }

    @Override
    public void start(Stage stage) {
        for (State<?> s : states) {
            s.addListener(() -> Platform.runLater(this::rerender));
        }

        ThemeRegistry.set(theme);

        container = new StackPane();
        rerender();

        scene = new Scene(container, 400, 600);
        applyThemeToScene(scene);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * Reconstroi a arvore de componentes e atualiza a janela.
     */
    protected void rerender() {
        container.getChildren().clear();
        container.getChildren().add(root().render());
    }

    /**
     * Cria um {@link State} reativo ligado a esta aplicacao.
     * Quando o valor do estado muda, a aplicacao faz re-render automaticamente.
     *
     * @param initial o valor inicial do estado
     * @param <T> o tipo do valor
     * @return o estado criado
     */
    protected <T> State<T> state(T initial) {
        State<T> s = new State<>(initial);
        states.add(s);
        return s;
    }

    /**
     * Cria um {@link StateList} reativo ligado a esta aplicacao.
     * Quando a lista e mutada, a aplicacao faz re-render automaticamente.
     *
     * <pre>{@code
     * private final StateList<Note> notas = stateList(seedNotes());
     *
     * // mutacao dispara re-render
     * notas.add(novaNota);
     * notas.remove(nota);
     * }</pre>
     *
     * @param initial lista com os valores iniciais
     * @param <T> o tipo dos elementos
     * @return o StateList criado
     */
    protected <T> StateList<T> stateList(List<T> initial) {
        StateList<T> sl = new StateList<>(initial);
        sl.addListener(() -> Platform.runLater(this::rerender));
        return sl;
    }

    /**
     * Cria um {@link DerivedState} ligado a esta aplicacao.
     * O valor e calculado automaticamente a partir das dependencias indicadas.
     * Quando qualquer dependencia muda, o valor e recalculado no proximo re-render.
     *
     * <pre>{@code
     * DerivedState<List<Note>> visiveis = derived(
     *     () -> notas.get().stream().filter(n -> n.matches(filtro.get())).toList(),
     *     notas, filtro
     * );
     * }</pre>
     *
     * @param supplier {@code Supplier<T>} — funcao que calcula o valor derivado
     * @param dependencies {@code State<?>...} — estados dos quais este valor depende
     * @param <T> o tipo do valor calculado
     * @return o estado derivado criado
     */
    protected <T> DerivedState<T> derived(Supplier<T> supplier, State<?>... dependencies) {
        DerivedState<T> ds = new DerivedState<>(supplier, dependencies);
        ds.setOnChanged(() -> Platform.runLater(this::rerender));
        return ds;
    }

    /**
     * Cria um {@link Effect} que executa uma acao quando qualquer dependencia muda.
     * Nao dispara re-render — e independente do ciclo de render da aplicacao.
     * A acao e executada imediatamente uma vez no arranque.
     *
     * <pre>{@code
     * private final Effect autoSave = effect(() -> {
     *     Storage.save(notas.get());
     * }, notas);
     * }</pre>
     *
     * @param action       {@code Runnable} — a acao a executar
     * @param dependencies {@code State<?>...} — os estados a observar
     * @return o effect criado
     */
    protected Effect effect(Runnable action, State<?>... dependencies) {
        return new Effect(action, dependencies);
    }

    private void applyThemeToScene(Scene s) {
        s.getStylesheets().clear();
        s.getStylesheets().add(theme.toDataUri());
    }
}
