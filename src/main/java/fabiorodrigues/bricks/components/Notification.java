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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

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
    private static final String TOAST_HOST_ID = "bricks-toast-host";

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
        toast(app, null, mensagem, tipo, duracaoMs);
    }

    /**
     * Mostra um toast com titulo opcional e duracao personalizada.
     *
     * @param app       {@link BricksApplication} — a aplicacao
     * @param titulo    {@code String} — titulo opcional
     * @param mensagem  {@code String} — texto a mostrar
     * @param tipo      {@link NotificationType} — tipo visual
     * @param duracaoMs {@code int} — duracao em milissegundos
     */
    public static void toast(BricksApplication app, String titulo, String mensagem,
                              NotificationType tipo, int duracaoMs) {
        Platform.runLater(() -> {
            String cor = tipo.getColorHex();
            HBox toast = new HBox(10);
            toast.setAlignment(Pos.TOP_LEFT);
            toast.setPadding(new Insets(12, 14, 12, 0));
            toast.setPrefWidth(360);
            toast.setMaxWidth(Region.USE_PREF_SIZE);
            toast.setMaxHeight(Region.USE_PREF_SIZE);
            toast.getStyleClass().add("bricks-toast");
            toast.getStyleClass().add("bricks-toast-" + tipo.name().toLowerCase());
            toast.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 6;"
                + "-fx-border-color: #e5e7eb;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 6;"
                + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 16, 0, 0, 4);"
            );

            Rectangle barra = new Rectangle(4, 48);
            barra.setFill(Color.web(cor));
            barra.setArcWidth(6);
            barra.setArcHeight(6);

            FontIcon icon = new FontIcon(iconFor(tipo));
            icon.setIconSize(18);
            icon.setIconColor(Color.web(cor));

            VBox textBox = new VBox(3);
            textBox.setAlignment(Pos.CENTER_LEFT);
            textBox.setMaxWidth(260);

            if (!isBlank(titulo)) {
                Label titleLabel = new Label(titulo);
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(260);
                titleLabel.getStyleClass().add("bricks-toast-title");
                titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #111827;");
                textBox.getChildren().add(titleLabel);
            }

            Label label = new Label(mensagem);
            label.setWrapText(true);
            label.setMaxWidth(260);
            label.getStyleClass().add("bricks-toast-label");
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: #4b5563;");
            textBox.getChildren().add(label);

            Label close = new Label("x");
            close.setMinSize(22, 22);
            close.setAlignment(Pos.CENTER);
            close.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-cursor: hand;");

            HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);
            toast.getChildren().addAll(barra, icon, textBox, close);

            VBox host = getOrCreateToastHost(app);
            host.getChildren().add(toast);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition pausa = new PauseTransition(Duration.millis(duracaoMs));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> host.getChildren().remove(toast));

            SequentialTransition transition = new SequentialTransition(fadeIn, pausa, fadeOut);
            close.setOnMouseClicked(e -> {
                transition.stop();
                host.getChildren().remove(toast);
            });
            transition.play();
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
            toast(app, titulo, mensagem, tipo, DURACAO_DEFAULT);
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

    private static VBox getOrCreateToastHost(BricksApplication app) {
        StackPane overlay = getOrCreateOverlay(app);
        for (Node child : overlay.getChildren()) {
            if (child instanceof VBox host && TOAST_HOST_ID.equals(host.getId())) {
                return host;
            }
        }

        VBox host = new VBox(10);
        host.setId(TOAST_HOST_ID);
        host.setFillWidth(false);
        host.setMouseTransparent(false);
        host.setMaxWidth(Region.USE_PREF_SIZE);
        host.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(host, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(host, new Insets(0, 16, 16, 0));
        overlay.getChildren().add(host);
        return host;
    }

    private static String iconFor(NotificationType tipo) {
        return switch (tipo) {
            case SUCCESS -> "fas-check-circle";
            case WARNING -> "fas-exclamation-triangle";
            case ERROR -> "fas-times-circle";
            case INFO -> "fas-info-circle";
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
