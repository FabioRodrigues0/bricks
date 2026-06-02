package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Calendario em grid com navegacao por mes.
 * Clicar no header muda para selecao de mes.
 * O dia selecionado e guardado num State&lt;LocalDate&gt;.
 *
 * <pre>{@code
 * State<LocalDate> dia = state(LocalDate.now());
 * new Calendar()
 *     .bindTo(dia)
 *     .onChange(d -> vm.carregarEventos(d))
 * }</pre>
 */
public class Calendar implements Component {

    private State<LocalDate> boundState = null;
    private Consumer<LocalDate> onChange = null;
    private Modifier modifier;
    private YearMonth mesAtual = YearMonth.now();
    private boolean modoMeses = false;
    private VBox renderedRoot;

    public Calendar bindTo(State<LocalDate> state) {
        this.boundState = state;
        if (state.get() != null) {
            this.mesAtual = YearMonth.from(state.get());
        }
        return this;
    }

    public Calendar onChange(Consumer<LocalDate> callback) {
        this.onChange = callback;
        return this;
    }

    public Calendar modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    private LocalDate getDiaAtual() {
        return boundState != null ? boundState.get() : null;
    }

    private void selecionarDia(LocalDate dia) {
        if (boundState != null) {
            boundState.set(dia);
        }
        if (onChange != null) {
            onChange.accept(dia);
        }
        refresh();
    }

    @Override
    public Node render() {
        renderedRoot = new VBox(8);
        renderedRoot.getStyleClass().add("bricks-calendar");
        renderedRoot.setPrefWidth(280);

        if (modifier != null) {
            modifier.applyTo(renderedRoot);
        }

        refresh();
        return renderedRoot;
    }

    private void refresh() {
        if (renderedRoot == null) return;

        renderedRoot.getChildren().setAll(renderHeader());
        if (modoMeses) {
            renderedRoot.getChildren().add(renderMesesGrid());
        } else {
            renderedRoot.getChildren().add(renderDiasHeader());
            renderedRoot.getChildren().add(renderDiasGrid());
        }
    }

    private Node renderHeader() {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER);

        if (!modoMeses) {
            javafx.scene.control.Button prev = calendarButton("<", "bricks-calendar-nav", () -> {
                mesAtual = mesAtual.minusMonths(1);
                triggerRefresh();
            });
            header.getChildren().add(prev);
        }

        String headerText = modoMeses
            ? String.valueOf(mesAtual.getYear())
            : mesAtual.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-PT"))
                + " " + mesAtual.getYear();

        javafx.scene.control.Button headerBtn = calendarButton(headerText, "bricks-calendar-header", () -> {
            modoMeses = !modoMeses;
            triggerRefresh();
        });
        headerBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(headerBtn, Priority.ALWAYS);
        header.getChildren().add(headerBtn);

        if (!modoMeses) {
            javafx.scene.control.Button next = calendarButton(">", "bricks-calendar-nav", () -> {
                mesAtual = mesAtual.plusMonths(1);
                triggerRefresh();
            });
            header.getChildren().add(next);
        }

        return header;
    }

    private Node renderDiasHeader() {
        HBox diasSemana = new HBox();
        diasSemana.setAlignment(Pos.CENTER);
        String[] letras = {"D", "S", "T", "Q", "Q", "S", "S"};

        for (String letra : letras) {
            Label label = (Label) new Text(letra).render();
            label.setPrefWidth(36);
            label.setAlignment(Pos.CENTER);
            label.getStyleClass().add("bricks-calendar-weekday");
            diasSemana.getChildren().add(label);
        }

        return diasSemana;
    }

    private Node renderDiasGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);

        LocalDate primeiroDia = mesAtual.atDay(1);
        int diaSemana = primeiroDia.getDayOfWeek().getValue() % 7;
        LocalDate cursor = primeiroDia.minusDays(diaSemana);

        LocalDate hoje = LocalDate.now();
        LocalDate selecionado = getDiaAtual();

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                final LocalDate dia = cursor;
                javafx.scene.control.Button btn = calendarButton(
                    String.valueOf(dia.getDayOfMonth()),
                    "bricks-calendar-day",
                    () -> {
                        mesAtual = YearMonth.from(dia);
                        selecionarDia(dia);
                    }
                );
                btn.setPrefWidth(36);
                btn.setPrefHeight(36);

                if (dia.getMonth() != mesAtual.getMonth()) {
                    btn.getStyleClass().add("bricks-calendar-day-other");
                }
                if (dia.equals(hoje)) {
                    btn.getStyleClass().add("bricks-calendar-day-today");
                }
                if (dia.equals(selecionado)) {
                    btn.getStyleClass().add("bricks-calendar-day-selected");
                }

                grid.add(btn, col, row);
                cursor = cursor.plusDays(1);
            }
        }

        return grid;
    }

    private Node renderMesesGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(8));

        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        for (int i = 0; i < 12; i++) {
            final int mesIdx = i + 1;
            javafx.scene.control.Button btn = calendarButton(meses[i], "bricks-calendar-month", () -> {
                mesAtual = YearMonth.of(mesAtual.getYear(), mesIdx);
                modoMeses = false;
                triggerRefresh();
            });
            btn.setPrefWidth(56);
            btn.setPrefHeight(36);

            if (mesIdx == mesAtual.getMonthValue()) {
                btn.getStyleClass().add("bricks-calendar-month-selected");
            }

            grid.add(btn, i % 4, i / 4);
        }

        return grid;
    }

    private javafx.scene.control.Button calendarButton(String label, String styleClass, Runnable action) {
        javafx.scene.control.Button button = (javafx.scene.control.Button) new Button(label)
            .onClick(action)
            .render();
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void triggerRefresh() {
        if (boundState != null) {
            boundState.set(boundState.get());
        }
        refresh();
    }
}
