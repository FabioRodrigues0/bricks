package fabiorodrigues.bricks.core;

/**
 * Handler reativo que executa uma acao quando qualquer um dos estados dependentes muda.
 * Ao contrario do {@link DerivedState}, nao calcula nem devolve um valor — apenas executa.
 *
 * <p>Deve ser criado atraves de {@link BricksApplication#effect(Runnable, State[])}.</p>
 *
 * <p>Auto-save quando a lista de notas muda:</p>
 * <pre>{@code
 * private final Effect autoSave = effect(() -> {
 *     Storage.save(notas.get());
 * }, notas);
 * }</pre>
 *
 * <p>Com multiplas dependencias:</p>
 * <pre>{@code
 * private final Effect log = effect(() -> {
 *     System.out.println("filtro: " + filtro.get() + " | notas: " + notas.size());
 * }, filtro, notas);
 * }</pre>
 *
 * <p>Sem dependencias — executa uma vez no arranque:</p>
 * <pre>{@code
 * private final Effect onInit = effect(() -> {
 *     notas.addAll(Storage.load());
 * });
 * }</pre>
 */
public class Effect {

    /**
     * Cria um Effect e regista-se como listener em cada dependencia.
     * A acao e executada imediatamente uma vez no arranque, e depois
     * sempre que qualquer dependencia muda.
     *
     * @param action       {@code Runnable} — a acao a executar
     * @param dependencies {@code State<?>...} — os estados a observar
     */
    public Effect(Runnable action, State<?>... dependencies) {
        for (State<?> dep : dependencies) {
            dep.addListener(action);
        }
        action.run();
    }
}
