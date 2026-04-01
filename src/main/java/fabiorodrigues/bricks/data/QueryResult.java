package fabiorodrigues.bricks.data;

import java.util.List;
import java.util.Map;

/**
 * Resultado bruto de uma query SELECT, sem mapeamento para uma classe especifica.
 * Cada linha e um {@code Map<String, Object>} onde a chave e o nome da coluna.
 *
 * <p>Util para queries ad-hoc ou introspeccao de resultados:</p>
 * <pre>{@code
 * QueryResult result = DB.query()
 *     .select("COUNT(*) as total")
 *     .from("alunos")
 *     .executeRaw();
 *
 * int total = (int) result.first().get("total");
 * }</pre>
 */
public class QueryResult {

    private final List<Map<String, Object>> rows;

    QueryResult(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    /**
     * Devolve todas as linhas do resultado.
     *
     * @return lista de linhas, cada uma como mapa coluna → valor
     */
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    /**
     * Devolve a primeira linha, ou {@code null} se o resultado estiver vazio.
     *
     * @return a primeira linha, ou null
     */
    public Map<String, Object> first() {
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * Indica se o resultado nao tem linhas.
     *
     * @return true se vazio
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    /**
     * Devolve o numero de linhas no resultado.
     *
     * @return numero de linhas
     */
    public int size() {
        return rows.size();
    }
}
