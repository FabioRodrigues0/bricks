package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Calendario com navegacao por mes, fontes de eventos e painel opcional do dia.
 *
 * <pre>{@code
 * new Calendar()
 *     .bindTo(diaAtual)
 *     .addSource(new EventSource<>(eventos).dateBy(Evento::data))
 *     .informacaoDia()
 * }</pre>
 */
public class Calendar implements Component {

    private State<LocalDate> boundState = null;
    private final List<EventSource<?>> sources = new ArrayList<>();
    private boolean showInfoPanel = false;
    private Function<List<Object>, Component> customInfoPanel = null;
    private Modifier modifier;
    private Consumer<LocalDate> onChange = null;

    private YearMonth mesAtual = YearMonth.now();
    private boolean modoMeses = false;
    private Node renderedRoot;

    public Calendar bindTo(State<LocalDate> state) {
        this.boundState = state;
        if (state.get() != null) {
            this.mesAtual = YearMonth.from(state.get());
        }
        return this;
    }

    public Calendar addSource(EventSource<?> source) {
        if (source != null) {
            this.sources.add(source);
        }
        return this;
    }

    public Calendar informacaoDia() {
        this.showInfoPanel = true;
        return this;
    }

    public Calendar informacaoDia(Function<List<Object>, Component> panel) {
        this.showInfoPanel = true;
        this.customInfoPanel = panel;
        return this;
    }

    /**
     * Mantido por compatibilidade com a API anterior.
     */
    public Calendar onChange(Consumer<LocalDate> callback) {
        this.onChange = callback;
        return this;
    }

    public Calendar modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        if (boundState != null && boundState.get() == null) {
            boundState.set(LocalDate.now());
        }

        LocalDate diaAtual = boundState != null ? boundState.get() : LocalDate.now();
        if (mesAtual == null) {
            mesAtual = YearMonth.from(diaAtual);
        }

        Node calendarNode = buildCalendarGrid(diaAtual);

        if (showInfoPanel) {
            HBox layout = (HBox) new Row()
                .gap(16)
                .modifier(new Modifier().alignment(Pos.TOP_LEFT))
                .children(
                    () -> calendarNode,
                    new When(showInfoPanel).then(() -> buildInfoPanel(diaAtual))
                )
                .render();

            if (modifier != null) {
                modifier.applyTo(layout);
            }
            renderedRoot = layout;
            return layout;
        }

        if (modifier != null && calendarNode instanceof javafx.scene.layout.Region region) {
            modifier.applyTo(region);
        }
        renderedRoot = calendarNode;
        return calendarNode;
    }

    private Node buildCalendarGrid(LocalDate diaAtual) {
        VBox cal = (VBox) new Column()
            .gap(8)
            .padding(12)
            .modifier(new Modifier().width(300))
            .styleClass("bricks-calendar")
            .render();

        cal.getChildren().add(buildHeader());

        if (modoMeses) {
            cal.getChildren().add(buildMesesGrid());
        } else {
            cal.getChildren().add(buildDiasSemanaHeader());
            cal.getChildren().add(buildDiasGrid(diaAtual));
        }

        return cal;
    }

    private Node buildHeader() {
        HBox header = (HBox) new Row()
            .gap(8)
            .modifier(new Modifier().alignment(Pos.CENTER))
            .render();

        if (!modoMeses) {
            javafx.scene.control.Button prev = calendarIconButton("fas-chevron-left", "Mes anterior", () -> {
                mesAtual = mesAtual.minusMonths(1);
                triggerRefresh();
            });
            header.getChildren().add(prev);
        }

        String headerText = modoMeses
            ? String.valueOf(mesAtual.getYear())
            : mesAtual.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-PT"))
                + " " + mesAtual.getYear();

        javafx.scene.control.Button headerBtn = calendarButton(
            headerText,
            () -> {
                modoMeses = !modoMeses;
                triggerRefresh();
            },
            new Modifier().fillMaxWidth(),
            "bricks-calendar-header-btn"
        );
        header.getChildren().add(headerBtn);

        if (!modoMeses) {
            javafx.scene.control.Button next = calendarIconButton("fas-chevron-right", "Proximo mes", () -> {
                mesAtual = mesAtual.plusMonths(1);
                triggerRefresh();
            });
            header.getChildren().add(next);
        }

        return header;
    }

    private Node buildDiasSemanaHeader() {
        HBox diasSemana = (HBox) new Row()
            .modifier(new Modifier().alignment(Pos.CENTER))
            .render();
        String[] dias = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};

        for (String dia : dias) {
            Node label = new Text(dia)
                .modifier(new Modifier().width(38))
                .alignment(Pos.CENTER)
                .styleClass("bricks-calendar-weekday")
                .render();
            diasSemana.getChildren().add(label);
        }

        return diasSemana;
    }

    private Node buildDiasGrid(LocalDate diaAtual) {
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);

        LocalDate primeiroDia = mesAtual.atDay(1);
        LocalDate cursor = primeiroDia.minusDays(primeiroDia.getDayOfWeek().getValue() - 1L);
        LocalDate hoje = LocalDate.now();

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                final LocalDate dia = cursor;
                boolean selected = dia.equals(diaAtual);
                javafx.scene.control.Button celula = calendarButton(
                    "",
                    () -> selecionarDia(dia),
                    new Modifier().size(38, 42),
                    dayStyleClasses(dia, hoje, selected)
                );

                VBox conteudo = (VBox) new Column()
                    .gap(2)
                    .modifier(new Modifier().alignment(Pos.CENTER))
                    .render();

                Text numeroText = new Text(String.valueOf(dia.getDayOfMonth()))
                    .alignment(Pos.CENTER)
                    .styleClass("bricks-calendar-day-number")
                    .styleClass(selected ? "bricks-calendar-day-number-selected" : "");
                Node numero = numeroText.render();
                conteudo.getChildren().add(numero);

                List<Color> cores = highlightColorsForDay(dia);
                Node dots = new When(!cores.isEmpty()).then(() -> buildHighlightDots(cores)).render();
                if (dots != null) {
                    conteudo.getChildren().add(dots);
                }

                celula.setGraphic(conteudo);

                grid.add(celula, col, row);
                cursor = cursor.plusDays(1);
            }
        }

        return grid;
    }

    private Node buildMesesGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(8));

        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        for (int i = 0; i < 12; i++) {
            final int mesIdx = i + 1;
            javafx.scene.control.Button btn = calendarButton(
                meses[i],
                () -> {
                    mesAtual = YearMonth.of(mesAtual.getYear(), mesIdx);
                    modoMeses = false;
                    triggerRefresh();
                },
                new Modifier().size(60, 36),
                mesIdx == mesAtual.getMonthValue()
                    ? new String[] {"bricks-calendar-month-btn", "bricks-calendar-month-selected"}
                    : new String[] {"bricks-calendar-month-btn"}
            );

            grid.add(btn, i % 4, i / 4);
        }

        return grid;
    }

    private Node buildInfoPanel(LocalDate dia) {
        List<Object> todosEventos = new ArrayList<>();
        for (EventSource<?> source : sources) {
            todosEventos.addAll(source.getEventsForDay(dia));
        }

        if (customInfoPanel != null) {
            Component panel = customInfoPanel.apply(todosEventos);
            VBox infoCard = (VBox) new Column()
                .gap(12)
                .styleClass("bricks-calendar-info-panel")
                .render();
            Node customNode = panel != null ? panel.render() : null;
            if (customNode != null) {
                infoCard.getChildren().add(customNode);
            }
            return scrollPanel(infoCard);
        }

        VBox panel = (VBox) new Column()
            .gap(12)
            .styleClass("bricks-calendar-info-panel")
            .render();

        String[] mesesPt = {"Janeiro", "Fevereiro", "Mar\u00e7o", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        String dataFormatada = dia.getDayOfMonth() + " de "
            + mesesPt[dia.getMonthValue() - 1] + " de " + dia.getYear();

        Node titulo = new Row()
            .gap(8)
            .modifier(new Modifier().alignment(Pos.CENTER_LEFT))
            .children(
                new Icon("far-calendar-alt").size(15),
                new Text(dataFormatada).fontSize(15).bold()
            )
            .render();
        panel.getChildren().add(titulo);
        panel.getChildren().add(new Divider().render());

        if (todosEventos.isEmpty()) {
            panel.getChildren().add(new Text("Sem eventos neste dia.").render());
        } else {
            for (EventSource<?> source : sources) {
                List<?> eventosDaSource = source.getEventsForDay(dia);
                for (Object evento : eventosDaSource) {
                    VBox eventoBox = (VBox) new Column()
                        .gap(2)
                        .styleClass("bricks-calendar-event-row")
                        .render();

                    List<Function<Object, String>> extractors = labelExtractors(source);
                    if (!extractors.isEmpty()) {
                        for (Function<Object, String> extractor : extractors) {
                            String value = extractor.apply(evento);
                            if (value != null) {
                                eventoBox.getChildren().add(new Text(value).fontSize(13).render());
                            }
                        }
                    } else {
                        eventoBox.getChildren().add(new Text(String.valueOf(evento)).fontSize(13).render());
                    }

                    panel.getChildren().add(eventoBox);
                }
            }
        }

        return scrollPanel(panel);
    }

    private Node scrollPanel(VBox panel) {
        ScrollPane scroll = (ScrollPane) new ScrollView(() -> panel)
            .vertical()
            .modifier(new Modifier().fillMaxWidth().fillMaxHeight())
            .styleClass("bricks-calendar-info-scroll")
            .render();
        return scroll;
    }

    private Node buildHighlightDots(List<Color> cores) {
        HBox dots = (HBox) new Row()
            .gap(2)
            .modifier(new Modifier().alignment(Pos.CENTER))
            .render();
        for (int d = 0; d < Math.min(cores.size(), 3); d++) {
            dots.getChildren().add(new Circle(3, cores.get(d)));
        }
        return dots;
    }

    private List<Color> highlightColorsForDay(LocalDate dia) {
        List<Color> cores = new ArrayList<>();
        for (EventSource<?> source : sources) {
            Color cor = source.getHighlightColor(dia);
            if (cor != null && !cores.contains(cor)) {
                cores.add(cor);
            }
        }
        return cores;
    }

    @SuppressWarnings("unchecked")
    private List<Function<Object, String>> labelExtractors(EventSource<?> source) {
        return (List<Function<Object, String>>) (List<?>) source.getLabelExtractors();
    }

    private void selecionarDia(LocalDate dia) {
        mesAtual = YearMonth.from(dia);
        if (boundState != null) {
            boundState.set(dia);
        }
        if (onChange != null) {
            onChange.accept(dia);
        }
        refreshRenderedRoot();
    }

    private javafx.scene.control.Button calendarButton(String label, String styleClass, Runnable action) {
        return calendarButton(label, action, null, styleClass);
    }

    private javafx.scene.control.Button calendarButton(
        String label,
        Runnable action,
        Modifier buttonModifier,
        String... styleClasses
    ) {
        Button bricksButton = new Button(label)
            .onClick(action)
            .styleClass(styleClasses);
        if (buttonModifier != null) {
            bricksButton.modifier(buttonModifier);
        }
        javafx.scene.control.Button button = (javafx.scene.control.Button) bricksButton.render();
        return button;
    }

    private String[] dayStyleClasses(LocalDate dia, LocalDate hoje, boolean selected) {
        List<String> classes = new ArrayList<>();
        classes.add("bricks-calendar-day");
        if (dia.getMonth() != mesAtual.getMonth()) {
            classes.add("bricks-calendar-day-other");
        }
        if (dia.equals(hoje)) {
            classes.add("bricks-calendar-day-today");
        }
        if (selected) {
            classes.add("bricks-calendar-day-selected");
        }
        return classes.toArray(String[]::new);
    }

    private javafx.scene.control.Button calendarIconButton(String icon, String tooltip, Runnable action) {
        javafx.scene.control.Button button = (javafx.scene.control.Button) new IconButton(icon)
            .ghost()
            .tooltip(tooltip)
            .onClick(action)
            .styleClass("bricks-calendar-nav")
            .render();
        return button;
    }

    private void triggerRefresh() {
        if (boundState != null) {
            boundState.set(boundState.get());
        }
        refreshRenderedRoot();
    }

    private void refreshRenderedRoot() {
        if (renderedRoot == null) return;

        Node oldRoot = renderedRoot;
        Node replacement = render();
        if (oldRoot instanceof HBox oldLayout && replacement instanceof HBox newLayout) {
            oldLayout.getChildren().setAll(newLayout.getChildren());
            renderedRoot = oldRoot;
        } else if (oldRoot instanceof VBox oldBox && replacement instanceof VBox newBox) {
            oldBox.getChildren().setAll(newBox.getChildren());
            renderedRoot = oldRoot;
        }
    }
}
