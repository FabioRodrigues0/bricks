package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;

/**
 * Layout principal da aplicacao com sidebar, navbar e conteudo scrollavel.
 *
 * <pre>{@code
 * new AppLayout()
 *     .onToggle(this::rerender)   // ou app::rerender em BricksApplication
 *     .sidebar(
 *         new Sidebar()
 *             .logo("/logo.png")
 *             .item(new SidebarItem("fas-home", "Inicio", () -> {}))
 *     )
 *     .navbar(
 *         new Navbar()
 *             .item(new NavbarItem("Perfil", () -> {}))
 *     )
 *     .content(new Column().children(...))
 * }</pre>
 *
 * <p>O toggle da sidebar dispara {@code onToggle} — tipicamente o metodo de re-render
 * da BricksScene ou BricksApplication — para que a sidebar reflita o novo estado.</p>
 */
public class AppLayout implements Component {

    private Sidebar sidebar;
    private Navbar navbar;
    private Component content;
    private Runnable onToggle;

    private final State<Boolean> sidebarAberta = new State<>(true);

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
     * Define a navbar. Injeta o state de abertura e o callback de toggle automaticamente.
     *
     * @param navbar a navbar a usar
     * @return este componente para encadeamento
     */
    public AppLayout navbar(Navbar navbar) {
        this.navbar = navbar;
        navbar.setSidebarAberta(sidebarAberta);
        if (onToggle != null) navbar.setOnToggle(onToggle);
        return this;
    }

    /**
     * Define o conteudo principal (area scrollavel abaixo da navbar).
     *
     * @param content o componente de conteudo
     * @return este componente para encadeamento
     */
    public AppLayout content(Component content) {
        this.content = content;
        return this;
    }

    /**
     * Regista o callback a executar quando o toggle da sidebar e ativado.
     * Deve apontar para o metodo de re-render da scene ou aplicacao.
     * Pode ser chamado antes ou depois de {@link #navbar(Navbar)}.
     *
     * <pre>{@code
     * // Em BricksApplication:
     * .onToggle(this::rerender)
     *
     * // Em BricksScene:
     * .onToggle(this::rerender)
     * }</pre>
     *
     * @param onToggle callback de re-render
     * @return este componente para encadeamento
     */
    public AppLayout onToggle(Runnable onToggle) {
        this.onToggle = onToggle;
        if (navbar != null) navbar.setOnToggle(onToggle);
        return this;
    }

    /**
     * Devolve o state interno de abertura da sidebar.
     * Util para forcar o estado ou observar externamente.
     *
     * @return o state booleano (true = expandida, false = colapsada)
     */
    public State<Boolean> getSidebarAberta() {
        return sidebarAberta;
    }

    @Override
    public Node render() {
        BricksTheme.ColorScheme cs = BricksTheme.current().colorScheme();

        Column rightCol = new Column();
        rightCol.modifier(new Modifier().fillMaxWidth().fillMaxHeight());

        if (navbar != null) {
            rightCol.children(navbar);
        }

        if (content != null) {
            rightCol.children(
                new ScrollView(content)
                    .vertical()
                    .modifier(new Modifier().fillMaxHeight())
            );
        }

        Row root = new Row();
        root.modifier(new Modifier()
            .background(cs.background())
            .fillMaxWidth()
            .fillMaxHeight()
        );

        if (sidebar != null) {
            root.children(sidebar);
        }

        root.children(rightCol);

        return root.render();
    }
}
