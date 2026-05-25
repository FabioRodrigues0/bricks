package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Layout principal da aplicacao com sidebar, navbar e conteudo scrollavel.
 *
 * <p>O esqueleto JavaFX e construido uma unica vez. Ao navegar entre paginas,
 * so o conteudo e substituido — sidebar e navbar nao re-renderizam, sem flicker.</p>
 *
 * <pre>{@code
 * // Criar como campo da scene/app — NAO dentro de root() / render()
 * private final AppLayout layout = new AppLayout()
 *     .sidebar(
 *         new Sidebar()
 *             .logo("/logo.png")
 *             .item(new SidebarItem("fas-home", "Inicio", () -> {}))
 *     )
 *     .navbar(new Navbar())
 *     .content(new DashboardScene());
 *
 * // Em root() / render() — devolver e atualizar o conteudo
 * public Component root() {
 *     return layout.content(currentPage);
 * }
 * }</pre>
 */
public class AppLayout implements Component {

    private Sidebar sidebar;
    private Navbar navbar;
    private Component content;
    private Runnable onToggle;

    private final State<Boolean> sidebarAberta = new State<>(true);

    // Estrutura JavaFX — construida uma vez, atualizada imperativamente
    private HBox rootHBox = null;
    private Node currentSidebarNode = null;
    private StackPane contentHolder = null;

    /**
     * Define a sidebar. Injeta o state de abertura automaticamente.
     *
     * @param sidebar a sidebar a usar
     * @return este componente para encadeamento
     */
    public AppLayout sidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
        sidebar.setSidebarAberta(sidebarAberta);
        return this;
    }

    /**
     * Define a navbar. Injeta o state de abertura automaticamente.
     *
     * @param navbar a navbar a usar
     * @return este componente para encadeamento
     */
    public AppLayout navbar(Navbar navbar) {
        this.navbar = navbar;
        navbar.setSidebarAberta(sidebarAberta);
        return this;
    }

    /**
     * Define ou atualiza o conteudo principal.
     * Pode ser chamado a cada render para trocar de pagina sem recriar o layout.
     *
     * @param content o componente de conteudo
     * @return este componente para encadeamento
     */
    public AppLayout content(Component content) {
        this.content = content;
        return this;
    }

    /**
     * Callback opcional chamado apos o toggle da sidebar.
     * Nao e necessario para o toggle funcionar — e apenas para efeitos laterais.
     *
     * @param onToggle callback opcional
     * @return este componente para encadeamento
     */
    public AppLayout onToggle(Runnable onToggle) {
        this.onToggle = onToggle;
        return this;
    }

    /**
     * Devolve o state interno de abertura da sidebar.
     *
     * @return o state booleano (true = expandida, false = colapsada)
     */
    public State<Boolean> getSidebarAberta() {
        return sidebarAberta;
    }

    @Override
    public Node render() {
        if (rootHBox == null) {
            buildLayout();
        }
        updateContent();
        return rootHBox;
    }

    private void buildLayout() {
        BricksTheme.ColorScheme cs = BricksTheme.current().colorScheme();

        rootHBox = new HBox();
        rootHBox.setMaxWidth(Double.MAX_VALUE);
        rootHBox.setMaxHeight(Double.MAX_VALUE);
        rootHBox.setStyle("-fx-background-color: " + hex(cs.background()) + ";");

        if (sidebar != null) {
            currentSidebarNode = sidebar.render();
            rootHBox.getChildren().add(currentSidebarNode);
        }

        VBox rightVBox = new VBox();
        HBox.setHgrow(rightVBox, Priority.ALWAYS);

        if (navbar != null) {
            // Toggle atualiza so o no da sidebar — sem re-render completo
            navbar.setOnToggle(() -> {
                updateSidebar();
                if (onToggle != null) onToggle.run();
            });
            rightVBox.getChildren().add(navbar.render());
        }

        contentHolder = new StackPane();
        VBox.setVgrow(contentHolder, Priority.ALWAYS);
        rightVBox.getChildren().add(contentHolder);

        rootHBox.getChildren().add(rightVBox);
    }

    private void updateSidebar() {
        if (sidebar == null || rootHBox == null) return;
        Node newSidebarNode = sidebar.render();
        int idx = rootHBox.getChildren().indexOf(currentSidebarNode);
        if (idx >= 0) {
            rootHBox.getChildren().set(idx, newSidebarNode);
        } else {
            rootHBox.getChildren().add(0, newSidebarNode);
        }
        currentSidebarNode = newSidebarNode;
    }

    private void updateContent() {
        if (contentHolder == null || content == null) return;
        Node contentNode = new ScrollView(content)
            .vertical()
            .modifier(new Modifier().fillMaxHeight())
            .render();
        contentHolder.getChildren().setAll(contentNode);
    }

    private static String hex(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
