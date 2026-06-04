package fabiorodrigues.bricks.components;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import fabiorodrigues.bricks.core.Component;
import javafx.scene.paint.Color;

/**
 * Fonte de eventos para o Calendar. Tipada com o tipo do evento.
 * Permite multiplas fontes de tipos diferentes no mesmo Calendar.
 *
 * <pre>{@code
 * new EventSource<>(vm.veiculos.get())
 *     .dateBy(Veiculo::getDataInspeccao)
 *     .highlightRules(List.of(
 *         new CalendarHighlight(Color.RED,    0),
 *         new CalendarHighlight(Color.ORANGE, 10),
 *         new CalendarHighlight(Color.GREEN,  30)
 *     ))
 * }</pre>
 */
public class EventSource<T> {

    private final List<T> items;
    private Function<T, LocalDate> dateExtractor;
    private List<CalendarHighlight> highlightRules = new ArrayList<>();
    private List<Function<T, String>> labelExtractors = new ArrayList<>();
    private Function<T, Component> componentRenderer;

    public EventSource(List<T> items) {
        this.items = items != null ? items : List.of();
    }

    /**
     * Define como extrair a data de cada evento.
     * Usado para filtrar eventos por dia e calcular highlights.
     */
    public EventSource<T> dateBy(Function<T, LocalDate> extractor) {
        this.dateExtractor = extractor;
        return this;
    }

    /**
     * Define as regras de highlight por ordem de prioridade (mais urgente primeiro).
     * A primeira regra cujo threshold &gt;= dias restantes e aplicada.
     */
    public EventSource<T> highlightRules(List<CalendarHighlight> rules) {
        this.highlightRules = rules != null ? rules : List.of();
        return this;
    }

    /**
     * Define quais campos mostrar no painel default do informacaoDia.
     * Cada Function extrai um campo como String, mostrado numa linha.
     *
     * <pre>{@code
     * .labels(ItemCalendario::getTitulo, ItemCalendario::getCategoria)
     * }</pre>
     */
    @SafeVarargs
    public final EventSource<T> labels(Function<T, String>... extractors) {
        this.labelExtractors = extractors != null ? List.of(extractors) : List.of();
        return this;
    }

    /**
     * Define como renderizar cada evento no painel default do informacaoDia.
     *
     * <pre>{@code
     * .component(evento -> new Text(evento.getName()))
     * }</pre>
     */
    public EventSource<T> component(Function<T, Component> renderer) {
        this.componentRenderer = renderer;
        return this;
    }

    public List<T> getItems() {
        return items;
    }

    public Function<T, LocalDate> getDateExtractor() {
        return dateExtractor;
    }

    public List<CalendarHighlight> getHighlightRules() {
        return highlightRules;
    }

    public List<Function<T, String>> getLabelExtractors() {
        return labelExtractors;
    }

    public Function<T, Component> getComponentRenderer() {
        return componentRenderer;
    }

    /**
     * Devolve os eventos para o dia dado.
     */
    public List<T> getEventsForDay(LocalDate day) {
        if (dateExtractor == null || day == null) return List.of();
        return items.stream()
            .filter(item -> {
                LocalDate d = dateExtractor.apply(item);
                return d != null && d.equals(day);
            })
            .toList();
    }

    /**
     * Devolve a cor de highlight para um dado dia, ou null se nao houver.
     */
    public Color getHighlightColor(LocalDate day) {
        if (dateExtractor == null || highlightRules.isEmpty() || day == null) return null;

        return items.stream()
            .filter(item -> {
                LocalDate d = dateExtractor.apply(item);
                return d != null && d.equals(day);
            })
            .map(item -> {
                LocalDate date = dateExtractor.apply(item);
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), date);
                for (CalendarHighlight rule : highlightRules) {
                    if (diasRestantes <= rule.getDaysThreshold()) {
                        return rule.getColor();
                    }
                }
                return null;
            })
            .filter(color -> color != null)
            .findFirst()
            .orElse(null);
    }
}
