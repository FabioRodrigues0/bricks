package fabiorodrigues.bricks.data.config;

import fabiorodrigues.bricks.data.dialect.SQLServerDialect;
import fabiorodrigues.bricks.data.dialect.SqlDialect;

/**
 * Configuracao SQL Server / Azure SQL Database com API fluente.
 *
 * <p>Azure SQL (serverless) — cada query abre e fecha a ligacao para a base poder entrar em pausa:</p>
 * <pre>{@code
 * DB.configure(SQLServerConfig.azure("nome-do-servidor", "escola")
 *     .credentialsFromEnv("DB_USER", "DB_PASSWORD"));
 * }</pre>
 *
 * <p>SQL Server em Docker (desenvolvimento local):</p>
 * <pre>{@code
 * DB.configure(SQLServerConfig.localDocker("escola")
 *     .password(System.getenv("MSSQL_SA_PASSWORD")));
 * }</pre>
 *
 * <p>Requer o driver {@code com.microsoft.sqlserver:mssql-jdbc} (versao {@code .jre11}) no projeto da app.</p>
 */
public class SQLServerConfig extends DbConfig {

    private static final String AZURE_SUFFIX = ".database.windows.net";

    private String host = "localhost";
    private int port = 1433;
    private String database = "";
    private String user = "sa";
    private String password = "";
    private boolean encrypt = true;
    private boolean trustServerCertificate = true;
    private String hostNameInCertificate;
    private boolean keepConnectionOpen = true;

    /**
     * Configuracao para Azure SQL Database: TLS com certificado validado e sem ligacao
     * persistente (a base serverless so pausa sem ligacoes abertas).
     *
     * @param server   {@code String} — nome do servidor (ex: {@code "meu-servidor"}) ou host completo
     * @param database {@code String} — nome da base de dados
     * @return a configuracao para encadeamento
     */
    public static SQLServerConfig azure(String server, String database) {
        SQLServerConfig c = new SQLServerConfig();
        c.host = server.contains(".") ? server : server + AZURE_SUFFIX;
        c.database = database;
        c.user = null;
        c.trustServerCertificate = false;
        c.hostNameInCertificate = "*" + AZURE_SUFFIX;
        c.keepConnectionOpen = false;
        return c;
    }

    /**
     * Configuracao para SQL Server em Docker em {@code localhost:1433}, utilizador {@code sa},
     * aceitando o certificado auto-assinado do container.
     *
     * @param database {@code String} — nome da base de dados
     * @return a configuracao para encadeamento
     */
    public static SQLServerConfig localDocker(String database) {
        return new SQLServerConfig().database(database);
    }

    /**
     * Define o host do servidor.
     *
     * @param host {@code String} — endereco do servidor
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Define a porta do servidor.
     *
     * @param port {@code int} — porto TCP (padrao: 1433)
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Define o nome da base de dados.
     *
     * @param database {@code String} — nome da base de dados
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Define o utilizador de autenticacao.
     *
     * @param user {@code String} — utilizador SQL Server
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Define a password de autenticacao.
     *
     * @param password {@code String} — password SQL Server
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Le utilizador e password de variaveis de ambiente, para nao haver credenciais no codigo.
     *
     * @param userVar     {@code String} — nome da variavel com o utilizador
     * @param passwordVar {@code String} — nome da variavel com a password
     * @return esta configuracao para encadeamento
     * @throws IllegalStateException se alguma variavel nao estiver definida
     */
    public SQLServerConfig credentialsFromEnv(String userVar, String passwordVar) {
        this.user = requireEnv(userVar);
        this.password = requireEnv(passwordVar);
        return this;
    }

    /**
     * Define se o certificado do servidor e aceite sem validacao.
     * Usar {@code true} apenas em desenvolvimento local (Docker).
     *
     * @param trust {@code boolean} — aceitar qualquer certificado
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig trustServerCertificate(boolean trust) {
        this.trustServerCertificate = trust;
        return this;
    }

    /**
     * Define se a ligacao e cifrada com TLS (padrao: true).
     *
     * @param encrypt {@code boolean} — cifrar a ligacao
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig encrypt(boolean encrypt) {
        this.encrypt = encrypt;
        return this;
    }

    /**
     * Define se a ligacao fica aberta entre queries.
     * Padrao: true em {@link #localDocker(String)}, false em {@link #azure(String, String)}.
     *
     * @param keep {@code boolean} — manter ligacao partilhada
     * @return esta configuracao para encadeamento
     */
    public SQLServerConfig keepConnectionOpen(boolean keep) {
        this.keepConnectionOpen = keep;
        return this;
    }

    @Override
    public boolean keepConnectionOpen() {
        return keepConnectionOpen;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder("jdbc:sqlserver://").append(host).append(":").append(port)
            .append(";databaseName=").append(database)
            .append(";encrypt=").append(encrypt)
            .append(";trustServerCertificate=").append(trustServerCertificate)
            .append(";loginTimeout=30");
        if (hostNameInCertificate != null) {
            sb.append(";hostNameInCertificate=").append(hostNameInCertificate);
        }
        return sb.toString();
    }

    @Override
    public String getDriver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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
    public SqlDialect dialect() {
        return SQLServerDialect.INSTANCE;
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Variavel de ambiente nao definida: " + name);
        }
        return value;
    }
}
