package fabiorodrigues.bricks.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Contentor de estado reativo. Quando o valor muda, notifica a aplicacao
 * para fazer re-render da arvore de componentes.
 *
 * <p>Deve ser criado atraves de {@link BricksApplication#state(Object)}
 * para que as alteracoes disparem o re-render automaticamente.</p>
 *
 * <pre>{@code
 * // dentro de uma BricksApplication
 * private final State<Integer> count = state(0);
 *
 * // ler
 * count.get();
 *
 * // escrever (dispara re-render)
 * count.set(10);
 * count.update(c -> c + 1);
 * }</pre>
 *
 * @param <T> o tipo do valor guardado
 */
public class State<T> {

    private T value;
    private final List<Runnable> listeners = new ArrayList<>();

    /**
     * Cria um novo estado com o valor inicial dado.
     *
     * @param initial o valor inicial
     */
    public State(T initial) {
        this.value = initial;
    }

    /**
     * Devolve o valor atual do estado.
     *
     * @return o valor atual
     */
    public T get() {
        return value;
    }

    /**
     * Define um novo valor e notifica todos os listeners registados.
     *
     * @param newValue o novo valor
     */
    public void set(T newValue) {
        this.value = newValue;
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /**
     * Atualiza o valor aplicando uma funcao ao valor atual.
     *
     * <pre>{@code
     * count.update(c -> c + 1); // incrementa
     * }</pre>
     *
     * @param updater funcao que recebe o valor atual e devolve o novo
     */
    public void update(UnaryOperator<T> updater) {
        set(updater.apply(value));
    }

    /**
     * Atualiza o valor sem disparar re-render.
     * Usado internamente por componentes de input para guardar o valor enquanto o utilizador escreve,
     * sem recriar a arvore de componentes a cada tecla.
     *
     * @param newValue o novo valor
     */
    public void setQuietly(T newValue) {
        this.value = newValue;
    }

    /**
     * Regista um listener a executar quando o valor muda.
     * Suporta multiplos listeners — usado por {@link BricksApplication} e {@link DerivedState}.
     *
     * @param callback a acao a executar
     */
    void addListener(Runnable callback) {
        this.listeners.add(callback);
    }
}
