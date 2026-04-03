package fabiorodrigues.bricks.components;

import java.util.function.Consumer;

/**
 * Item com checkbox de um {@link DropdownMenu}. Nao fecha o menu ao clicar.
 *
 * <pre>{@code
 * new DropdownCheckItem("Nome").checked(true).onChange(val -> ...)
 * }</pre>
 */
public class DropdownCheckItem {

    private final String label;
    private boolean checked = false;
    private Consumer<Boolean> onChange;

    /**
     * Cria um item com checkbox com o texto dado.
     *
     * @param label texto do item
     */
    public DropdownCheckItem(String label) {
        this.label = label;
    }

    /**
     * Define o estado inicial da checkbox.
     *
     * @param checked true para marcada, false para desmarcada
     * @return este item para encadeamento
     */
    public DropdownCheckItem checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    /**
     * Define um callback chamado sempre que o estado da checkbox muda.
     *
     * @param callback funcao que recebe o novo valor (true/false)
     * @return este item para encadeamento
     */
    public DropdownCheckItem onChange(Consumer<Boolean> callback) {
        this.onChange = callback;
        return this;
    }

    String getLabel() {
        return label;
    }

    boolean isChecked() {
        return checked;
    }

    Consumer<Boolean> getOnChange() {
        return onChange;
    }
}
