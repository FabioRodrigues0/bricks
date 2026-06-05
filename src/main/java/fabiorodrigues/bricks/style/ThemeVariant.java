package fabiorodrigues.bricks.style;

/**
 * Interface base para variantes de tema visual.
 * Implementada por {@link fabiorodrigues.bricks.components.BadgeVariant}
 * e {@link fabiorodrigues.bricks.components.NotificationType}.
 * Garante consistencia visual entre componentes que usam as mesmas cores.
 */
public interface ThemeVariant {

    /**
     * Nome do valor do enum — disponivel automaticamente em todos os enums.
     *
     * @return {@code String} — nome do valor (ex: "SUCCESS", "WARNING")
     */
    String name();

    /**
     * Devolve a classe CSS correspondente a esta variante.
     * Segue o padrao {@code bricks-variant-{nome_lowercase}}.
     *
     * @return {@code String} — nome da classe CSS
     */
    default String getCssClass() {
        return "bricks-variant-" + name().toLowerCase();
    }

    /**
     * Devolve a cor hex correspondente a esta variante.
     * Usado para componentes que precisam de cor diretamente (ex: tray icon, toast bar).
     *
     * @return {@code String} — cor no formato hex (ex: "#16a34a")
     */
    default String getColorHex() {
        return switch (name()) {
            case "SUCCESS"          -> "#16a34a";
            case "WARNING"          -> "#d97706";
            case "DANGER", "ERROR"  -> "#dc2626";
            case "INFO"             -> "#2563eb";
            case "PRIMARY"          -> "#3b82f6";
            default                 -> "#6b7280";
        };
    }
}
