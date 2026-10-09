package fabiorodrigues.bricks.core;

/**
 * Regra de validacao — recebe o valor e devolve mensagem de erro ou null se valido.
 *
 * @param <T> o tipo do valor validado
 */
@FunctionalInterface
public interface ValidationRule<T> {
    /**
     * Valida um valor.
     *
     * @param value o valor a validar
     * @return mensagem de erro, ou null se o valor e valido
     */
    String validate(T value);
}
