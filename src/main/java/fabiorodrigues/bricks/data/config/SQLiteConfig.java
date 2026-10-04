package fabiorodrigues.bricks.data.config;

import fabiorodrigues.bricks.data.dialect.SQLiteDialect;
import fabiorodrigues.bricks.data.dialect.SqlDialect;

import java.io.File;

/**
 * Configuracao SQLite. Base de dados por defeito da lib Bricks.
 * Cria automaticamente a pasta {@code ./data/} se nao existir.
 *
 * <p>Uso padrao (sem configuracao):</p>
 * <pre>{@code
 * // DB.configure() nao e necessario — SQLite e o default
 * DB.query().createTableIfNotExists("alunos")...execute();
 * }</pre>
 *
 * <p>Com caminho personalizado:</p>
 * <pre>{@code
 * DB.configure(new SQLiteConfig("./minha_app/dados.db"));
 * }</pre>
 */
public class SQLiteConfig extends DbConfig {

    private final String path;

    /**
     * Cria configuracao SQLite com o caminho padrao {@code ./data/database.db}.
     */
    public SQLiteConfig() {
        this("./data/database.db");
    }

    /**
     * Cria configuracao SQLite com caminho personalizado.
     *
     * @param path {@code String} — caminho para o ficheiro .db (ex: {@code ./minha_app/dados.db})
     */
    public SQLiteConfig(String path) {
        this.path = path;
        File dir = new File(path).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlite:" + path;
    }

    @Override
    public String getDriver() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public SqlDialect dialect() {
        return SQLiteDialect.INSTANCE;
    }
}
