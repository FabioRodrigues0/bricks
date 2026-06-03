package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.DerivedState;
import fabiorodrigues.bricks.core.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Componente condicional. Renderiza os filhos apenas quando a condicao e verdadeira.
 *
 * <p>Quando usado com {@link State} criado pela aplicacao, a alteracao do valor
 * dispara o re-render normal da arvore e os filhos aparecem/desaparecem
 * automaticamente.</p>
 *
 * <pre>{@code
 * State<Boolean> mostrarDetalhes = state(false);
 *
 * new Column().children(
 *     new Checkbox("Mostrar detalhes").bindTo(mostrarDetalhes),
 *     new When(mostrarDetalhes).children(
 *         new Text("Detalhes visiveis")
 *     )
 * )
 * }</pre>
 *
 * <p>Tambem aceita uma condicao calculada:</p>
 * <pre>{@code
 * new When(() -> email.get().contains("@"))
 *     .children(new Text("Email valido"))
 * }</pre>
 */
public class When implements Component {

    private Supplier<Boolean> condition = () -> false;
    private final List<Component> children = new ArrayList<>();

    /**
     * Cria um condicional inicialmente falso.
     * Usar {@link #condition(boolean)}, {@link #condition(Supplier)} ou {@link #condition(State)}.
     */
    public When() {
    }

    /**
     * Cria um condicional com valor booleano estatico.
     *
     * @param condition condicao a avaliar
     */
    public When(boolean condition) {
        condition(condition);
    }

    /**
     * Cria um condicional ligado a um {@link State} booleano.
     *
     * @param state state que controla a visibilidade dos filhos
     */
    public When(State<Boolean> state) {
        condition(state);
    }

    /**
     * Cria um condicional ligado a um {@link DerivedState} booleano.
     *
     * @param state estado derivado que controla a visibilidade dos filhos
     */
    public When(DerivedState<Boolean> state) {
        condition(state);
    }

    /**
     * Cria um condicional com condicao calculada no momento do render.
     *
     * @param condition supplier que devolve true para renderizar os filhos
     */
    public When(Supplier<Boolean> condition) {
        condition(condition);
    }

    /**
     * Define uma condicao booleana estatica.
     *
     * @param condition condicao a avaliar
     * @return este componente para encadeamento
     */
    public When condition(boolean condition) {
        this.condition = () -> condition;
        return this;
    }

    /**
     * Define a condicao a partir de um {@link State} booleano.
     *
     * @param state state que controla a visibilidade dos filhos
     * @return este componente para encadeamento
     */
    public When condition(State<Boolean> state) {
        this.condition = () -> state != null && Boolean.TRUE.equals(state.get());
        return this;
    }

    /**
     * Define a condicao a partir de um {@link DerivedState} booleano.
     *
     * @param state estado derivado que controla a visibilidade dos filhos
     * @return este componente para encadeamento
     */
    public When condition(DerivedState<Boolean> state) {
        this.condition = () -> state != null && Boolean.TRUE.equals(state.get());
        return this;
    }

    /**
     * Define uma condicao calculada no momento do render.
     *
     * @param condition supplier que devolve true para renderizar os filhos
     * @return este componente para encadeamento
     */
    public When condition(Supplier<Boolean> condition) {
        this.condition = condition != null ? condition : () -> false;
        return this;
    }

    /**
     * Alias expressivo para {@link #children(Component...)}.
     *
     * @param children componentes a renderizar quando a condicao for verdadeira
     * @return este componente para encadeamento
     */
    public When then(Component... children) {
        return children(children);
    }

    /**
     * Define os componentes filhos a renderizar quando a condicao for verdadeira.
     *
     * @param children componentes condicionais
     * @return este componente para encadeamento
     */
    public When children(Component... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public Node render() {
        if (!Boolean.TRUE.equals(condition.get())) {
            return null;
        }

        if (children.isEmpty()) {
            return new VBox();
        }

        if (children.size() == 1) {
            return children.get(0).render();
        }

        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("bricks-when");
        for (Component child : children) {
            Node node = child.render();
            if (node != null) {
                wrapper.getChildren().add(node);
            }
        }
        return wrapper;
    }
}
