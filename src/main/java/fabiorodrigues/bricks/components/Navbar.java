package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Barra de navegacao superior. Por defeito inclui o botao de toggle da sidebar.
 * Usar {@link #noToggle()} quando usada sem sidebar (ex: navbar de navegacao standalone).
 *
 * <pre>{@code
 * // Com sidebar (dentro de AppLayout)
 * new Navbar()
 *     .item(new NavbarItem("fas-bell", null, () -> {}))
 *
 * // Sem sidebar — esconde o botao de toggle
 * new Navbar()
 *     .noToggle()
 *     .item(new NavbarItem("Inicio", () -> {}))
 *     .item(new NavbarItem("Sobre", () -> {}))
 * }</pre>
 */
public class Navbar implements Component {

    private final List<NavbarItem> items = new ArrayList<>();
    private Modifier modifier;
    private State<Boolean> sidebarAberta;
    private Runnable onToggle;
    private boolean showToggle = true;

    /**
     * Injetado pelo AppLayout — nao chamar diretamente.
     */
    void setSidebarAberta(State<Boolean> state) {
        this.sidebarAberta = state;
    }

    /**
     * Injetado pelo AppLayout — nao chamar diretamente.
     */
    void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
    }

    /**
     * Esconde o botao de toggle da sidebar.
     * Usar quando a Navbar e usada sem sidebar (ex: navegacao standalone).
     *
     * @return este componente para encadeamento
     */
    public Navbar noToggle() {
        this.showToggle = false;
        return this;
    }

    /**
     * Adiciona um item a navbar (aparecem à direita do spacer).
     *
     * @param item o item a adicionar
     * @return este componente para encadeamento
     */
    public Navbar item(NavbarItem item) {
        this.items.add(item);
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais adicionais.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Navbar modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        BricksTheme.ColorScheme cs = BricksTheme.current().colorScheme();

        Row row = new Row();
        row.gap(8);
        row.modifier(new Modifier()
            .padding(8, 16)
            .alignment(Pos.CENTER_LEFT)
            .background(cs.surfaceContainer())
        );

        if (showToggle) {
            javafx.scene.control.Button toggleBtn = new javafx.scene.control.Button();
            toggleBtn.setGraphic(new Icon("fas-bars").size(18).render());
            toggleBtn.getStyleClass().add("bricks-icon-button");
            toggleBtn.setOnAction(e -> {
                if (sidebarAberta != null) sidebarAberta.update(v -> !v);
                if (onToggle != null) onToggle.run();
            });
            row.children(() -> toggleBtn);
        }

        row.children(new Spacer());

        for (NavbarItem item : items) {
            javafx.scene.control.Button btn = new javafx.scene.control.Button();
            btn.getStyleClass().add("bricks-navbar-item");

            if (item.hasIcon()) {
                btn.setGraphic(new Icon(item.getIconCode()).size(16).render());
            }
            if (item.hasLabel()) {
                btn.setText(item.getLabel());
            }
            if (item.getOnClick() != null) {
                btn.setOnAction(e -> item.getOnClick().run());
            }
            row.children(() -> btn);
        }

        Node navbarNode = row.render();
        navbarNode.getStyleClass().add("bricks-navbar");

        if (modifier != null) {
            modifier.applyTo((Region) navbarNode);
        }

        return navbarNode;
    }
}
