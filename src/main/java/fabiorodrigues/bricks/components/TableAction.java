package fabiorodrigues.bricks.components;

import java.util.function.Consumer;

/**
 * Botão de ação compacto (32×32) com ícone, usado nas colunas de ação de um {@link DataTable}.
 *
 * <pre>{@code
 * new TableAction<Aluno>()
 *     .icon("fas-pencil")
 *     .tooltip("Editar")
 *     .onClick(a -> editar(a))
 *
 * new TableAction<Aluno>()
 *     .icon("fas-trash")
 *     .tooltip("Apagar")
 *     .danger()
 *     .onClick(a -> apagar(a))
 * }</pre>
 *
 * @param <T> o tipo do item da linha
 */
public class TableAction<T> {

    private String icon;
    private String tooltip;
    private boolean danger = false;
    private Consumer<T> onClick;

    /**
     * Define o ícone Ikonli a mostrar (ex: {@code "fas-trash"}, {@code "fas-pencil"}).
     *
     * @param icon código do ícone Ikonli
     * @return este objeto para encadeamento
     */
    public TableAction<T> icon(String icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Define o texto que aparece ao fazer hover sobre o botão.
     *
     * @param tooltip texto do tooltip
     * @return este objeto para encadeamento
     */
    public TableAction<T> tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Marca a ação como destrutiva — o ícone fica vermelho e o hover tem fundo rosado.
     *
     * @return este objeto para encadeamento
     */
    public TableAction<T> danger() {
        this.danger = true;
        return this;
    }

    /**
     * Define a ação a executar quando o botão é clicado.
     * Recebe o item da linha correspondente.
     *
     * @param onClick {@code Consumer<T>} — ação que recebe o item da linha
     * @return este objeto para encadeamento
     */
    public TableAction<T> onClick(Consumer<T> onClick) {
        this.onClick = onClick;
        return this;
    }

    String getIcon() { return icon; }
    String getTooltip() { return tooltip; }
    boolean isDanger() { return danger; }
    Consumer<T> getOnClick() { return onClick; }
}
