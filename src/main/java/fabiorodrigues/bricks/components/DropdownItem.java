package fabiorodrigues.bricks.components;

/**
 * Item simples de um {@link DropdownMenu}. Executa uma acao ao clicar e fecha o menu.
 *
 * <pre>{@code
 * new DropdownItem("Exportar").onClick(() -> exportar())
 * }</pre>
 */
public class DropdownItem {

    private final String label;
    private Runnable onClick;

    /**
     * Cria um item com o texto dado.
     *
     * @param label texto do item
     */
    public DropdownItem(String label) {
        this.label = label;
    }

    /**
     * Define a acao a executar quando o item e clicado.
     *
     * @param action acao a executar
     * @return este item para encadeamento
     */
    public DropdownItem onClick(Runnable action) {
        this.onClick = action;
        return this;
    }

    String getLabel() {
        return label;
    }

    Runnable getOnClick() {
        return onClick;
    }
}
