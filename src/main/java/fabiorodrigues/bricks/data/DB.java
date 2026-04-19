package fabiorodrigues.bricks.data;

import fabiorodrigues.bricks.data.config.DbConfig;
import fabiorodrigues.bricks.data.config.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Ponto de entrada para o sistema de base de dados da lib Bricks.
 * Usa SQLite com {@code ./data/database.db} por defeito — nenhuma configuracao necessaria.
 *
 * <p>Uso padrao (SQLite):</p>
 * <pre>{@code
 * // Schema — criado uma vez no arranque via Effect
 * private final Effect initDb = effect(() -> {
 *     DB.query()
 *         .createTableIfNotExists("notas")
 *         .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
 *         .column("titulo", "TEXT NOT NULL")
 *         .column("conteudo", "TEXT")
 *         .execute();
 * });
 *
 * // Leitura
 * List<Nota> notas = DB.query()
 *     .select("id", "titulo", "conteudo")
 *     .from("notas")
 *     .orderBy("id", "DESC")
 *     .execute(Nota.class);
 *
 * // Insercao
 * int id = DB.query()
 *     .insertInto("notas")
 *     .values(Map.of("titulo", "Nota 1", "conteudo", "Texto..."))
 *     .execute();
 * }</pre>
 *
 * <p>Com MySQL:</p>
 * <pre>{@code
 * // numa BricksApplication, antes do Effect de schema:
 * {
 *     DB.configure(new MySQLConfig()
 *         .host("localhost")
 *         .database("escola")
 *         .user("root")
 *         .password("pass")
 *     );
 * }
 * }</pre>
 */
public final class DB {

    private static DbConfig config = new SQLiteConfig();
    private static Connection connection;

    private DB() {}

    /**
     * Define a configuracao de base de dados a usar.
     * Fecha a conexao existente se houver, para forcas uma nova ligacao com a nova config.
     * Deve ser chamado antes de qualquer {@link #query()}, tipicamente num
     * inicializador de instancia da {@code BricksApplication}.
     *
     * @param dbConfig {@code DbConfig} — configuracao a usar (null repoe o SQLite padrao)
     */
    public static synchronized void configure(DbConfig dbConfig) {
        closeConnection();
        config = dbConfig != null ? dbConfig : new SQLiteConfig();
    }

    /**
     * Devolve a conexao ativa, criando uma nova se nao existir ou tiver fechado.
     * A conexao e partilhada entre todas as queries — e reutilizada em vez de abrir
     * uma nova a cada {@code execute()}.
     *
     * @return a conexao JDBC ativa
     * @throws SQLException se nao for possivel ligar
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || !isValid()) {
            try {
                Class.forName(config.getDriver());
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver JDBC nao encontrado: " + config.getDriver(), e);
            }
            connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
        }
        return connection;
    }

    private static boolean isValid() {
        try {
            return !connection.isClosed() && connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            connection = null;
        }
    }

    /**
     * Devolve a configuracao atualmente ativa.
     *
     * @return a configuracao de base de dados
     */
    public static DbConfig getConfig() {
        return config;
    }

    /**
     * Configura a base de dados automaticamente a partir de uma classe {@code DatabaseConfig}
     * no classpath. Se nao existir, usa SQLite por defeito.
     *
     * <p>A classe deve ter um metodo {@code getConfig()} que devolve um {@link DbConfig}:</p>
     * <pre>{@code
     * public class DatabaseConfig {
     *     public DbConfig getConfig() {
     *         return new MySQLConfig().host("localhost").database("app").user("root").password("pass");
     *     }
     * }
     * }</pre>
     */
    public static synchronized void autoConfig() {
        try {
            Class<?> configClass = Class.forName("DatabaseConfig");
            Object instance = configClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method method = configClass.getMethod("getConfig");
            DbConfig dbConfig = (DbConfig) method.invoke(instance);
            configure(dbConfig);
        } catch (Exception e) {
            configure(new SQLiteConfig());
        }
    }

    /**
     * Cria uma nova {@link Query} com a configuracao atual.
     *
     * @return uma nova query pronta a encadear
     */
    public static Query query() {
        return new Query(config);
    }
}
