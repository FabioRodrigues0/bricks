package fabiorodrigues.bricks.core;

import fabiorodrigues.bricks.components.NotificationType;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

/**
 * Gere o icone na system tray e as notificacoes nativas do OS via AWT.
 * Singleton — inicializado pelo {@link BricksApplication} quando
 * {@code setTrayIcon()} e chamado.
 *
 * <p>Em ambientes onde {@link SystemTray#isSupported()} devolve {@code false}
 * (ex: alguns sistemas Linux headless), as operacoes sao no-op silenciosas.</p>
 */
public class TrayManager {

    private static TrayManager instance;
    private TrayIcon trayIcon;
    private Stage stage;

    private TrayManager() {}

    /**
     * Devolve a instancia unica do {@code TrayManager}.
     *
     * @return o singleton
     */
    public static TrayManager getInstance() {
        if (instance == null) instance = new TrayManager();
        return instance;
    }

    /**
     * Inicializa o tray icon.
     * Chamado pelo {@link BricksApplication} quando {@code setTrayIcon()} e usado.
     *
     * @param stage    {@link Stage} — a janela principal da app
     * @param iconPath {@code String} — caminho do icone no classpath (ex: "/logo.png")
     * @param tooltip  {@code String} — texto ao passar o rato no icone
     * @param appTitle {@code String} — titulo da app para o menu de contexto
     */
    public void init(Stage stage, String iconPath, String tooltip, String appTitle) {
        init(stage, iconPath, tooltip, appTitle, null, null);
    }

    /**
     * Inicializa o tray icon com labels personalizados para o menu nativo.
     *
     * @param stage     {@link Stage} — a janela principal da app
     * @param iconPath  {@code String} — caminho do icone no classpath, ficheiro ou URL
     * @param tooltip   {@code String} — texto ao passar o rato no icone
     * @param appTitle  {@code String} — titulo da app para fallback dos labels
     * @param openLabel {@code String} — texto do item abrir (null usa "Abrir {appTitle}")
     * @param exitLabel {@code String} — texto do item sair (null usa "Sair")
     */
    public void init(Stage stage, String iconPath, String tooltip, String appTitle,
                     String openLabel, String exitLabel) {
        if (!SystemTray.isSupported()) return;

        this.stage = stage;

        java.awt.Image icon = AppIconLoader.loadAwtImage(iconPath);

        PopupMenu menu = new PopupMenu();

        String resolvedTitle = isBlank(appTitle) ? "App" : appTitle;
        String resolvedOpenLabel = isBlank(openLabel) ? "Abrir " + resolvedTitle : openLabel;
        String resolvedExitLabel = isBlank(exitLabel) ? "Sair" : exitLabel;

        MenuItem abrirItem = new MenuItem(resolvedOpenLabel);
        abrirItem.addActionListener(e -> Platform.runLater(() -> {
            stage.show();
            stage.setIconified(false);
            stage.toFront();
        }));

        MenuItem sairItem = new MenuItem(resolvedExitLabel);
        sairItem.addActionListener(e -> {
            SystemTray.getSystemTray().remove(trayIcon);
            Platform.exit();
            System.exit(0);
        });

        menu.add(abrirItem);
        menu.addSeparator();
        menu.add(sairItem);

        trayIcon = new TrayIcon(icon, tooltip, menu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(e -> Platform.runLater(() -> {
            stage.show();
            stage.setIconified(false);
            stage.toFront();
        }));

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            System.err.println("[TrayManager] Nao foi possivel adicionar ao tray: "
                + e.getMessage());
        }
    }

    /**
     * Mostra uma notificacao nativa do OS via tray icon.
     * No-op se o tray nao foi inicializado.
     *
     * @param titulo   {@code String} — titulo da notificacao
     * @param mensagem {@code String} — corpo da mensagem
     * @param tipo     {@link NotificationType} — tipo (mapeia para icone nativo)
     */
    public void showNotification(String titulo, String mensagem,
                                  NotificationType tipo) {
        if (trayIcon == null) return;

        TrayIcon.MessageType awtTipo = switch (tipo) {
            case SUCCESS, INFO -> TrayIcon.MessageType.INFO;
            case WARNING       -> TrayIcon.MessageType.WARNING;
            case ERROR         -> TrayIcon.MessageType.ERROR;
        };

        trayIcon.displayMessage(titulo, mensagem, awtTipo);
    }

    /**
     * Remove o icone da tray.
     */
    public void remove() {
        if (trayIcon != null && SystemTray.isSupported()) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    /**
     * Indica se o tray foi inicializado com sucesso.
     *
     * @return {@code true} se ha tray icon ativo
     */
    public boolean isInitialized() {
        return trayIcon != null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
