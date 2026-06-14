package fabiorodrigues.bricks.core;

import java.nio.file.Path;

/**
 * Configuracao central de caminhos usados pela aplicacao.
 */
public final class BricksPaths {

    private static Path userDataPath = defaultUserDataPath();

    private BricksPaths() {
    }

    /**
     * Define a pasta base para ficheiros fornecidos pelo utilizador.
     *
     * @param baseDir caminho absoluto ou relativo ao diretorio atual
     */
    public static void setPathUserData(String baseDir) {
        setUserDataPath(baseDir);
    }

    /**
     * Define a pasta base para ficheiros fornecidos pelo utilizador.
     *
     * @param baseDir caminho absoluto ou relativo ao diretorio atual
     */
    public static void setUserDataPath(String baseDir) {
        if (baseDir == null || baseDir.isBlank()) {
            throw new IllegalArgumentException("Path user data nao pode ser vazio");
        }
        userDataPath = Path.of(baseDir).toAbsolutePath().normalize();
    }

    /**
     * Devolve a pasta base para ficheiros fornecidos pelo utilizador.
     *
     * @return pasta base configurada
     */
    public static Path getUserDataPath() {
        return userDataPath;
    }

    /**
     * Resolve um caminho contra a pasta base de user data.
     * Caminhos absolutos sao mantidos.
     *
     * @param path caminho relativo a user data ou caminho absoluto
     * @return caminho absoluto normalizado
     */
    public static Path resolveUserData(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Caminho de user data nao pode ser vazio");
        }

        Path value = Path.of(path);
        if (value.isAbsolute()) {
            return value.normalize();
        }
        return userDataPath.resolve(value).normalize();
    }

    private static Path defaultUserDataPath() {
        return Path.of(System.getProperty("user.home"), ".bricks").toAbsolutePath().normalize();
    }
}
