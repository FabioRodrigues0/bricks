package fabiorodrigues.bricks.data.config;

/**
 * Contrato de configuracao de base de dados.
 * Cada implementacao define a URL de ligacao, driver JDBC e sintaxe especifica.
 *
 * <p>Implementacoes disponiveis:</p>
 * <ul>
 *   <li>{@link SQLiteConfig} — padrao, sem configuracao necessaria</li>
 *   <li>{@link MySQLConfig} — host, porta, base de dados, user, password</li>
 *   <li>{@link PostgreSQLConfig} — host, porta, base de dados, user, password</li>
 * </ul>
 */
public abstract class DbConfig {

    /**
     * URL JDBC de ligacao (ex: {@code jdbc:sqlite:./data/database.db}).
     *
     * @return a URL de ligacao
     */
    public abstract String getUrl();

    /**
     * Nome completo da classe do driver JDBC (ex: {@code org.sqlite.JDBC}).
     *
     * @return o nome da classe do driver
     */
    public abstract String getDriver();

    /**
     * Utilizador para autenticacao. Pode ser {@code null} para SQLite.
     *
     * @return o utilizador, ou null
     */
    public abstract String getUser();

    /**
     * Password para autenticacao. Pode ser {@code null} para SQLite.
     *
     * @return a password, ou null
     */
    public abstract String getPassword();

    /**
     * Clausula LIMIT/OFFSET no dialeto desta base de dados.
     *
     * @param limit  numero maximo de linhas
     * @param offset numero de linhas a saltar
     * @return a clausula SQL gerada
     */
    public abstract String limitSyntax(int limit, int offset);

    /**
     * Sintaxe para o campo auto-incremento no CREATE TABLE.
     *
     * @return a definicao de coluna auto-incremento (ex: {@code INTEGER PRIMARY KEY AUTOINCREMENT})
     */
    public abstract String autoIncrementSyntax();

    /**
     * Indica se a base de dados suporta upsert via clausula de conflito.
     * SQLite usa {@code INSERT OR REPLACE} como prefixo em vez de clausula.
     *
     * @return true se suportar clausula de conflito pos-VALUES
     */
    public abstract boolean supportsOnDuplicateKey();

    /**
     * Gera a clausula de upsert para os campos indicados.
     * Chamado apenas quando {@link #supportsOnDuplicateKey()} e true.
     *
     * @param updateFields  campos a atualizar em caso de conflito
     * @param conflictTarget campo de conflito (necessario para PostgreSQL, ignorado pelo MySQL)
     * @return a clausula SQL gerada
     */
    public abstract String onConflictSyntax(String[] updateFields, String conflictTarget);
}
