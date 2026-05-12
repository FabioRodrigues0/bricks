package fabiorodrigues.bricks.core;

/**
 * Guarda a posicao do scroll de um LazyColumn entre re-renders.
 * Criar com {@code rememberScrollState()} no BricksScene ou BricksViewModel.
 *
 * <pre>{@code
 * private final ScrollState scrollState = rememberScrollState();
 *
 * new LazyColumn<Item>()
 *     .scrollState(scrollState)
 *     .items(lista)
 *     .item(i -> ...)
 * }</pre>
 */
public class ScrollState {
    double position = 0.0;

    /**
     * Devolve a posicao atual guardada.
     */
    public double getPosition() {
        return position;
    }

    /**
     * Define a posicao guardada — chamado internamente pelo LazyColumn.
     */
    public void setPosition(double position) {
        this.position = position;
    }
}
