package fabiorodrigues.bricks.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Colecao reativa. Operacoes de mutacao ({@link #add}, {@link #remove}, {@link #set}, etc.)
 * notificam automaticamente a aplicacao para fazer re-render, sem necessidade de substituir
 * a lista inteira como acontece com {@code State<List<T>>}.
 *
 * <p>Deve ser criada atraves de {@link BricksApplication#stateList(List)}
 * para que as alteracoes disparem o re-render automaticamente.</p>
 *
 * <pre>{@code
 * // dentro de BricksApplication
 * private final StateList<Note> notas = stateList(seedNotes());
 *
 * // leitura
 * notas.get();        // snapshot imutavel
 * notas.get(0);       // item por indice
 * notas.size();
 *
 * // mutacao (dispara re-render)
 * notas.add(novaNota);
 * notas.remove(nota);
 * notas.set(0, notaAtualizada);
 * notas.clear();
 * }</pre>
 *
 * @param <T> o tipo dos elementos da colecao
 */
public class StateList<T> {

    private final ArrayList<T> items;
    private final List<Runnable> listeners = new ArrayList<>();

    /**
     * Cria um novo StateList com os elementos iniciais dados.
     *
     * @param initial lista com os valores iniciais (pode ser vazia)
     */
    public StateList(List<T> initial) {
        this.items = new ArrayList<>(initial);
    }

    // --- Leitura ---

    /**
     * Devolve um snapshot imutavel da lista atual.
     * Modificar o resultado nao afeta o estado nem dispara re-render.
     *
     * @return lista imutavel com os elementos atuais
     */
    public List<T> get() {
        return List.copyOf(items);
    }

    /**
     * Devolve o elemento no indice indicado.
     *
     * @param index {@code int} — posicao do elemento
     * @return o elemento nessa posicao
     */
    public T get(int index) {
        return items.get(index);
    }

    /**
     * Devolve o numero de elementos na lista.
     *
     * @return tamanho da lista
     */
    public int size() {
        return items.size();
    }

    /**
     * Indica se a lista esta vazia.
     *
     * @return {@code true} se nao tiver elementos
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Indica se a lista contem o elemento dado.
     *
     * @param item {@code T} — elemento a procurar
     * @return {@code true} se o elemento estiver presente
     */
    public boolean contains(T item) {
        return items.contains(item);
    }

    // --- Mutacao ---

    /**
     * Adiciona um elemento ao fim da lista e dispara re-render.
     *
     * @param item {@code T} — elemento a adicionar
     */
    public void add(T item) {
        items.add(item);
        notifyListeners();
    }

    /**
     * Insere um elemento numa posicao especifica e dispara re-render.
     *
     * @param index {@code int} — posicao onde inserir
     * @param item {@code T} — elemento a inserir
     */
    public void add(int index, T item) {
        items.add(index, item);
        notifyListeners();
    }

    /**
     * Remove a primeira ocorrencia do elemento dado e dispara re-render.
     *
     * @param item {@code T} — elemento a remover
     * @return {@code true} se o elemento foi encontrado e removido
     */
    public boolean remove(T item) {
        boolean removed = items.remove(item);
        if (removed) {
            notifyListeners();
        }
        return removed;
    }

    /**
     * Remove o elemento na posicao indicada e dispara re-render.
     *
     * @param index {@code int} — posicao do elemento a remover
     * @return o elemento removido
     */
    public T remove(int index) {
        T removed = items.remove(index);
        notifyListeners();
        return removed;
    }

    /**
     * Substitui o elemento na posicao indicada e dispara re-render.
     *
     * @param index {@code int} — posicao do elemento a substituir
     * @param item {@code T} — novo elemento
     */
    public void set(int index, T item) {
        items.set(index, item);
        notifyListeners();
    }

    /**
     * Remove todos os elementos da lista e dispara re-render.
     */
    public void clear() {
        items.clear();
        notifyListeners();
    }

    /**
     * Adiciona todos os elementos da lista dada e dispara re-render uma unica vez.
     *
     * @param collection {@code List<T>} — elementos a adicionar
     */
    public void addAll(List<T> collection) {
        items.addAll(collection);
        notifyListeners();
    }

    // --- Interno ---

    /**
     * Regista um listener a executar quando a lista e mutada.
     *
     * @param callback a acao a executar
     */
    void addListener(Runnable callback) {
        this.listeners.add(callback);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
