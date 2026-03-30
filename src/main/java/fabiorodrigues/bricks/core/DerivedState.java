package fabiorodrigues.bricks.core;

import java.util.function.Supplier;

/**
 * Estado derivado — o valor e calculado automaticamente a partir de outros estados.
 * Equivalente ao {@code derivedStateOf} do Jetpack Compose.
 *
 * <p>O valor e recalculado apenas quando uma das dependencias muda.
 * Entre mudancas, devolve o valor em cache sem executar o supplier.</p>
 *
 * <p>Deve ser criado atraves de {@link BricksApplication#derived(Supplier, State[])}:</p>
 *
 * <pre>{@code
 * State<List<Note>> notas = state(List.of());
 * State<String> filtro = state("");
 *
 * DerivedState<List<Note>> visiveis = derived(
 *     () -> notas.get().stream()
 *         .filter(n -> n.matches(filtro.get()))
 *         .toList(),
 *     notas, filtro
 * );
 *
 * // no root() usa-se como um State normal
 * visiveis.get(); // recalcula se alguma dependencia mudou, ou devolve cache
 * }</pre>
 *
 * <p>Nao tem {@code set()} nem {@code update()} — o valor e apenas calculado, nunca definido externamente.</p>
 *
 * @param <T> o tipo do valor calculado
 */
public class DerivedState<T> {

    private final Supplier<T> supplier;
    private T cachedValue;
    private boolean dirty = true;
    private Runnable onChanged;

    /**
     * Cria um estado derivado e regista-se imediatamente como listener nas dependencias.
     * Dentro de uma {@link BricksApplication}, usar {@link BricksApplication#derived} em vez disso.
     *
     * @param supplier {@code Supplier<T>} — funcao que calcula o valor derivado
     * @param dependencies {@code State<?>...} — estados dos quais este valor depende
     */
    public DerivedState(Supplier<T> supplier, State<?>... dependencies) {
        this.supplier = supplier;
        for (State<?> dep : dependencies) {
            dep.addListener(() -> {
                dirty = true;
                if (onChanged != null) {
                    onChanged.run();
                }
            });
        }
    }

    /**
     * Devolve o valor atual.
     * Recalcula via supplier se alguma dependencia mudou desde a ultima chamada.
     * Caso contrario, devolve o valor em cache.
     *
     * @return o valor calculado ou em cache
     */
    public T get() {
        if (dirty) {
            cachedValue = supplier.get();
            dirty = false;
        }
        return cachedValue;
    }

    /**
     * Define o callback a executar quando o valor derivado precisa de ser recalculado.
     * Usado internamente por {@link BricksApplication}.
     *
     * @param callback a acao a executar
     */
    void setOnChanged(Runnable callback) {
        this.onChanged = callback;
    }
}
