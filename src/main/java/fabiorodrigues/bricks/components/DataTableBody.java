package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

/**
 * Área de dados do {@link DataTable}: linhas paginadas com seleção, células e botões de ação.
 * Classe de pacote — não faz parte da API pública.
 */
class DataTableBody<T> implements Component {

    private final DataTable.DataTableState<T> state;
    private final List<DataTableColumn<T>> columns;
    private final boolean selectable;
    private final List<TableAction<T>> tableActions;

    DataTableBody(DataTable.DataTableState<T> state, List<DataTableColumn<T>> columns,
                  boolean selectable, List<TableAction<T>> tableActions) {
        this.state = state;
        this.columns = columns;
        this.selectable = selectable;
        this.tableActions = tableActions;
    }

    @Override
    public Node render() {
        List<T> pageItems = state.getPageItems(state.getFilteredAndSorted());

        // ── estado vazio ──────────────────────────────────────────────────────────
        if (pageItems.isEmpty()) {
            return new Column().children(
                new Text("Sem resultados.")
                        .modifier(new Modifier().textColor(Color.web("#888")).padding(20, 12))
            ).render();
        }

        // ── linha de cor base para seleção ────────────────────────────────────────
        String selectedBg = toHex(BricksTheme.current().colorScheme().primaryContainer());

        List<Component> rowComponents = new ArrayList<>();

        for (int i = 0; i < pageItems.size(); i++) {
            final T item = pageItems.get(i);
            final boolean isEven = i % 2 == 0;
            final String normalBg = isEven ? "transparent" : "rgba(0,0,0,0.03)";

            List<Component> cellComponents = new ArrayList<>();

            // ── checkbox de seleção (Bricks Checkbox) ────────────────────────────
            if (selectable) {
                Node cbNode = new Checkbox("")
                        .checked(state.selectedItems.contains(item))
                        .onChange(val -> {
                            if (val) state.selectedItems.add(item);
                            else     state.selectedItems.remove(item);
                            state.onRefresh.run();
                        })
                        .render();
                if (cbNode instanceof CheckBox cb) {
                    cb.setPrefWidth(32);
                    cb.setMinWidth(32);
                }
                cellComponents.add(() -> cbNode);
            }

            // ── células de dados (Bricks Text → Label pós-processado) ─────────────
            for (DataTableColumn<T> col : columns) {
                if (!Boolean.TRUE.equals(state.columnVisible.get(col))) continue;

                String value = "";
                try {
                    String v = col.getValueFunction().apply(item);
                    if (v != null) value = v;
                } catch (Exception ignored) {}

                Node cellNode = new Text(value).render();
                if (cellNode instanceof Label lbl) {
                    if (col.isBold()) {
                        lbl.setFont(Font.font(null, FontWeight.BOLD, lbl.getFont().getSize()));
                    }
                    lbl.setWrapText(col.isWrapText());
                    applyWidth(lbl, col.getWidth());
                    applyAlignment(lbl, col.getAlign());
                }
                cellComponents.add(() -> cellNode);
            }

            // ── botões de ação 32×32 (JavaFX Button — sem equivalente Bricks) ────
            if (!tableActions.isEmpty()) {
                List<Component> actionBtns = new ArrayList<>();
                for (TableAction<T> action : tableActions) {
                    javafx.scene.control.Button actionBtn = new javafx.scene.control.Button();
                    actionBtn.setPrefSize(32, 32);
                    actionBtn.setMinSize(32, 32);
                    actionBtn.setMaxSize(32, 32);
                    actionBtn.setCursor(Cursor.HAND);

                    // Ícone via Bricks Icon
                    if (action.getIcon() != null) {
                        Node iconNode = new Icon(action.getIcon())
                                .size(14)
                                .color(action.isDanger() ? Color.web("#d32f2f") : null)
                                .render();
                        actionBtn.setGraphic(iconNode);
                    }

                    if (action.getTooltip() != null) {
                        actionBtn.setTooltip(new Tooltip(action.getTooltip()));
                    }

                    String baseStyle = "-fx-background-radius: 4; -fx-padding: 0; " +
                                       "-fx-background-color: transparent; -fx-border-color: transparent;";
                    String hoverStyle = action.isDanger()
                            ? "-fx-background-radius: 4; -fx-padding: 0; " +
                              "-fx-background-color: #ffebee; -fx-border-color: transparent;"
                            : "-fx-background-radius: 4; -fx-padding: 0; " +
                              "-fx-background-color: rgba(0,0,0,0.08); -fx-border-color: transparent;";
                    actionBtn.setStyle(baseStyle);
                    actionBtn.setOnMouseEntered(e -> actionBtn.setStyle(hoverStyle));
                    actionBtn.setOnMouseExited(e -> actionBtn.setStyle(baseStyle));

                    if (action.getOnClick() != null) {
                        actionBtn.setOnAction(e -> action.getOnClick().accept(item));
                    }

                    actionBtns.add(() -> actionBtn);
                }

                double actionWidth = tableActions.size() * 40.0;
                Node actionsNode = new Row()
                        .gap(4)
                        .modifier(new Modifier().width(actionWidth))
                        .children(actionBtns.toArray(new Component[0]))
                        .render();
                if (actionsNode instanceof HBox hbox) {
                    hbox.setAlignment(Pos.CENTER);
                    hbox.setMinWidth(actionWidth);
                }
                cellComponents.add(() -> actionsNode);
            }

            // ── linha (Bricks Row → HBox pós-processado para estilo e eventos) ────
            Node rowNode = new Row()
                    .modifier(new Modifier().padding(6, 12))
                    .children(cellComponents.toArray(new Component[0]))
                    .render();

            if (rowNode instanceof HBox hbox) {
                hbox.setAlignment(Pos.CENTER_LEFT);
                boolean isSelected = state.selectedItems.contains(item);
                hbox.setStyle("-fx-background-color: " + (isSelected ? selectedBg : normalBg) + ";");

                hbox.setOnMouseEntered(e -> {
                    if (!state.selectedItems.contains(item)) {
                        hbox.setStyle("-fx-background-color: rgba(0,0,0,0.06);");
                    }
                });
                hbox.setOnMouseExited(e -> {
                    boolean sel = state.selectedItems.contains(item);
                    hbox.setStyle("-fx-background-color: " + (sel ? selectedBg : normalBg) + ";");
                });

                // Clique na linha fora de checkbox/button também seleciona
                if (selectable) {
                    hbox.setCursor(Cursor.HAND);
                    hbox.setOnMouseClicked(e -> {
                        // Percorre ascendentes para detetar checkbox ou button
                        Node target = (Node) e.getTarget();
                        while (target != null && target != hbox) {
                            if (target instanceof CheckBox || target instanceof javafx.scene.control.Button) return;
                            target = target.getParent();
                        }
                        if (state.selectedItems.contains(item)) state.selectedItems.remove(item);
                        else state.selectedItems.add(item);
                        state.onRefresh.run();
                    });
                }
            }

            rowComponents.add(() -> rowNode);
        }

        return new Column()
                .children(rowComponents.toArray(new Component[0]))
                .render();
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
        label.setAlignment(switch (align) {
            case CENTER -> Pos.CENTER;
            case RIGHT  -> Pos.CENTER_RIGHT;
            default     -> Pos.CENTER_LEFT;
        });
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255));
    }
}
