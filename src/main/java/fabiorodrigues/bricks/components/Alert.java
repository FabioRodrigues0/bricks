package fabiorodrigues.bricks.components;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utilitario para mostrar dialogos de alerta ao utilizador.
 * Nao e um componente de layout — e invocado diretamente para mostrar uma janela.
 *
 * <p>Uso simples:</p>
 * <pre>{@code
 * Alert.info("Titulo", "Operacao concluida com sucesso.");
 * Alert.warning("Aviso", "Nao podes fazer isso ainda.");
 * Alert.error("Erro", "Ocorreu um erro inesperado.");
 * }</pre>
 *
 * <p>Com confirmacao:</p>
 * <pre>{@code
 * boolean confirmado = Alert.confirm("Confirmacao", "Tens a certeza?");
 * if (confirmado) { ... }
 * }</pre>
 *
 * <p>Ou via instancia para mais controlo:</p>
 * <pre>{@code
 * new Alert("Aviso", "Mensagem").type(Alert.Type.WARNING).show();
 * }</pre>
 */
public class Alert {

    /**
     * Tipos de alerta disponiveis.
     */
    public enum Type {
        /** Informacao geral. Icone azul. */
        INFO,
        /** Aviso ao utilizador. Icone amarelo. */
        WARNING,
        /** Erro ou falha. Icone vermelho. */
        ERROR,
        /** Pergunta de confirmacao com botoes OK/Cancelar. */
        CONFIRMATION
    }

    private final String title;
    private final String message;
    private Type type = Type.INFO;

    /**
     * Cria um alerta com titulo e mensagem.
     *
     * @param title {@code String} — titulo da janela do alerta
     * @param message {@code String} — mensagem a mostrar ao utilizador
     */
    public Alert(String title, String message) {
        this.title = title;
        this.message = message;
    }

    /**
     * Define o tipo do alerta (INFO, WARNING, ERROR, CONFIRMATION).
     *
     * @param type {@code Alert.Type} — tipo do alerta
     * @return este alerta para encadeamento
     */
    public Alert type(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Mostra o dialogo. Bloqueia ate o utilizador fechar.
     * No caso de CONFIRMATION, usa {@link #confirm} para obter a resposta.
     */
    public void show() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(toAlertType(type));
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- atalhos estaticos ---

    /**
     * Mostra um alerta de informacao.
     *
     * @param title {@code String} — titulo da janela
     * @param message {@code String} — mensagem a mostrar
     */
    public static void info(String title, String message) {
        new Alert(title, message).type(Type.INFO).show();
    }

    /**
     * Mostra um alerta de aviso.
     *
     * @param title {@code String} — titulo da janela
     * @param message {@code String} — mensagem a mostrar
     */
    public static void warning(String title, String message) {
        new Alert(title, message).type(Type.WARNING).show();
    }

    /**
     * Mostra um alerta de erro.
     *
     * @param title {@code String} — titulo da janela
     * @param message {@code String} — mensagem a mostrar
     */
    public static void error(String title, String message) {
        new Alert(title, message).type(Type.ERROR).show();
    }

    /**
     * Mostra um dialogo de confirmacao com botoes OK e Cancelar.
     * Bloqueia ate o utilizador responder.
     *
     * @param title {@code String} — titulo da janela
     * @param message {@code String} — pergunta a mostrar ao utilizador
     * @return {@code boolean} — true se o utilizador clicou OK, false se cancelou
     */
    public static boolean confirm(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait()
            .map(btn -> btn == ButtonType.OK)
            .orElse(false);
    }

    private static AlertType toAlertType(Type type) {
        return switch (type) {
            case INFO -> AlertType.INFORMATION;
            case WARNING -> AlertType.WARNING;
            case ERROR -> AlertType.ERROR;
            case CONFIRMATION -> AlertType.CONFIRMATION;
        };
    }
}
