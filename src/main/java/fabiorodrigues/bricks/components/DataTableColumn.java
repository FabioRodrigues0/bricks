package fabiorodrigues.bricks.components;

import java.util.function.Function;

/**
 * Definição de uma coluna de dados num {@link DataTable}.
 * Criada através de {@link DataTable#column(String, Function)}, não diretamente.
 *
 * @param <T> o tipo do item da linha
 */
public class DataTableColumn<T> {

    private final String name;
    private final Function<T, String> valueFunction;
    private double width = -1;
    private Align align = Align.LEFT;
    private boolean bold = false;
    private boolean wrapText = false;

    DataTableColumn(String name, Function<T, String> valueFunction) {
        this.name = name;
        this.valueFunction = valueFunction;
    }

    String getName() { return name; }
    Function<T, String> getValueFunction() { return valueFunction; }
    double getWidth() { return width; }
    Align getAlign() { return align; }
    boolean isBold() { return bold; }
    boolean isWrapText() { return wrapText; }

    void setWidth(double width) { this.width = width; }
    void setAlign(Align align) { this.align = align; }
    void setBold(boolean bold) { this.bold = bold; }
    void setWrapText(boolean wrapText) { this.wrapText = wrapText; }

    /**
     * Define a largura fixa (px) desta coluna.
     *
     * @param width largura em pixels
     * @return esta coluna para encadeamento
     */
    public DataTableColumn<T> width(double width) { this.width = width; return this; }

    /**
     * Define o alinhamento do conteudo desta coluna.
     *
     * @param align {@link Align#LEFT}, {@link Align#CENTER} ou {@link Align#RIGHT}
     * @return esta coluna para encadeamento
     */
    public DataTableColumn<T> align(Align align) { this.align = align; return this; }

    /**
     * Aplica negrito ao conteudo desta coluna.
     *
     * @return esta coluna para encadeamento
     */
    public DataTableColumn<T> bold() { this.bold = true; return this; }

    /**
     * Ativa o word wrap no conteudo desta coluna.
     *
     * @return esta coluna para encadeamento
     */
    public DataTableColumn<T> wrapText() { this.wrapText = true; return this; }
}
