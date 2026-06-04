package fabiorodrigues.bricks.components;

import javafx.scene.paint.Color;

/**
 * Regra de cor para highlight de dias no Calendar.
 * A cor aplica-se quando os dias restantes sao &lt;= ao threshold.
 *
 * <pre>{@code
 * List.of(
 *     new CalendarHighlight(Color.RED,    0),   // expirado ou hoje
 *     new CalendarHighlight(Color.ORANGE, 10),  // <= 10 dias
 *     new CalendarHighlight(Color.GREEN,  30)   // <= 30 dias
 * )
 * }</pre>
 */
public class CalendarHighlight {
    private final Color color;
    private final long daysThreshold;

    public CalendarHighlight(Color color, long daysThreshold) {
        this.color = color;
        this.daysThreshold = daysThreshold;
    }

    public Color getColor() {
        return color;
    }

    public long getDaysThreshold() {
        return daysThreshold;
    }
}
