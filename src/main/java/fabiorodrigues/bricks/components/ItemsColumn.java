package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.StateList;
import fabiorodrigues.bricks.style.Modifier;
import java.util.List;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Lista não virtualizada. Itera todos os items e cresce verticalmente
 * conforme o número de items. Pensado para listas pequenas/médias onde
 * não há necessidade de scroll virtual.
 *
 * <p>Diferenças vs {@link LazyColumn}:</p>
 * <ul>
 *   <li>Renderiza todos os items de uma vez (sem virtualização).</li>
 *   <li>Sem scroll interno — o container pai decide se scrolla.</li>
 *   <li>Sem {@code itemHeight}, {@code buffer} ou {@code scrollState}.</li>
 * </ul>
 *
 * <p>Uso básico:</p>
 * <pre>{@code
 * new ItemsColumn<Note>()
 *     .gap(12)
 *     .padding(16)
 *     .items(notas)
 *     .emptyState(new Text("Nenhuma nota."))
 *     .item(nota ->
 *         new Card().padding(16).children(
 *             new Text(nota.getTitle()).fontSize(15),
 *             new Text(nota.getContent()).fontSize(13)
 *         )
 *     )
 * }</pre>
 *
 * <p>Grelha com 2 colunas:</p>
 * <pre>{@code
 * new ItemsColumn<Item>()
 *     .columns(2)
 *     .gap(12)
 *     .items(lista)
 *     .item(it -> new Card().children(new Text(it.getName())))
 * }</pre>
 *
 * @param <T> o tipo dos items da lista
 */
public class ItemsColumn<T> implements Component {

    private List<T> items;
    private StateList<T> stateItems;
    private Function<T, Component> itemTemplate;
    private Component emptyState;
    private double gap = 0;
    private double padding = 0;
    private double itemHeight = -1;
    private int columns = 1;
    private Modifier modifier;

    /**
     * Define o espaço entre items (vertical entre linhas, horizontal entre colunas).
     *
     * @param gap {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Define o espaço interno da lista.
     *
     * @param padding {@code double} — valor em pixels
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> padding(double padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Define a fonte de dados como {@link List} estática.
     *
     * @param items {@code List<T>} — lista de items a mostrar
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> items(List<T> items) {
        this.items = items;
        return this;
    }

    /**
     * Define a fonte de dados como {@link StateList} reativa.
     * Lê o snapshot atual da lista no momento do render.
     *
     * @param items {@code StateList<T>} — lista reativa de items
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> items(StateList<T> items) {
        this.stateItems = items;
        return this;
    }

    /**
     * Define como cada item é construído visualmente.
     *
     * @param template {@code Function<T, Component>} — função que recebe um item e devolve um componente
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> item(Function<T, Component> template) {
        this.itemTemplate = template;
        return this;
    }

    /**
     * Define o componente a mostrar quando a lista está vazia.
     *
     * @param component {@code Component} — componente de estado vazio
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> emptyState(Component component) {
        this.emptyState = component;
        return this;
    }

    /**
     * Normaliza a altura de cada item. Útil para grelhas onde todos os cards
     * devem ter a mesma altura independentemente do conteúdo.
     *
     * @param height {@code double} — altura em pixels (ex: 80, 120); {@code -1} desativa
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> itemHeight(double height) {
        this.itemHeight = height;
        return this;
    }

    /**
     * Define o número de colunas por linha (modo grelha).
     * Default {@code 1} mantém o comportamento vertical clássico.
     *
     * <p>Items distribuídos da esquerda para a direita, top-down.
     * Última linha incompleta alinhada à esquerda com espaço vazio à direita.</p>
     *
     * @param columns {@code int} — número de colunas (mínimo 1)
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> columns(int columns) {
        this.columns = Math.max(1, columns);
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizáveis.
     *
     * @param modifier {@code Modifier} — o modifier a aplicar
     * @return este componente para encadeamento
     */
    public ItemsColumn<T> modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        List<T> lista = stateItems != null ? stateItems.get() : items;

        if (lista == null || (lista.isEmpty() && emptyState != null)) {
            return emptyState != null ? emptyState.render() : new VBox();
        }

        VBox container = new VBox(gap);
        container.setPadding(new Insets(padding));

        if (modifier != null) {
            modifier.applyTo(container);
        }

        if (lista == null || lista.isEmpty()) {
            return container;
        }

        int cols = Math.max(1, columns);

        if (cols == 1) {
            for (T item : lista) {
                Node node = itemTemplate.apply(item).render();
                applyItemHeight(node);
                container.getChildren().add(node);
            }
        } else {
            for (int i = 0; i < lista.size(); i += cols) {
                HBox row = new HBox(gap);
                int end = Math.min(i + cols, lista.size());
                for (int j = i; j < end; j++) {
                    Node node = itemTemplate.apply(lista.get(j)).render();
                    if (node instanceof Region region) {
                        region.setMaxWidth(Double.MAX_VALUE);
                    }
                    applyItemHeight(node);
                    HBox.setHgrow(node, Priority.ALWAYS);
                    row.getChildren().add(node);
                }
                for (int k = end; k < i + cols; k++) {
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    row.getChildren().add(filler);
                }
                container.getChildren().add(row);
            }
        }

        return container;
    }

    private void applyItemHeight(Node node) {
        if (itemHeight > 0 && node instanceof Region region) {
            region.setMinHeight(itemHeight);
            region.setPrefHeight(itemHeight);
            region.setMaxHeight(itemHeight);
        }
    }
}
