package fabiorodrigues.bricks.data;

/**
 * Operadores de comparacao para clausulas WHERE type-safe.
 *
 * <p>Uso com enum:</p>
 * <pre>{@code
 * .where("idade", WhereOperator.GTE, 18)
 * .where("nome", WhereOperator.LIKE, "%Silva%")
 * .where("removido_em", WhereOperator.IS_NULL, null)
 * .where("id", WhereOperator.IN, List.of(1, 2, 3))
 * }</pre>
 *
 * <p>Uso com String (equivalente):</p>
 * <pre>{@code
 * .where("idade", ">=", 18)
 * .where("nome", "LIKE", "%Silva%")
 * }</pre>
 */
public enum WhereOperator {

    /** Igual: {@code campo = valor} */
    EQ("="),

    /** Diferente: {@code campo != valor} */
    NEQ("!="),

    /** Maior que: {@code campo > valor} */
    GT(">"),

    /** Maior ou igual: {@code campo >= valor} */
    GTE(">="),

    /** Menor que: {@code campo < valor} */
    LT("<"),

    /** Menor ou igual: {@code campo <= valor} */
    LTE("<="),

    /** Correspondencia parcial: {@code campo LIKE valor} (usar {@code %} como wildcard) */
    LIKE("LIKE"),

    /** Sem correspondencia: {@code campo NOT LIKE valor} */
    NOT_LIKE("NOT LIKE"),

    /** Contido numa lista: {@code campo IN (v1, v2, ...)} — valor deve ser {@code Collection<?>} */
    IN("IN"),

    /** Nao contido numa lista: {@code campo NOT IN (v1, v2, ...)} — valor deve ser {@code Collection<?>} */
    NOT_IN("NOT IN"),

    /** Nulo: {@code campo IS NULL} — valor ignorado */
    IS_NULL("IS NULL"),

    /** Nao nulo: {@code campo IS NOT NULL} — valor ignorado */
    IS_NOT_NULL("IS NOT NULL");

    private final String sql;

    WhereOperator(String sql) {
        this.sql = sql;
    }

    /**
     * Devolve o fragmento SQL correspondente ao operador.
     *
     * @return o operador em SQL
     */
    public String getSql() {
        return sql;
    }
}
