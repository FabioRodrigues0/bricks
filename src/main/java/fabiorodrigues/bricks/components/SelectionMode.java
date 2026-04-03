package fabiorodrigues.bricks.components;

/**
 * Controla quando uma ação da toolbar de um {@link DataTable} está ativa.
 */
public enum SelectionMode {
    /** A ação está sempre disponível. */
    ALWAYS,
    /** A ação só está disponível quando pelo menos um item está selecionado. */
    REQUIRES_SELECTION
}
