package fabiorodrigues.bricks.style;

/**
 * Registo do tema ativo na aplicacao Bricks.
 * Permite que qualquer componente aceda ao tema atual sem necessidade de passagem explicita.
 *
 * <p>Equivalente ao {@code LocalContentColor} / {@code LocalTextStyle} do Jetpack Compose.</p>
 *
 * <p>Uso nos componentes:</p>
 * <pre>{@code
 * // aceder ao tema atual
 * BricksTheme tema = ThemeRegistry.current();
 * }</pre>
 *
 * <p>Preferir usar {@link BricksTheme#current()} que e a API publica equivalente.</p>
 */
public final class ThemeRegistry {

    private static BricksTheme current = BricksTheme.material();

    private ThemeRegistry() {}

    /**
     * Devolve o tema atualmente ativo.
     * Nunca devolve null — usa Material light por defeito.
     *
     * @return o tema ativo
     */
    public static BricksTheme current() {
        return current;
    }

    /**
     * Define o tema ativo.
     * Chamado apenas por {@link fabiorodrigues.bricks.core.BricksApplication#setTheme}.
     *
     * @param theme o novo tema (se null, repoe o Material light)
     */
    public static void set(BricksTheme theme) {
        current = theme != null ? theme : BricksTheme.material();
    }
}
