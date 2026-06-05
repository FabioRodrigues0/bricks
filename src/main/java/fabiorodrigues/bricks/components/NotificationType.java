package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.style.ThemeVariant;

/**
 * Tipos de notificacao — in-app toast e notificacoes do sistema.
 * Implementa {@link ThemeVariant} para partilhar cores com {@link BadgeVariant}.
 *
 * <pre>{@code
 * Notification.toast(app, "Guardado!", NotificationType.SUCCESS)
 * Notification.system(app, "IUC a vencer", "5 dias", NotificationType.WARNING)
 * }</pre>
 */
public enum NotificationType implements ThemeVariant {
    /** Verde — operacao bem sucedida. */
    SUCCESS,
    /** Amarelo — aviso. */
    WARNING,
    /** Vermelho — erro. */
    ERROR,
    /** Azul — informativo. */
    INFO
}
