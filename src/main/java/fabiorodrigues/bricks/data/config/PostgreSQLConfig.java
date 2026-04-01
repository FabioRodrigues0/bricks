package fabiorodrigues.bricks.data.config;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Configuracao PostgreSQL com API fluente.
 *
 * <pre>{@code
 * DB.configure(new PostgreSQLConfig()
 *     .host("localhost")
 *     .database("escola")
 *     .user("postgres")
 *     .password("pass")
 * );
 * }</pre>
 *
 * <p>Para upsert, especificar o campo de conflito na query:</p>
 * <pre>{@code
 * DB.query()
 *     .insertInto("alunos")
 *     .values(Map.of("id", 1, "nome", "Fabio"))
 *     .onDuplicateUpdate("nome")
 *     .conflictOn("id")   // necessario no PostgreSQL
 *     .execute();
 * }</pre>
 *
 * <p>Requer o driver {@code org.postgresql:postgresql} no classpath do projeto.</p>
 */
public class PostgreSQLConfig extends DbConfig {

    private String host = "localhost";
    private int port = 5432;
    private String database = "";
    private String user = "postgres";
    private String password = "";

    /**
     * Define o host do servidor PostgreSQL.
     *
     * @param host {@code String} — endereco do servidor
     * @return esta configuracao para encadeamento
     */
    public PostgreSQLConfig host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Define a porta do servidor PostgreSQL.
     *
     * @param port {@code int} — porto TCP (padrao: 5432)
     * @return esta configuracao para encadeamento
     */
    public PostgreSQLConfig port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Define o nome da base de dados.
     *
     * @param database {@code String} — nome da base de dados
     * @return esta configuracao para encadeamento
     */
    public PostgreSQLConfig database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Define o utilizador de autenticacao.
     *
     * @param user {@code String} — utilizador PostgreSQL
     * @return esta configuracao para encadeamento
     */
    public PostgreSQLConfig user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Define a password de autenticacao.
     *
     * @param password {@code String} — password PostgreSQL
     * @return esta configuracao para encadeamento
     */
    public PostgreSQLConfig password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String limitSyntax(int limit, int offset) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String autoIncrementSyntax() {
        return "SERIAL PRIMARY KEY";
    }

    @Override
    public boolean supportsOnDuplicateKey() {
        return true;
    }

    @Override
    public String onConflictSyntax(String[] updateFields, String conflictTarget) {
        String target = conflictTarget != null ? "(" + conflictTarget + ")" : "";
        String updates = Arrays.stream(updateFields)
            .map(f -> f + " = EXCLUDED." + f)
            .collect(Collectors.joining(", "));
        return "ON CONFLICT " + target + " DO UPDATE SET " + updates;
    }
}
