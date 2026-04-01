package fabiorodrigues.bricks.data.config;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Configuracao MySQL com API fluente.
 *
 * <pre>{@code
 * DB.configure(new MySQLConfig()
 *     .host("localhost")
 *     .port(3306)
 *     .database("escola")
 *     .user("root")
 *     .password("pass")
 * );
 * }</pre>
 *
 * <p>Requer o driver {@code com.mysql:mysql-connector-j} no classpath do projeto.</p>
 */
public class MySQLConfig extends DbConfig {

    private String host = "localhost";
    private int port = 3306;
    private String database = "";
    private String user = "root";
    private String password = "";

    /**
     * Define o host do servidor MySQL.
     *
     * @param host {@code String} — endereco do servidor (ex: {@code "localhost"}, {@code "192.168.1.1"})
     * @return esta configuracao para encadeamento
     */
    public MySQLConfig host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Define a porta do servidor MySQL.
     *
     * @param port {@code int} — porto TCP (padrao: 3306)
     * @return esta configuracao para encadeamento
     */
    public MySQLConfig port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Define o nome da base de dados.
     *
     * @param database {@code String} — nome da base de dados
     * @return esta configuracao para encadeamento
     */
    public MySQLConfig database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Define o utilizador de autenticacao.
     *
     * @param user {@code String} — utilizador MySQL
     * @return esta configuracao para encadeamento
     */
    public MySQLConfig user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Define a password de autenticacao.
     *
     * @param password {@code String} — password MySQL
     * @return esta configuracao para encadeamento
     */
    public MySQLConfig password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    @Override
    public String getDriver() {
        return "com.mysql.cj.jdbc.Driver";
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
        return "INT PRIMARY KEY AUTO_INCREMENT";
    }

    @Override
    public boolean supportsOnDuplicateKey() {
        return true;
    }

    @Override
    public String onConflictSyntax(String[] updateFields, String conflictTarget) {
        String updates = Arrays.stream(updateFields)
            .map(f -> f + " = VALUES(" + f + ")")
            .collect(Collectors.joining(", "));
        return "ON DUPLICATE KEY UPDATE " + updates;
    }
}
