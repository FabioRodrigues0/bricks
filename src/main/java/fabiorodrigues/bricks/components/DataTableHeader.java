package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Linha de cabeçalho do {@link DataTable}: selecionar tudo, nomes de colunas com
 * ordenação clicável e espaço reservado para as ações.
 * Classe de pacote — não faz parte da API pública.
 */
class DataTableHeader<T> implements Component {

    private final DataTable.DataTableState<T> state;
    private final List<DataTableColumn<T>> columns;
    private final boolean selectable;
    private final List<TableAction<T>> tableActions;

    DataTableHeader(
        DataTable.DataTableState<T> state,
        List<DataTableColumn<T>> columns,
        boolean selectable,
        List<TableAction<T>> tableActions
    ) {
        this.state = state;
        this.columns = columns;
        this.selectable = selectable;
        this.tableActions = tableActions;
    }

    @Override
    public Node render() {
        List<Component> children = new ArrayList<>();

        // ── checkbox "selecionar tudo" (Bricks Checkbox) ──────────────────────────
        if (selectable) {
            List<T> pageItems = state.getPageItems(state.getFilteredAndSorted());
            long selectedOnPage = pageItems.stream().filter(state.selectedItems::contains).count();
            boolean allSelected = !pageItems.isEmpty() && selectedOnPage == pageItems.size();
            boolean partial = selectedOnPage > 0 && selectedOnPage < pageItems.size();

            Node cbNode = new Checkbox("")
                .checked(allSelected)
                .onChange(val -> {
                    List<T> page = state.getPageItems(state.getFilteredAndSorted());
                    if (val) state.selectedItems.addAll(page);
                    else state.selectedItems.removeAll(page);
                    state.onRefresh.run();
                })
                .render();

            if (cbNode instanceof CheckBox cb) {
                cb.setIndeterminate(partial);
                cb.setPrefWidth(32);
                cb.setMinWidth(32);
            }
            children.add(() -> cbNode);
        }

        // ── cabeçalhos de coluna (Bricks Text → Label pós-processado) ────────────
        for (DataTableColumn<T> col : columns) {
            if (!Boolean.TRUE.equals(state.columnVisible.get(col))) continue;

            String displayName =
                col == state.sortColumn
                    ? col.getName() + (state.sortAscending ? " ↑" : " ↓")
                    : col.getName();

            Node lblNode = new Text(displayName).modifier(new Modifier().bold()).render();

            if (lblNode instanceof Label lbl) {
                // Bold via Font (mais fiável que inline CSS)
                lbl.setFont(Font.font(null, FontWeight.BOLD, lbl.getFont().getSize()));
                lbl.setCursor(Cursor.HAND);
                lbl.setOnMouseClicked(e -> {
                    if (state.sortColumn == col) {
                        state.sortAscending = !state.sortAscending;
                    } else {
                        state.sortColumn = col;
                        state.sortAscending = true;
                    }
                    state.currentPage = 0;
                    state.onRefresh.run();
                });
                applyWidth(lbl, col.getWidth());
                applyAlignment(lbl, col.getAlign());
            }
            children.add(() -> lblNode);
        }

        // ── espaço reservado para colunas de ação (Bricks Spacer fixo) ───────────
        if (!tableActions.isEmpty()) {
            Node spacerNode = new Spacer(tableActions.size() * 40.0).render();
            children.add(() -> spacerNode);
        }

        Node headerNode = new Row()
            .modifier(new Modifier().padding(2, 12).margin(0, 0))
            .children(children.toArray(new Component[0]))
            .render();

        if (headerNode instanceof HBox hbox) {
            hbox.getStyleClass().add("bricks-datatable-header");
            hbox.setAlignment(Pos.CENTER_LEFT);
        }

        return headerNode;
    }

    // ── helpers ───────────────────────────────────────────────────────────────────

    private static void applyWidth(Label label, double width) {
        if (width > 0) {
            label.setPrefWidth(width);
            label.setMinWidth(width);
            label.setMaxWidth(width);
            HBox.setHgrow(label, Priority.NEVER);
        } else {
            HBox.setHgrow(label, Priority.ALWAYS);
        }
    }

    private static void applyAlignment(Label label, Align align) {
        label.setAlignment(
            switch (align) {
                case CENTER -> Pos.CENTER;
                case RIGHT -> Pos.CENTER_RIGHT;
                default -> Pos.CENTER_LEFT;
            }
        );
    }
}
