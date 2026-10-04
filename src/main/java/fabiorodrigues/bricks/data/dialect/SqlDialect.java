package fabiorodrigues.bricks.data.dialect;

import java.util.List;

/**
 * Sintaxe especifica de cada base de dados.
 * Ao contrario de sufixos colados ao SQL, cada metodo recebe contexto e devolve
 * o statement ou fragmento completo — necessario para SQL Server, onde paginacao,
 * upsert e {@code CREATE TABLE IF NOT EXISTS} tem forma diferente.
 *
 * <p>Implementacoes: {@link SQLiteDialect}, {@link MySQLDialect},
 * {@link PostgreSQLDialect}, {@link SQLServerDialect}.</p>
 */
public interface SqlDialect {

    /**
     * Coloca um identificador gerado pelo builder entre aspas do dialeto.
     * Nomes com ponto ({@code "dbo.alunos"}) sao tratados parte a parte.
     *
     * @param name {@code String} — nome de tabela ou coluna
     * @return o identificador entre aspas
     */
    String quoteIdentifier(String name);

    /**
     * Traduz um tipo portavel para o tipo nativo.
     *
     * @param type {@code ColumnType} — tipo portavel
     * @return a definicao SQL do tipo (ex: {@code NVARCHAR(255)})
     */
    String columnType(ColumnType type);

    /**
     * Acrescenta paginacao a um SELECT ja montado (incluindo ORDER BY, se existir).
     * Os valores sao escritos no SQL — nao ha parametros {@code ?} na paginacao.
     *
     * @param sql        {@code String} — SELECT completo
     * @param limit      {@code Integer} — maximo de linhas ({@code >= 1}), ou null
     * @param offset     {@code Integer} — linhas a saltar ({@code >= 0}), ou null
     * @param hasOrderBy {@code boolean} — se o SELECT ja tem ORDER BY
     * @return o SELECT paginado (igual ao original se ambos forem null)
     */
    String paginate(String sql, Integer limit, Integer offset, boolean hasOrderBy);

    /**
     * Gera um CREATE TABLE que nao falha se a tabela ja existir.
     *
     * @param table       {@code String} — nome da tabela (sem aspas)
     * @param definitions {@code String} — definicoes de coluna ja montadas
     * @return o statement completo
     */
    String createTableIfNotExists(String table, String definitions);

    /**
     * Gera um upsert completo com um {@code ?} por coluna, pela ordem de {@code columns}.
     *
     * @param table           {@code String} — nome da tabela (sem aspas)
     * @param columns         {@code List<String>} — colunas inseridas, pela ordem de bind
     * @param conflictColumns {@code List<String>} — colunas com PK/UNIQUE que definem o conflito (pode ser vazia se o dialeto permitir)
     * @param updateColumns   {@code List<String>} — colunas a atualizar em caso de conflito (vazia = ignorar o conflito)
     * @return o statement completo
     */
    String upsert(String table, List<String> columns, List<String> conflictColumns, List<String> updateColumns);
}
