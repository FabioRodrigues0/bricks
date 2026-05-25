package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Sidebar lateral com logo opcional e lista de {@link SidebarItem}.
 * Colapsa para mostrar so icons quando a sidebar esta fechada.
 * Usar dentro de {@link AppLayout}.
 *
 * <pre>{@code
 * new Sidebar()
 *     .logo("/logo.png")
 *     .item(new SidebarItem("fas-home", "Inicio", () -> {}))
 *     .item(new SidebarItem("fas-credit-card", "Cartoes", () -> {}))
 * }</pre>
 */
public class Sidebar implements Component {

    private String logoPath = null;
    private final List<SidebarItem> items = new ArrayList<>();
    private Modifier modifier;
    private State<Boolean> sidebarAberta;

    /**
     * Injetado pelo AppLayout — nao chamar diretamente.
     */
    void setSidebarAberta(State<Boolean> state) {
        this.sidebarAberta = state;
    }

    /**
     * Define o caminho do logo a mostrar no topo da sidebar quando expandida.
     *
     * @param imagePath caminho do recurso da imagem (ex: "/logo.png")
     * @return este componente para encadeamento
     */
    public Sidebar logo(String imagePath) {
        this.logoPath = imagePath;
        return this;
    }

    /**
     * Adiciona um item a sidebar.
     *
     * @param item o item a adicionar
     * @return este componente para encadeamento
     */
    public Sidebar item(SidebarItem item) {
        this.items.add(item);
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais adicionais.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Sidebar modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        boolean aberta = sidebarAberta == null || sidebarAberta.get();
        double largura = aberta ? 220 : 60;

        BricksTheme.ColorScheme cs = BricksTheme.current().colorScheme();

        Column col = new Column();
        col.modifier(new Modifier()
            .width(largura)
            .fillMaxHeight()
            .background(cs.surfaceContainer())
        );

        if (logoPath != null && aberta) {
            Node logoNode = new Image(logoPath).width(largura - 32).render();
            VBox.setMargin(logoNode, new Insets(16, 16, 8, 16));
            col.children(() -> logoNode);
            col.children(new Divider());
        }

        for (SidebarItem item : items) {
            if (!aberta && !item.hasIcon()) continue;

            javafx.scene.control.Button btn = new javafx.scene.control.Button();
            btn.getStyleClass().add("bricks-sidebar-item");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(aberta ? Pos.CENTER_LEFT : Pos.CENTER);
            btn.setOnAction(e -> item.getOnClick().run());

            if (item.hasIcon()) {
                btn.setGraphic(new Icon(item.getIconCode()).size(16).render());
            }
            if (aberta) {
                btn.setText(item.getLabel());
            }

            VBox.setMargin(btn, new Insets(2, 8, 2, 8));
            col.children(() -> btn);
        }

        Node sidebarNode = col.render();
        sidebarNode.getStyleClass().add("bricks-sidebar");

        if (modifier != null) {
            modifier.applyTo((Region) sidebarNode);
        }

        return sidebarNode;
    }
}
