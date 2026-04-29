package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.BricksApplication;
import fabiorodrigues.bricks.core.Component;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Janela modal bloqueante. Herda o tema da app principal.
 * Fecha automaticamente ao perder foco ou ao chamar {@code modal.close()}.
 *
 * <pre>{@code
 * Modal.show(app, modal ->
 *     new Column().children(
 *         new Text("Titulo").fontSize(18),
 *         new Button("Fechar").onClick(() -> modal.close())
 *     )
 * );
 * }</pre>
 */
public class Modal {

    /**
     * Interface funcional que recebe o Stage do modal para permitir
     * fechar a janela de dentro do conteudo.
     */
    @FunctionalInterface
    public interface ModalContent {
        Component build(Stage modal);
    }

    private Modal() {}

    /**
     * Abre um modal com tamanho default (400x300) e sem titulo.
     *
     * @param app     a aplicacao Bricks atual
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void show(BricksApplication app, ModalContent content) {
        show(app, null, 400, 300, content);
    }

    /**
     * Abre um modal com titulo e tamanho default (400x300).
     *
     * @param app     a aplicacao Bricks atual
     * @param title   titulo da janela modal
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void show(BricksApplication app, String title, ModalContent content) {
        show(app, title, 400, 300, content);
    }

    /**
     * Abre um modal com titulo e tamanho custom.
     *
     * @param app     a aplicacao Bricks atual
     * @param title   titulo da janela modal (null para sem titulo)
     * @param width   largura da janela em pixels
     * @param height  altura da janela em pixels
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void show(BricksApplication app, String title,
                            double width, double height, ModalContent content) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(app.getStage());

        if (title != null) {
            modal.setTitle(title);
        }

        StackPane root = new StackPane(content.build(modal).render());
        root.setPadding(new javafx.geometry.Insets(16));

        Scene scene = new Scene(root, width, height);

        if (app.getStage().getScene() != null) {
            scene.getStylesheets().addAll(
                app.getStage().getScene().getStylesheets()
            );
        }

        modal.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) modal.close();
        });

        modal.setScene(scene);
        modal.show();
    }
}
