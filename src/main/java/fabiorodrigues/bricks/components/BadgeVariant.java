package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.style.ThemeVariant;

/**
 * Variantes de cor do {@link Badge}.
 * Implementa {@link ThemeVariant} para partilhar cores com {@link NotificationType}.
 */
public enum BadgeVariant implements ThemeVariant {
    /** Cinza neutro. */
    DEFAULT,
    /** Cor primaria do tema. */
    PRIMARY,
    /** Verde. */
    SUCCESS,
    /** Amarelo. */
    WARNING,
    /** Vermelho. */
    DANGER,
    /** Azul. */
    INFO
}
