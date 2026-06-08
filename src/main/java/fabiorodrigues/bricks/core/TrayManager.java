package fabiorodrigues.bricks.core;

import fabiorodrigues.bricks.components.NotificationType;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.Locale;

/**
 * Gere o icone na system tray e as notificacoes nativas do OS.
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
    private String appTitle = "App";
    private boolean initializedForNotifications;

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
        this.stage = stage;
        this.appTitle = isBlank(appTitle) ? "App" : appTitle;
        this.initializedForNotifications = true;

        if (!SystemTray.isSupported()) return;

        java.awt.Image icon = AppIconLoader.loadAwtImage(iconPath);

        PopupMenu menu = new PopupMenu();

        String resolvedTitle = this.appTitle;
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
     * Mostra uma notificacao nativa do OS.
     * No macOS usa o Notification Center via osascript; nos restantes sistemas
     * usa o tray icon AWT. No-op se o tray/notificacoes nao foram inicializados.
     *
     * @param titulo   {@code String} — titulo da notificacao
     * @param mensagem {@code String} — corpo da mensagem
     * @param tipo     {@link NotificationType} — tipo (mapeia para icone nativo)
     */
    public void showNotification(String titulo, String mensagem,
                                  NotificationType tipo) {
        if (isMacOs()) {
            showMacNotification(titulo, mensagem, tipo);
            return;
        }

        if (trayIcon == null) return;

        TrayIcon.MessageType awtTipo = switch (tipo) {
            case SUCCESS, INFO -> TrayIcon.MessageType.INFO;
            case WARNING       -> TrayIcon.MessageType.WARNING;
            case ERROR         -> TrayIcon.MessageType.ERROR;
        };

        trayIcon.displayMessage(titulo, mensagem, awtTipo);
    }

    private void showMacNotification(String titulo, String mensagem,
                                      NotificationType tipo) {
        if (!initializedForNotifications) return;

        String script = "display notification " + toAppleScriptString(mensagem)
            + " with title " + toAppleScriptString(resolveNotificationTitle(titulo))
            + " subtitle " + toAppleScriptString(toMacSubtitle(tipo));

        try {
            new ProcessBuilder("osascript", "-e", script).start();
        } catch (IOException e) {
            System.err.println("[TrayManager] Nao foi possivel mostrar notificacao macOS: "
                + e.getMessage());
        }
    }

    /**
     * Remove o icone da tray.
     */
    public void remove() {
        if (trayIcon != null && SystemTray.isSupported()) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
        initializedForNotifications = false;
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

    private boolean isMacOs() {
        return isMacOsName(System.getProperty("os.name", ""));
    }

    private String resolveNotificationTitle(String title) {
        return isBlank(title) ? appTitle : title;
    }

    static boolean isMacOsName(String osName) {
        return osName != null && osName.toLowerCase(Locale.ROOT).contains("mac");
    }

    static String toAppleScriptString(String value) {
        if (value == null) return "\"\"";

        String escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }

    static String toMacSubtitle(NotificationType tipo) {
        return switch (tipo) {
            case SUCCESS -> "Sucesso";
            case INFO    -> "Informacao";
            case WARNING -> "Aviso";
            case ERROR   -> "Erro";
        };
    }
}
