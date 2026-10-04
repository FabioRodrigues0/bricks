package fabiorodrigues.bricks.data.config;

import fabiorodrigues.bricks.data.dialect.SqlDialect;

/**
 * Contrato de configuracao de base de dados.
 * Cada implementacao define a URL de ligacao, driver JDBC e o {@link SqlDialect} a usar.
 *
 * <p>Implementacoes disponiveis:</p>
 * <ul>
 *   <li>{@link SQLiteConfig} — padrao, sem configuracao necessaria</li>
 *   <li>{@link MySQLConfig} — host, porta, base de dados, user, password</li>
 *   <li>{@link PostgreSQLConfig} — host, porta, base de dados, user, password</li>
 *   <li>{@link SQLServerConfig} — SQL Server em Docker ou Azure SQL Database</li>
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
     * Dialeto SQL desta base de dados (paginacao, upsert, tipos, aspas).
     *
     * @return o dialeto
     */
    public abstract SqlDialect dialect();

    /**
     * Indica se a ligacao deve ficar aberta e partilhada entre queries.
     * Com {@code false}, cada query abre e fecha a sua ligacao — necessario em bases
     * serverless (Azure SQL) que so entram em pausa sem ligacoes abertas.
     *
     * @return true para manter uma ligacao partilhada (padrao)
     */
    public boolean keepConnectionOpen() {
        return true;
    }
}
