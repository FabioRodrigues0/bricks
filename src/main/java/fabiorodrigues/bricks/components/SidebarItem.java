package fabiorodrigues.bricks.components;

/**
 * Item individual da Sidebar com icon opcional, label e acao ao clicar.
 *
 * <pre>{@code
 * // Com icon
 * new SidebarItem("fas-home", "Inicio", () -> navigateTo(...))
 *
 * // Sem icon
 * new SidebarItem("Definicoes", () -> navigateTo(...))
 * }</pre>
 */
public class SidebarItem {

    private final String label;
    private final String iconCode;
    private final Runnable onClick;

    /**
     * Cria um item com icon e label.
     *
     * @param iconCode codigo Ikonli do icon (ex: "fas-home", "fas-user")
     * @param label    texto a mostrar ao lado do icon
     * @param onClick  acao ao clicar
     */
    public SidebarItem(String iconCode, String label, Runnable onClick) {
        this.iconCode = iconCode;
        this.label = label;
        this.onClick = onClick;
    }

    /**
     * Cria um item sem icon, so com label.
     * Quando a sidebar colapsa, este item fica invisivel.
     *
     * @param label   texto do item
     * @param onClick acao ao clicar
     */
    public SidebarItem(String label, Runnable onClick) {
        this.iconCode = null;
        this.label = label;
        this.onClick = onClick;
    }

    /** @return o texto do item */
    public String getLabel() { return label; }

    /** @return o codigo Ikonli do icon, ou null se nao tiver */
    public String getIconCode() { return iconCode; }

    /** @return true se o item tem icon */
    public boolean hasIcon() { return iconCode != null; }

    /** @return a acao ao clicar */
    public Runnable getOnClick() { return onClick; }
}
