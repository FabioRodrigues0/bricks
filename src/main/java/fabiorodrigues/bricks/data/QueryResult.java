package fabiorodrigues.bricks.data;

import java.util.List;
import java.util.Map;

/**
 * Resultado bruto de uma query.
 * Em SELECT, cada linha e um {@code Map<String, Object>} onde a chave e o nome da coluna.
 * Em INSERT/UPDATE/DELETE, tambem pode incluir linhas afetadas e chaves geradas.
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
 *
 * <p>Util tambem para obter detalhes de INSERT:</p>
 * <pre>{@code
 * QueryResult result = DB.query()
 *     .insertInto("alunos")
 *     .values(Map.of("nome", "Fabio", "turma", 1))
 *     .executeResult();
 *
 * int id = result.getGeneratedIdAsInt();
 * Map<String, Object> aluno = result.first();
 * }</pre>
 */
public class QueryResult {

    private final List<Map<String, Object>> rows;
    private final List<Map<String, Object>> generatedKeys;
    private final int affectedRows;
    private final Object generatedId;

    QueryResult(List<Map<String, Object>> rows) {
        this(rows, List.of(), 0, null);
    }

    QueryResult(List<Map<String, Object>> rows, List<Map<String, Object>> generatedKeys, int affectedRows, Object generatedId) {
        this.rows = rows != null ? rows : List.of();
        this.generatedKeys = generatedKeys != null ? generatedKeys : List.of();
        this.affectedRows = affectedRows;
        this.generatedId = generatedId;
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
     * Devolve as chaves geradas pelo INSERT, quando o driver JDBC as disponibiliza.
     *
     * @return lista de chaves geradas como mapas coluna → valor
     */
    public List<Map<String, Object>> getGeneratedKeys() {
        return generatedKeys;
    }

    /**
     * Devolve o primeiro id gerado pelo INSERT, ou {@code null} se nao existir.
     *
     * @return id gerado, ou null
     */
    public Object getGeneratedId() {
        return generatedId;
    }

    /**
     * Devolve o primeiro id gerado como {@code int}, ou 0 se nao existir.
     *
     * @return id gerado como int, ou 0
     */
    public int getGeneratedIdAsInt() {
        if (generatedId instanceof Number number) {
            return number.intValue();
        }
        if (generatedId == null) {
            return 0;
        }
        return Integer.parseInt(generatedId.toString());
    }

    /**
     * Indica se existe id gerado pelo INSERT.
     *
     * @return true se existir id gerado
     */
    public boolean hasGeneratedId() {
        return generatedId != null;
    }

    /**
     * Devolve o numero de linhas afetadas por INSERT, UPDATE ou DELETE.
     *
     * @return linhas afetadas
     */
    public int getAffectedRows() {
        return affectedRows;
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
