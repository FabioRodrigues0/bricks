package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.BricksApplication;
import fabiorodrigues.bricks.core.Component;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

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
        show(app, title, width, height, true, content);
    }

    /**
     * Abre um modal sem a barra nativa da janela (minimizar, maximizar, fechar).
     *
     * @param app     a aplicacao Bricks atual
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void showUndecorated(BricksApplication app, ModalContent content) {
        showUndecorated(app, null, 400, 300, content);
    }

    /**
     * Abre um modal sem a barra nativa da janela (minimizar, maximizar, fechar).
     *
     * @param app     a aplicacao Bricks atual
     * @param title   titulo da janela modal (fica disponivel no Stage, mas nao aparece sem decoracao)
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void showUndecorated(BricksApplication app, String title, ModalContent content) {
        showUndecorated(app, title, 400, 300, content);
    }

    /**
     * Abre um modal sem a barra nativa da janela (minimizar, maximizar, fechar)
     * e com tamanho custom.
     *
     * @param app     a aplicacao Bricks atual
     * @param title   titulo da janela modal (fica disponivel no Stage, mas nao aparece sem decoracao)
     * @param width   largura da janela em pixels
     * @param height  altura da janela em pixels
     * @param content lambda que recebe o Stage e devolve o Component a mostrar
     */
    public static void showUndecorated(BricksApplication app, String title,
                                       double width, double height, ModalContent content) {
        show(app, title, width, height, false, content);
    }

    private static void show(BricksApplication app, String title,
                             double width, double height, boolean decorated, ModalContent content) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(app.getStage());
        if (!decorated) {
            modal.initStyle(StageStyle.UNDECORATED);
        }

        if (title != null) {
            modal.setTitle(title);
        }

        StackPane root = new StackPane();
        root.setPadding(new javafx.geometry.Insets(16));
        root.setPrefSize(width, height);
        Runnable renderContent = () -> {
            root.getChildren().clear();
            Node node = content.build(modal).render();
            root.getChildren().add(node != null ? node : new Pane());
        };
        renderContent.run();
        app.addRerenderListener(renderContent);
        modal.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> app.removeRerenderListener(renderContent));

        Scene scene = new Scene(root, width, height);
        applyBackdrop(app, modal);

        if (app.getStage().getScene() != null) {
            scene.getStylesheets().addAll(
                app.getStage().getScene().getStylesheets()
            );
        }

        modal.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) modal.close();
        });

        modal.setScene(scene);
        modal.setResizable(false);
        modal.sizeToScene();
        modal.centerOnScreen();
        modal.show();
    }

    private static void applyBackdrop(BricksApplication app, Stage modal) {
        if (app.getStage() == null || app.getStage().getScene() == null) {
            return;
        }

        Node ownerRoot = app.getStage().getScene().getRoot();
        Effect previousEffect = ownerRoot.getEffect();

        ColorAdjust dimEffect = new ColorAdjust();
        dimEffect.setBrightness(-0.45);
        dimEffect.setSaturation(-0.15);
        dimEffect.setInput(previousEffect);

        ownerRoot.setEffect(dimEffect);
        modal.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> ownerRoot.setEffect(previousEffect));
    }
}
