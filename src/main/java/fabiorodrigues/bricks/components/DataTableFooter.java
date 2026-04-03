package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Barra de paginação do {@link DataTable}: contagem de resultados e botões anterior/seguinte.
 * Classe de pacote — não faz parte da API pública.
 */
class DataTableFooter<T> implements Component {

    private final DataTable.DataTableState<T> state;
    private final int pageSize;

    DataTableFooter(DataTable.DataTableState<T> state, int pageSize) {
        this.state = state;
        this.pageSize = pageSize;
    }

    @Override
    public Node render() {
        List<T> all = state.getFilteredAndSorted();
        int total = all.size();
        int totalPages = state.totalPages(all);

        String paginationText;
        if (total == 0) {
            paginationText = "Sem resultados";
        } else {
            int from = state.currentPage * pageSize + 1;
            int to = Math.min((state.currentPage + 1) * pageSize, total);
            paginationText = from + "–" + to + " de " + total;
        }

        boolean canGoPrev = state.currentPage > 0;
        boolean canGoNext = state.currentPage < totalPages - 1 && total > 0;

        Node footerNode = new Row()
                .padding(8)
                .gap(8)
                .children(
                        new Text(paginationText).modifier(new Modifier().fontSize(13)),
                        new Spacer(),
                        new Button("‹")
                                .enabled(canGoPrev)
                                .onClick(() -> {
                                    state.currentPage--;
                                    state.onRefresh.run();
                                }),
                        new Button("›")
                                .enabled(canGoNext)
                                .onClick(() -> {
                                    state.currentPage++;
                                    state.onRefresh.run();
                                })
                )
                .render();

        if (footerNode instanceof HBox hbox) {
            hbox.setStyle("-fx-border-color: rgba(0,0,0,0.12) transparent transparent transparent;" +
                          "-fx-border-width: 1 0 0 0;");
        }

        return footerNode;
    }
}
