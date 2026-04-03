package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Toolbar do {@link DataTable}: campo de pesquisa, botões de ação e toggle de colunas.
 * Classe de pacote — não faz parte da API pública.
 */
class DataTableToolbar<T> implements Component {

    private final DataTable.DataTableState<T> state;
    private final boolean searchable;
    private final boolean columnToggle;
    private final List<DataTable.DataTableState.ActionDef> actions;
    private final List<DataTableColumn<T>> columns;

    DataTableToolbar(DataTable.DataTableState<T> state, boolean searchable, boolean columnToggle,
                     List<DataTable.DataTableState.ActionDef> actions,
                     List<DataTableColumn<T>> columns) {
        this.state = state;
        this.searchable = searchable;
        this.columnToggle = columnToggle;
        this.actions = actions;
        this.columns = columns;
    }

    @Override
    public Node render() {
        List<Component> children = new ArrayList<>();

        // ── campo de pesquisa (Bricks TextField) ─────────────────────────────────
        if (searchable) {
            Node searchNode = new TextField()
                    .placeholder("Pesquisar...")
                    .onChange(val -> {
                        state.searchQuery = val == null ? "" : val;
                        state.currentPage = 0;
                        state.onRefresh.run();
                    })
                    .render();
            if (searchNode instanceof javafx.scene.control.TextField tf) {
                tf.setPrefWidth(220);
            }
            children.add(() -> searchNode);
        }

        // ── spacer (Bricks Spacer) ────────────────────────────────────────────────
        children.add(new Spacer());

        // ── botões de ação (Bricks Button) ────────────────────────────────────────
        for (DataTable.DataTableState.ActionDef def : actions) {
            boolean requiresSelection = def.mode() == SelectionMode.REQUIRES_SELECTION;
            boolean hasSelection = !state.selectedItems.isEmpty();

            Node btnNode = new Button(def.label())
                    .enabled(!requiresSelection || hasSelection)
                    .onClick(def.action())
                    .render();

            children.add(() -> btnNode);

            if (requiresSelection && btnNode instanceof javafx.scene.control.Button jfxBtn) {
                state.selectionDependentBtns.add(jfxBtn);
            }
        }

        // ── toggle de colunas ─────────────────────────────────────────────────────
        if (columnToggle) {
            DropdownMenu colMenu = new DropdownMenu()
                    .icon("fas-cog")
                    .tooltip("Colunas visíveis");

            for (DataTableColumn<T> col : columns) {
                colMenu.item(new DropdownCheckItem(col.getName())
                        .checked(Boolean.TRUE.equals(state.columnVisible.get(col)))
                        .onChange(val -> {
                            state.columnVisible.put(col, val);
                            state.onRefresh.run();
                        }));
            }

            children.add(colMenu);
        }

        return new Row()
                .gap(8)
                .padding(8)
                .children(children.toArray(new Component[0]))
                .render();
    }
}
