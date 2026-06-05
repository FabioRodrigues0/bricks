package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.BricksApplication;
import fabiorodrigues.bricks.core.TrayManager;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Sistema de notificacoes in-app (toast) e do sistema (OS tray).
 * As cores seguem o mesmo padrao visual do {@link Badge} via
 * {@link fabiorodrigues.bricks.style.ThemeVariant}.
 *
 * <pre>{@code
 * // Toast in-app
 * Notification.toast(app, "Guardado!", NotificationType.SUCCESS);
 *
 * // Notificacao nativa do OS
 * Notification.system(app, "Titulo", "Mensagem", NotificationType.WARNING);
 *
 * // Conforme foco da janela: toast se focada, sistema caso contrario
 * Notification.notify(app, "Titulo", "Mensagem", NotificationType.INFO);
 * }</pre>
 */
public class Notification {

    private static final int DURACAO_DEFAULT = 3000;
    private static final String OVERLAY_ID = "bricks-overlay";

    private Notification() {}

    /**
     * Mostra um toast no canto inferior direito da janela.
     * Desaparece automaticamente apos 3 segundos.
     *
     * @param app      {@link BricksApplication} — a aplicacao
     * @param mensagem {@code String} — texto a mostrar
     * @param tipo     {@link NotificationType} — tipo (define cor da barra lateral)
     */
    public static void toast(BricksApplication app, String mensagem,
                              NotificationType tipo) {
        toast(app, mensagem, tipo, DURACAO_DEFAULT);
    }

    /**
     * Mostra um toast com duracao personalizada em milissegundos.
     *
     * @param app       {@link BricksApplication} — a aplicacao
     * @param mensagem  {@code String} — texto a mostrar
     * @param tipo      {@link NotificationType} — tipo (define cor da barra lateral)
     * @param duracaoMs {@code int} — duracao em milissegundos
     */
    public static void toast(BricksApplication app, String mensagem,
                              NotificationType tipo, int duracaoMs) {
        Platform.runLater(() -> {
            HBox toast = new HBox(10);
            toast.setAlignment(Pos.CENTER_LEFT);
            toast.setPadding(new Insets(12, 16, 12, 16));
            toast.setMaxWidth(320);
            toast.getStyleClass().add("bricks-toast");
            toast.getStyleClass().add("bricks-toast-" + tipo.name().toLowerCase());
            toast.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            );

            Rectangle barra = new Rectangle(4, 40);
            barra.setFill(Color.web(tipo.getColorHex()));
            barra.setArcWidth(4);
            barra.setArcHeight(4);

            Label label = new Label(mensagem);
            label.setWrapText(true);
            label.setMaxWidth(260);
            label.getStyleClass().add("bricks-toast-label");
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: #1f2937;");

            toast.getChildren().addAll(barra, label);

            StackPane overlay = getOrCreateOverlay(app);
            StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(toast, new Insets(0, 16, 16, 0));
            overlay.getChildren().add(toast);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition pausa = new PauseTransition(Duration.millis(duracaoMs));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> overlay.getChildren().remove(toast));

            new SequentialTransition(fadeIn, pausa, fadeOut).play();
        });
    }

    /**
     * Dispara uma notificacao nativa do sistema operativo via tray icon.
     * Requer que o tray esteja inicializado (chamar {@code setTrayIcon()} no App).
     *
     * @param app      {@link BricksApplication} — a aplicacao
     * @param titulo   {@code String} — titulo da notificacao
     * @param mensagem {@code String} — corpo da mensagem
     * @param tipo     {@link NotificationType} — tipo (mapeia para icone nativo)
     */
    public static void system(BricksApplication app, String titulo,
                               String mensagem, NotificationType tipo) {
        TrayManager.getInstance().showNotification(titulo, mensagem, tipo);
    }

    /**
     * Mostra toast se a janela estiver em foco, notificacao do sistema caso contrario.
     *
     * @param app      {@link BricksApplication} — a aplicacao
     * @param titulo   {@code String} — titulo (usado na notificacao do sistema)
     * @param mensagem {@code String} — corpo da mensagem
     * @param tipo     {@link NotificationType} — tipo
     */
    public static void notify(BricksApplication app, String titulo,
                               String mensagem, NotificationType tipo) {
        if (app.getStage() != null && app.getStage().isFocused()) {
            toast(app, mensagem, tipo);
        } else {
            system(app, titulo, mensagem, tipo);
        }
    }

    /**
     * Devolve (ou cria) um {@link StackPane} overlay que envolve o root da scene.
     * O overlay vive FORA do container interno que e limpo a cada re-render,
     * por isso os toasts persistem durante a sua animacao.
     */
    private static StackPane getOrCreateOverlay(BricksApplication app) {
        Scene scene = app.getStage().getScene();
        Node root = scene.getRoot();
        if (root instanceof StackPane sp && OVERLAY_ID.equals(sp.getId())) {
            return sp;
        }
        Parent oldRoot = scene.getRoot();
        StackPane overlay = new StackPane();
        overlay.setId(OVERLAY_ID);
        scene.setRoot(overlay);
        overlay.getChildren().add(oldRoot);
        return overlay;
    }
}
