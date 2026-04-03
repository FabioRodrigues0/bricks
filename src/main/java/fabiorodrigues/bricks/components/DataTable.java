package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.StateList;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tabela de dados com suporte a pesquisa, ordenação, paginação, seleção de linhas
 * e ações por linha e por toolbar.
 *
 * <p>Gere o seu próprio estado interno — não usa {@code BricksApplication.state()}.
 * O re-render parcial (header + linhas + footer) acontece dentro do próprio componente,
 * sem desencadear um re-render de toda a aplicação.</p>
 *
 * <pre>{@code
 * new DataTable<Aluno>()
 *     .items(alunos)
 *     .searchable()
 *     .columnToggle()
 *     .toolbarAction("Exportar", () -> exportar())
 *     .toolbarAction("Apagar selecionados",
 *         () -> apagar(tabela.getSelected()),
 *         SelectionMode.REQUIRES_SELECTION)
 *     .column("Nome", Aluno::nome).bold()
 *     .column("Turma", Aluno::turma).width(80).align(Align.CENTER)
 *     .actionColumn(new TableAction<Aluno>()
 *         .icon("fas-pencil").tooltip("Editar").onClick(a -> editar(a)))
 *     .actionColumn(new TableAction<Aluno>()
 *         .icon("fas-trash").tooltip("Apagar").danger().onClick(a -> apagar(a)))
 *     .selectable()
 * }</pre>
 *
 * @param <T> o tipo dos itens da tabela
 */
public class DataTable<T> implements Component {

    // ── estado partilhado com os subcomponentes ───────────────────────────────────

    static class DataTableState<T> {
        List<T> items = new ArrayList<>();
        int pageSize = 20;
        String searchQuery = "";
        int currentPage = 0;
        DataTableColumn<T> sortColumn = null;
        boolean sortAscending = true;
        final Set<T> selectedItems = new HashSet<>();
        final Map<DataTableColumn<T>, Boolean> columnVisible = new LinkedHashMap<>();
        /** Botões da toolbar com REQUIRES_SELECTION, atualizados diretamente em cada refresh. */
        final List<javafx.scene.control.Button> selectionDependentBtns = new ArrayList<>();
        Runnable onRefresh;

        record ActionDef(String label, Runnable action, SelectionMode mode) {}

        List<T> getFilteredAndSorted() {
            List<T> result = new ArrayList<>(items);
            if (!searchQuery.isEmpty()) {
                String query = searchQuery.toLowerCase();
                result = result.stream()
                        .filter(item -> columnVisible.entrySet().stream()
                                .filter(Map.Entry::getValue)
                                .map(Map.Entry::getKey)
                                .anyMatch(col -> {
                                    try {
                                        String val = col.getValueFunction().apply(item);
                                        return val != null && val.toLowerCase().contains(query);
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }))
                        .collect(Collectors.toList());
            }
            if (sortColumn != null) {
                final boolean asc = sortAscending;
                result.sort((a, b) -> {
                    String va = sortColumn.getValueFunction().apply(a);
                    String vb = sortColumn.getValueFunction().apply(b);
                    if (va == null) va = "";
                    if (vb == null) vb = "";
                    return asc ? va.compareToIgnoreCase(vb) : vb.compareToIgnoreCase(va);
                });
            }
            return result;
        }

        List<T> getPageItems(List<T> all) {
            int from = currentPage * pageSize;
            if (from >= all.size()) return Collections.emptyList();
            return all.subList(from, Math.min(from + pageSize, all.size()));
        }

        int totalPages(List<T> all) {
            return Math.max(1, (int) Math.ceil((double) all.size() / pageSize));
        }
    }

    // ── configuração ─────────────────────────────────────────────────────────────

    private List<T> items = new ArrayList<>();
    private boolean searchable = false;
    private boolean columnToggle = false;
    private boolean selectable = false;
    private final List<DataTableColumn<T>> columns = new ArrayList<>();
    private final List<TableAction<T>> tableActions = new ArrayList<>();
    private final List<DataTableState.ActionDef> toolbarActionDefs = new ArrayList<>();
    private int pageSize = 20;
    private DataTableColumn<T> lastColumn;
    private Modifier modifier;

    // Estado persistente — sobrevive a re-renders da app, mantém seleção e página atual
    private final DataTableState<T> state = new DataTableState<>();

    // ── métodos builder ───────────────────────────────────────────────────────────

    /**
     * Define a fonte de dados como {@code List} estática.
     *
     * @param items lista de itens
     * @return este componente para encadeamento
     */
    public DataTable<T> items(List<T> items) {
        this.items = new ArrayList<>(items);
        return this;
    }

    /**
     * Define a fonte de dados como {@link StateList} reativa.
     *
     * @param items lista reativa de itens
     * @return este componente para encadeamento
     */
    public DataTable<T> items(StateList<T> items) {
        this.items = new ArrayList<>(items.get());
        return this;
    }

    /** Ativa o campo de pesquisa na toolbar. */
    public DataTable<T> searchable() {
        this.searchable = true;
        return this;
    }

    /** Ativa o botão de toggle de colunas na toolbar. */
    public DataTable<T> columnToggle() {
        this.columnToggle = true;
        return this;
    }

    /** Ativa a seleção de linhas com checkbox e clique na linha. */
    public DataTable<T> selectable() {
        this.selectable = true;
        return this;
    }

    /**
     * Adiciona uma coluna de dados. Os métodos {@link #bold()}, {@link #width(double)},
     * {@link #align(Align)} e {@link #wrapText()} seguintes aplicam-se a esta coluna.
     *
     * @param name          cabeçalho da coluna
     * @param valueFunction função que extrai o valor a apresentar
     * @return este componente para encadeamento
     */
    public DataTable<T> column(String name, Function<T, String> valueFunction) {
        lastColumn = new DataTableColumn<>(name, valueFunction);
        columns.add(lastColumn);
        return this;
    }

    /** Aplica negrito à última coluna adicionada. */
    public DataTable<T> bold() {
        if (lastColumn != null) lastColumn.setBold(true);
        return this;
    }

    /**
     * Define a largura fixa (px) da última coluna adicionada.
     *
     * @param w largura em pixels
     * @return este componente para encadeamento
     */
    public DataTable<T> width(double w) {
        if (lastColumn != null) lastColumn.setWidth(w);
        return this;
    }

    /**
     * Define o alinhamento da última coluna adicionada.
     *
     * @param align {@link Align#LEFT}, {@link Align#CENTER} ou {@link Align#RIGHT}
     * @return este componente para encadeamento
     */
    public DataTable<T> align(Align align) {
        if (lastColumn != null) lastColumn.setAlign(align);
        return this;
    }

    /** Ativa o word wrap na última coluna adicionada. */
    public DataTable<T> wrapText() {
        if (lastColumn != null) lastColumn.setWrapText(true);
        return this;
    }

    /**
     * Adiciona uma coluna de ação — botão compacto 32×32 com ícone, por linha.
     *
     * @param action definição da ação
     * @return este componente para encadeamento
     */
    public DataTable<T> actionColumn(TableAction<T> action) {
        tableActions.add(action);
        return this;
    }

    /**
     * Adiciona um botão à toolbar, sempre ativo.
     *
     * @param label  texto do botão
     * @param action ação a executar
     * @return este componente para encadeamento
     */
    public DataTable<T> toolbarAction(String label, Runnable action) {
        toolbarActionDefs.add(new DataTableState.ActionDef(label, action, SelectionMode.ALWAYS));
        return this;
    }

    /**
     * Adiciona um botão à toolbar com controlo de disponibilidade.
     *
     * @param label  texto do botão
     * @param action ação a executar
     * @param mode   {@link SelectionMode#ALWAYS} ou {@link SelectionMode#REQUIRES_SELECTION}
     * @return este componente para encadeamento
     */
    public DataTable<T> toolbarAction(String label, Runnable action, SelectionMode mode) {
        toolbarActionDefs.add(new DataTableState.ActionDef(label, action, mode));
        return this;
    }

    /**
     * Define o número de itens por página (por defeito: 20).
     *
     * @param size número de itens por página
     * @return este componente para encadeamento
     */
    public DataTable<T> pageSize(int size) {
        this.pageSize = size;
        return this;
    }

    /**
     * Aplica um {@link Modifier} ao contentor raiz.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public DataTable<T> modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Devolve os itens atualmente selecionados.
     *
     * @return conjunto imutável de itens selecionados
     */
    public Set<T> getSelected() {
        return Collections.unmodifiableSet(state.selectedItems);
    }

    // ── render — só orquestra subcomponentes ──────────────────────────────────────

    @Override
    public Node render() {
        // Atualiza o estado persistente com a configuração atual
        state.items = this.items;
        state.pageSize = this.pageSize;
        for (DataTableColumn<T> col : columns) {
            state.columnVisible.putIfAbsent(col, true);
        }

        // Raiz via Bricks Column (cast a VBox para poder adicionar filhos diretamente)
        Column root = new Column();
        VBox rootNode = (VBox) root.render();
        rootNode.setMaxWidth(Double.MAX_VALUE);

        // Body — contentor com referência para refresh parcial (direto VBox necessário)
        VBox body = new VBox();
        VBox.setVgrow(body, Priority.ALWAYS);

        // Footer — contentor que é recriado em cada refresh
        VBox footerContainer = new VBox();

        // ── callback de refresh parcial (não re-renderiza a app inteira) ──────────
        state.onRefresh = () -> {
            // Valida página atual
            List<T> all = state.getFilteredAndSorted();
            if (state.currentPage >= state.totalPages(all)) {
                state.currentPage = 0;
            }

            // Reconstrói header + linhas
            body.getChildren().clear();
            body.getChildren().add(
                    new DataTableHeader<>(state, columns, selectable, tableActions).render()
            );
            body.getChildren().add(new Divider().render());
            body.getChildren().add(
                    new DataTableBody<>(state, columns, selectable, tableActions).render()
            );

            // Reconstrói footer (paginação muda a cada refresh)
            footerContainer.getChildren().clear();
            footerContainer.getChildren().add(
                    new DataTableFooter<>(state, pageSize).render()
            );

            // Atualiza botões da toolbar que dependem de seleção
            boolean hasSelection = !state.selectedItems.isEmpty();
            for (javafx.scene.control.Button btn : state.selectionDependentBtns) {
                btn.setDisable(!hasSelection);
            }
        };

        // ── toolbar (opcional, construída uma vez) ────────────────────────────────
        boolean needsToolbar = searchable || columnToggle || !toolbarActionDefs.isEmpty();
        if (needsToolbar) {
            state.selectionDependentBtns.clear();
            rootNode.getChildren().add(
                    new DataTableToolbar<>(state, searchable, columnToggle,
                            toolbarActionDefs, columns).render()
            );
            rootNode.getChildren().add(new Divider().render());
        }

        rootNode.getChildren().add(body);
        rootNode.getChildren().add(footerContainer);

        // Render inicial
        state.onRefresh.run();

        if (modifier != null) {
            modifier.applyTo(rootNode);
        }

        return rootNode;
    }
}
