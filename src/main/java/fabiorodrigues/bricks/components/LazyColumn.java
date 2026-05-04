package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.StateList;
import fabiorodrigues.bricks.style.Modifier;
import java.util.List;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Lista virtualizada de alto desempenho com {@link ListView} JavaFX.
 * Cria e destrói nodes conforme o utilizador faz scroll — ideal para listas longas.
 *
 * <p>Uso básico:</p>
 * <pre>{@code
 * new LazyColumn<Note>()
 *     .gap(12)
 *     .padding(16)
 *     .items(notas)
 *     .emptyState(new Text("Nenhuma nota."))
 *     .render(nota ->
 *         new Card().padding(16).children(
 *             new Text(nota.getTitle()).fontSize(15),
 *             new Text(nota.getContent()).fontSize(13)
 *         )
 *     )
 * }</pre>
 *
 * <p>Com altura fixa por item (melhora performance):</p>
 * <pre>{@code
 * new LazyColumn<Note>()
 *     .items(notas)
 *     .itemHeight(80)
 *     .render(nota -> ...)
 * }</pre>
 *
 * @param <T> o tipo dos itens da lista
 */
public class LazyColumn<T> implements Component {

    private List<T> items;
    private StateList<T> stateItems;
    private Function<T, Component> itemTemplate;
    private Component emptyState;
    private double gap = 0;
    private double padding = 0;
    private double itemHeight = -1;
    private int buffer = 5;
    private Modifier modifier;

    /**
     * Define o espaço entre itens.
     *
     * @param gap {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public LazyColumn<T> gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Define o espaço interno da lista.
     *
     * @param padding {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public LazyColumn<T> padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Define a fonte de dados como {@link List} estática.
     *
     * @param items {@code List<T>} — lista de itens a mostrar
     * @return este componente para encadeamento
     */
    public LazyColumn<T> items(List<T> items) {
        this.items = items;
        return this;
    }

    /**
     * Define a fonte de dados como {@link StateList} reativa.
     * O componente lê o snapshot atual da lista no momento do render.
     *
     * @param items {@code StateList<T>} — lista reativa de itens
     * @return este componente para encadeamento
     */
    public LazyColumn<T> items(StateList<T> items) {
        this.stateItems = items;
        return this;
    }

    /**
     * Define como cada item é construído visualmente.
     *
     * @param template {@code Function<T, Component>} — função que recebe um item e devolve um componente
     * @return este componente para encadeamento
     */
    public LazyColumn<T> item(Function<T, Component> template) {
        this.itemTemplate = template;
        return this;
    }

    /**
     * Define o componente a mostrar quando a lista está vazia.
     *
     * @param component {@code Component} — componente de estado vazio
     * @return este componente para encadeamento
     */
    public LazyColumn<T> emptyState(Component component) {
        this.emptyState = component;
        return this;
    }

    /**
     * Define a altura fixa por item. Melhora a performance do scroll
     * pois o ListView consegue calcular posições sem medir cada cell.
     *
     * @param height {@code double} — altura em pixels (ex: 80, 120)
     * @return este componente para encadeamento
     */
    public LazyColumn<T> itemHeight(double height) {
        this.itemHeight = height;
        return this;
    }

    /**
     * Define o número de itens extra renderizados fora do viewport.
     * Valores mais altos reduzem o flickering ao fazer scroll rápido.
     *
     * @param buffer {@code int} — número de itens extra (default: 5)
     * @return este componente para encadeamento
     */
    public LazyColumn<T> buffer(int buffer) {
        this.buffer = buffer;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizáveis.
     *
     * @param modifier {@code Modifier} — o modifier a aplicar
     * @return este componente para encadeamento
     */
    public LazyColumn<T> modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        List<T> lista = stateItems != null ? stateItems.get() : items;

        if (lista == null || (lista.isEmpty() && emptyState != null)) {
            return emptyState != null ? emptyState.render() : new VBox();
        }

        ListView<T> listView = new ListView<>();
        listView.getStyleClass().add("bricks-lazy-column");
        listView.getItems().addAll(lista);
        listView.setStyle(
            "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 0;"
        );

        if (itemHeight > 0) {
            listView.setFixedCellSize(itemHeight);
        }

        double halfGap = gap / 2;
        listView.setCellFactory(lv ->
            new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                    } else {
                        setGraphic(itemTemplate.apply(item).render());
                        setStyle(
                            String.format(
                                "-fx-background-color: transparent; -fx-padding: %.1f 0 %.1f 0;",
                                halfGap,
                                halfGap
                            )
                        );
                    }
                }
            }
        );

        VBox wrapper = new VBox();

        if (modifier != null) {
            modifier.applyTo(listView);
            modifier.applyTo(wrapper);
        }

        wrapper.setPadding(new Insets(padding));
        wrapper.getChildren().add(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        return wrapper;
    }
}
