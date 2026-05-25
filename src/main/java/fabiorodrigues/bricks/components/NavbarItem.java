package fabiorodrigues.bricks.components;

/**
 * Item da Navbar com label e/ou icon e acao ao clicar.
 *
 * <pre>{@code
 * // So texto
 * new NavbarItem("Perfil", () -> {})
 *
 * // So icon (label = null)
 * new NavbarItem("fas-bell", null, () -> {})
 *
 * // Icon + texto
 * new NavbarItem("fas-user", "Perfil", () -> {})
 * }</pre>
 */
public class NavbarItem {

    private final String label;
    private final String iconCode;
    private final Runnable onClick;

    /**
     * Cria um item so com texto.
     *
     * @param label   texto do item
     * @param onClick acao ao clicar
     */
    public NavbarItem(String label, Runnable onClick) {
        this.label = label;
        this.iconCode = null;
        this.onClick = onClick;
    }

    /**
     * Cria um item com icon e label opcional.
     *
     * @param iconCode codigo Ikonli do icon (ex: "fas-bell")
     * @param label    texto ao lado do icon, ou null para so icon
     * @param onClick  acao ao clicar
     */
    public NavbarItem(String iconCode, String label, Runnable onClick) {
        this.iconCode = iconCode;
        this.label = label;
        this.onClick = onClick;
    }

    /** @return o texto do item, ou null se so icon */
    public String getLabel() { return label; }

    /** @return o codigo Ikonli do icon, ou null se so texto */
    public String getIconCode() { return iconCode; }

    /** @return true se tem icon */
    public boolean hasIcon() { return iconCode != null; }

    /** @return true se tem label */
    public boolean hasLabel() { return label != null; }

    /** @return a acao ao clicar */
    public Runnable getOnClick() { return onClick; }
}
