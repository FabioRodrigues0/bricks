package fabiorodrigues.bricks.style;

import javafx.scene.paint.Color;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Define o tema visual da aplicacao Bricks, equivalente ao {@code MaterialTheme} do Jetpack Compose.
 *
 * <p>Por defeito, todas as aplicacoes Bricks usam o tema Material 3 light automaticamente.
 * Nao e necessario fazer nada para ter uma aparencia consistente e profissional.</p>
 *
 * <p>Para usar o tema dark:</p>
 * <pre>{@code
 * { setTheme(BricksTheme.dark()); }
 * }</pre>
 *
 * <p>Para personalizar:</p>
 * <pre>{@code
 * { setTheme(BricksTheme.material()
 *     .colorScheme().primary(Color.web("#E91E63")).and()
 *     .shapes().medium(20).and()
 *     .typography().bodyLarge(16)); }
 * }</pre>
 *
 * <p>Para aceder a uma cor do tema num Modifier:</p>
 * <pre>{@code
 * new Text("Olá").modifier(
 *     new Modifier().textColor(BricksTheme.current().colorScheme().primary())
 * )
 * }</pre>
 */
public class BricksTheme {

    // -------------------------------------------------------------------------
    // Inner class: ColorScheme
    // -------------------------------------------------------------------------

    /**
     * Esquema de cores Material 3. Contem todos os roles de cor definidos pela especificacao M3.
     *
     * <p>Aceder via {@link BricksTheme#colorScheme()}.</p>
     */
    public class ColorScheme {

        private Color primary             = Color.web("#65558F");
        private Color onPrimary           = Color.web("#FFFFFF");
        private Color primaryContainer    = Color.web("#EADDFF");
        private Color onPrimaryContainer  = Color.web("#4F378B");

        private Color secondary           = Color.web("#625B71");
        private Color onSecondary         = Color.web("#FFFFFF");
        private Color secondaryContainer  = Color.web("#E8DEF8");
        private Color onSecondaryContainer = Color.web("#4A4458");

        private Color tertiary            = Color.web("#7D5260");
        private Color onTertiary          = Color.web("#FFFFFF");
        private Color tertiaryContainer   = Color.web("#FFD8E4");
        private Color onTertiaryContainer = Color.web("#633B48");

        private Color error               = Color.web("#B3261E");
        private Color onError             = Color.web("#FFFFFF");
        private Color errorContainer      = Color.web("#F9DEDC");
        private Color onErrorContainer    = Color.web("#8C1D18");

        private Color background          = Color.web("#FEF7FF");
        private Color onBackground        = Color.web("#1D1B20");
        private Color surface             = Color.web("#FEF7FF");
        private Color onSurface           = Color.web("#1D1B20");
        private Color surfaceVariant      = Color.web("#E7E0EC");
        private Color onSurfaceVariant    = Color.web("#49454F");
        private Color surfaceContainer    = Color.web("#F3EDF7");
        private Color surfaceContainerHigh = Color.web("#ECE6F0");
        private Color surfaceContainerHighest = Color.web("#E6E0E9");

        private Color outline             = Color.web("#79747E");
        private Color outlineVariant      = Color.web("#CAC4D0");

        private Color inverseSurface      = Color.web("#322F35");
        private Color inverseOnSurface    = Color.web("#F5EFF7");
        private Color inversePrimary      = Color.web("#D0BCFF");

        /** @return a cor primaria da marca — usada em botoes e elementos de destaque */
        public Color primary() { return primary; }
        /** @param c {@code Color} — cor primaria */
        public ColorScheme primary(Color c) { this.primary = c; return this; }

        /** @return a cor do conteudo sobre a cor primaria */
        public Color onPrimary() { return onPrimary; }
        /** @param c {@code Color} — cor do conteudo sobre primary */
        public ColorScheme onPrimary(Color c) { this.onPrimary = c; return this; }

        /** @return o container da cor primaria (fundo de elementos de destaque medio) */
        public Color primaryContainer() { return primaryContainer; }
        /** @param c {@code Color} — cor do container primario */
        public ColorScheme primaryContainer(Color c) { this.primaryContainer = c; return this; }

        /** @return a cor do conteudo sobre o container primario */
        public Color onPrimaryContainer() { return onPrimaryContainer; }
        /** @param c {@code Color} — cor do conteudo sobre primaryContainer */
        public ColorScheme onPrimaryContainer(Color c) { this.onPrimaryContainer = c; return this; }

        /** @return a cor secundaria — usada em elementos menos prominentes */
        public Color secondary() { return secondary; }
        /** @param c {@code Color} — cor secundaria */
        public ColorScheme secondary(Color c) { this.secondary = c; return this; }

        /** @return a cor do conteudo sobre a cor secundaria */
        public Color onSecondary() { return onSecondary; }
        /** @param c {@code Color} — cor do conteudo sobre secondary */
        public ColorScheme onSecondary(Color c) { this.onSecondary = c; return this; }

        /** @return o container da cor secundaria */
        public Color secondaryContainer() { return secondaryContainer; }
        /** @param c {@code Color} — cor do container secundario */
        public ColorScheme secondaryContainer(Color c) { this.secondaryContainer = c; return this; }

        /** @return a cor do conteudo sobre o container secundario */
        public Color onSecondaryContainer() { return onSecondaryContainer; }
        /** @param c {@code Color} — cor do conteudo sobre secondaryContainer */
        public ColorScheme onSecondaryContainer(Color c) { this.onSecondaryContainer = c; return this; }

        /** @return a cor terciaria — usada para acentos contrastantes */
        public Color tertiary() { return tertiary; }
        /** @param c {@code Color} — cor terciaria */
        public ColorScheme tertiary(Color c) { this.tertiary = c; return this; }

        /** @return a cor do conteudo sobre a cor terciaria */
        public Color onTertiary() { return onTertiary; }
        /** @param c {@code Color} — cor do conteudo sobre tertiary */
        public ColorScheme onTertiary(Color c) { this.onTertiary = c; return this; }

        /** @return o container da cor terciaria */
        public Color tertiaryContainer() { return tertiaryContainer; }
        /** @param c {@code Color} — cor do container terciario */
        public ColorScheme tertiaryContainer(Color c) { this.tertiaryContainer = c; return this; }

        /** @return a cor do conteudo sobre o container terciario */
        public Color onTertiaryContainer() { return onTertiaryContainer; }
        /** @param c {@code Color} — cor do conteudo sobre tertiaryContainer */
        public ColorScheme onTertiaryContainer(Color c) { this.onTertiaryContainer = c; return this; }

        /** @return a cor de erro */
        public Color error() { return error; }
        /** @param c {@code Color} — cor de erro */
        public ColorScheme error(Color c) { this.error = c; return this; }

        /** @return a cor do conteudo sobre a cor de erro */
        public Color onError() { return onError; }
        /** @param c {@code Color} — cor do conteudo sobre error */
        public ColorScheme onError(Color c) { this.onError = c; return this; }

        /** @return o container da cor de erro */
        public Color errorContainer() { return errorContainer; }
        /** @param c {@code Color} — cor do container de erro */
        public ColorScheme errorContainer(Color c) { this.errorContainer = c; return this; }

        /** @return a cor do conteudo sobre o container de erro */
        public Color onErrorContainer() { return onErrorContainer; }
        /** @param c {@code Color} — cor do conteudo sobre errorContainer */
        public ColorScheme onErrorContainer(Color c) { this.onErrorContainer = c; return this; }

        /** @return a cor de fundo da aplicacao */
        public Color background() { return background; }
        /** @param c {@code Color} — cor de fundo */
        public ColorScheme background(Color c) { this.background = c; return this; }

        /** @return a cor do conteudo sobre o fundo */
        public Color onBackground() { return onBackground; }
        /** @param c {@code Color} — cor do conteudo sobre background */
        public ColorScheme onBackground(Color c) { this.onBackground = c; return this; }

        /** @return a cor de superficie dos componentes (cards, dialogs, etc.) */
        public Color surface() { return surface; }
        /** @param c {@code Color} — cor de superficie */
        public ColorScheme surface(Color c) { this.surface = c; return this; }

        /** @return a cor do conteudo sobre a superficie */
        public Color onSurface() { return onSurface; }
        /** @param c {@code Color} — cor do conteudo sobre surface */
        public ColorScheme onSurface(Color c) { this.onSurface = c; return this; }

        /** @return a variante da cor de superficie */
        public Color surfaceVariant() { return surfaceVariant; }
        /** @param c {@code Color} — variante da superficie */
        public ColorScheme surfaceVariant(Color c) { this.surfaceVariant = c; return this; }

        /** @return a cor do conteudo sobre a variante de superficie */
        public Color onSurfaceVariant() { return onSurfaceVariant; }
        /** @param c {@code Color} — cor do conteudo sobre surfaceVariant */
        public ColorScheme onSurfaceVariant(Color c) { this.onSurfaceVariant = c; return this; }

        /** @return a cor do container de superficie */
        public Color surfaceContainer() { return surfaceContainer; }
        /** @param c {@code Color} — cor do container de superficie */
        public ColorScheme surfaceContainer(Color c) { this.surfaceContainer = c; return this; }

        /** @return a cor do container de superficie alto */
        public Color surfaceContainerHigh() { return surfaceContainerHigh; }
        /** @param c {@code Color} — cor do container de superficie alto */
        public ColorScheme surfaceContainerHigh(Color c) { this.surfaceContainerHigh = c; return this; }

        /** @return a cor do container de superficie mais alto */
        public Color surfaceContainerHighest() { return surfaceContainerHighest; }
        /** @param c {@code Color} — cor do container de superficie mais alto */
        public ColorScheme surfaceContainerHighest(Color c) { this.surfaceContainerHighest = c; return this; }

        /** @return a cor de contorno (borders, dividers) */
        public Color outline() { return outline; }
        /** @param c {@code Color} — cor de contorno */
        public ColorScheme outline(Color c) { this.outline = c; return this; }

        /** @return a variante da cor de contorno */
        public Color outlineVariant() { return outlineVariant; }
        /** @param c {@code Color} — variante do contorno */
        public ColorScheme outlineVariant(Color c) { this.outlineVariant = c; return this; }

        /** @return a cor de superficie inversa */
        public Color inverseSurface() { return inverseSurface; }
        /** @param c {@code Color} — cor de superficie inversa */
        public ColorScheme inverseSurface(Color c) { this.inverseSurface = c; return this; }

        /** @return a cor do conteudo sobre a superficie inversa */
        public Color inverseOnSurface() { return inverseOnSurface; }
        /** @param c {@code Color} — cor do conteudo sobre inverseSurface */
        public ColorScheme inverseOnSurface(Color c) { this.inverseOnSurface = c; return this; }

        /** @return a cor primaria inversa */
        public Color inversePrimary() { return inversePrimary; }
        /** @param c {@code Color} — cor primaria inversa */
        public ColorScheme inversePrimary(Color c) { this.inversePrimary = c; return this; }

        /** Volta ao {@link BricksTheme} pai para continuar o encadeamento. */
        public BricksTheme and() { return BricksTheme.this; }
    }

    // -------------------------------------------------------------------------
    // Inner class: Typography
    // -------------------------------------------------------------------------

    /**
     * Escala tipografica Material 3.
     * Define tamanhos de fonte para cada papel na hierarquia visual.
     *
     * <p>Aceder via {@link BricksTheme#typography()}.</p>
     */
    public class Typography {

        private String fontFamily = "Roboto, Segoe UI, Arial";

        private double displayLarge  = 57;
        private double displayMedium = 45;
        private double displaySmall  = 36;

        private double headlineLarge  = 32;
        private double headlineMedium = 28;
        private double headlineSmall  = 24;

        private double titleLarge  = 22;
        private double titleMedium = 16;
        private double titleSmall  = 14;

        private double bodyLarge  = 16;
        private double bodyMedium = 14;
        private double bodySmall  = 12;

        private double labelLarge  = 14;
        private double labelMedium = 12;
        private double labelSmall  = 11;

        /** @return a familia de fontes usada em toda a aplicacao */
        public String fontFamily() { return fontFamily; }
        /** @param family {@code String} — nome da familia (ex: "Roboto", "Arial") */
        public Typography fontFamily(String family) { this.fontFamily = family; return this; }

        /** @return tamanho do display grande (57pt) — para titulos de ecra */
        public double displayLarge() { return displayLarge; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography displayLarge(double size) { this.displayLarge = size; return this; }

        /** @return tamanho do display medio (45pt) */
        public double displayMedium() { return displayMedium; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography displayMedium(double size) { this.displayMedium = size; return this; }

        /** @return tamanho do display pequeno (36pt) */
        public double displaySmall() { return displaySmall; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography displaySmall(double size) { this.displaySmall = size; return this; }

        /** @return tamanho do headline grande (32pt) — para titulos de secao */
        public double headlineLarge() { return headlineLarge; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography headlineLarge(double size) { this.headlineLarge = size; return this; }

        /** @return tamanho do headline medio (28pt) */
        public double headlineMedium() { return headlineMedium; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography headlineMedium(double size) { this.headlineMedium = size; return this; }

        /** @return tamanho do headline pequeno (24pt) */
        public double headlineSmall() { return headlineSmall; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography headlineSmall(double size) { this.headlineSmall = size; return this; }

        /** @return tamanho do titulo grande (22pt) — para titulos de card/dialog */
        public double titleLarge() { return titleLarge; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography titleLarge(double size) { this.titleLarge = size; return this; }

        /** @return tamanho do titulo medio (16pt) */
        public double titleMedium() { return titleMedium; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography titleMedium(double size) { this.titleMedium = size; return this; }

        /** @return tamanho do titulo pequeno (14pt) */
        public double titleSmall() { return titleSmall; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography titleSmall(double size) { this.titleSmall = size; return this; }

        /** @return tamanho do corpo grande (16pt) — para texto de leitura principal */
        public double bodyLarge() { return bodyLarge; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography bodyLarge(double size) { this.bodyLarge = size; return this; }

        /** @return tamanho do corpo medio (14pt) — para texto de leitura secundario */
        public double bodyMedium() { return bodyMedium; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography bodyMedium(double size) { this.bodyMedium = size; return this; }

        /** @return tamanho do corpo pequeno (12pt) — para texto de apoio */
        public double bodySmall() { return bodySmall; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography bodySmall(double size) { this.bodySmall = size; return this; }

        /** @return tamanho do label grande (14pt) — para labels de botao */
        public double labelLarge() { return labelLarge; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography labelLarge(double size) { this.labelLarge = size; return this; }

        /** @return tamanho do label medio (12pt) */
        public double labelMedium() { return labelMedium; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography labelMedium(double size) { this.labelMedium = size; return this; }

        /** @return tamanho do label pequeno (11pt) */
        public double labelSmall() { return labelSmall; }
        /** @param size {@code double} — tamanho em pontos */
        public Typography labelSmall(double size) { this.labelSmall = size; return this; }

        /** Volta ao {@link BricksTheme} pai para continuar o encadeamento. */
        public BricksTheme and() { return BricksTheme.this; }
    }

    // -------------------------------------------------------------------------
    // Inner class: Shapes
    // -------------------------------------------------------------------------

    /**
     * Escala de formas Material 3.
     * Define o raio dos cantos arredondados para cada tamanho de componente.
     *
     * <p>Aceder via {@link BricksTheme#shapes()}.</p>
     */
    public class Shapes {

        private double extraSmall = 4;   // TextField, Menu
        private double small      = 8;   // Chip
        private double medium     = 12;  // Card, Dialog
        private double large      = 16;  // Bottom sheet
        private double extraLarge = 24;  // FAB

        /** @return raio extra pequeno (4px) — para TextField, inputs */
        public double extraSmall() { return extraSmall; }
        /** @param radius {@code double} — raio dos cantos em pixels */
        public Shapes extraSmall(double radius) { this.extraSmall = radius; return this; }

        /** @return raio pequeno (8px) — para chips, badges */
        public double small() { return small; }
        /** @param radius {@code double} — raio dos cantos em pixels */
        public Shapes small(double radius) { this.small = radius; return this; }

        /** @return raio medio (12px) — para cards, dialogs */
        public double medium() { return medium; }
        /** @param radius {@code double} — raio dos cantos em pixels */
        public Shapes medium(double radius) { this.medium = radius; return this; }

        /** @return raio grande (16px) — para bottom sheets, menus */
        public double large() { return large; }
        /** @param radius {@code double} — raio dos cantos em pixels */
        public Shapes large(double radius) { this.large = radius; return this; }

        /** @return raio extra grande (24px) — para FAB */
        public double extraLarge() { return extraLarge; }
        /** @param radius {@code double} — raio dos cantos em pixels */
        public Shapes extraLarge(double radius) { this.extraLarge = radius; return this; }

        /** Volta ao {@link BricksTheme} pai para continuar o encadeamento. */
        public BricksTheme and() { return BricksTheme.this; }
    }

    // -------------------------------------------------------------------------
    // BricksTheme fields
    // -------------------------------------------------------------------------

    private final ColorScheme colorScheme = new ColorScheme();
    private final Typography typography   = new Typography();
    private final Shapes shapes           = new Shapes();

    private BricksTheme() {}

    /** @return o esquema de cores para personalizacao ou leitura de valores */
    public ColorScheme colorScheme() { return colorScheme; }

    /** @return a escala tipografica para personalizacao ou leitura de tamanhos */
    public Typography typography() { return typography; }

    /** @return a escala de formas para personalizacao ou leitura de raios */
    public Shapes shapes() { return shapes; }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /**
     * Cria o tema Material 3 light (padrao).
     * Usado automaticamente pela {@link fabiorodrigues.bricks.core.BricksApplication} se nao for definido outro.
     *
     * @return novo tema Material 3 light
     */
    public static BricksTheme material() {
        return new BricksTheme();
    }

    /**
     * Cria o tema Material 3 dark.
     *
     * @return novo tema Material 3 dark
     */
    public static BricksTheme dark() {
        BricksTheme t = new BricksTheme();
        t.colorScheme()
            .primary(Color.web("#D0BCFE"))
            .onPrimary(Color.web("#381E72"))
            .primaryContainer(Color.web("#4F378B"))
            .onPrimaryContainer(Color.web("#EADDFF"))
            .secondary(Color.web("#CCC2DC"))
            .onSecondary(Color.web("#332D41"))
            .secondaryContainer(Color.web("#4A4458"))
            .onSecondaryContainer(Color.web("#E8DEF8"))
            .tertiary(Color.web("#EFB8C8"))
            .onTertiary(Color.web("#492532"))
            .tertiaryContainer(Color.web("#633B48"))
            .onTertiaryContainer(Color.web("#FFD8E4"))
            .error(Color.web("#F2B8B5"))
            .onError(Color.web("#601410"))
            .errorContainer(Color.web("#8C1D18"))
            .onErrorContainer(Color.web("#F9DEDC"))
            .background(Color.web("#141218"))
            .onBackground(Color.web("#E6E0E9"))
            .surface(Color.web("#141218"))
            .onSurface(Color.web("#E6E0E9"))
            .surfaceVariant(Color.web("#49454F"))
            .onSurfaceVariant(Color.web("#CAC4D0"))
            .surfaceContainer(Color.web("#211F26"))
            .surfaceContainerHigh(Color.web("#2B2930"))
            .surfaceContainerHighest(Color.web("#36343B"))
            .outline(Color.web("#938F99"))
            .outlineVariant(Color.web("#49454F"))
            .inverseSurface(Color.web("#E6E0E9"))
            .inverseOnSurface(Color.web("#322F35"))
            .inversePrimary(Color.web("#6750A4"));
        return t;
    }

    // -------------------------------------------------------------------------
    // Acesso ao tema atual
    // -------------------------------------------------------------------------

    /**
     * Devolve o tema atualmente ativo na aplicacao.
     * Equivalente ao {@code MaterialTheme} no Jetpack Compose.
     *
     * <pre>{@code
     * new Modifier().textColor(BricksTheme.current().colorScheme().primary())
     * }</pre>
     *
     * @return o tema atual (nunca null — devolve material() se nenhum foi definido)
     */
    public static BricksTheme current() {
        return ThemeRegistry.current();
    }

    // -------------------------------------------------------------------------
    // CSS generation
    // -------------------------------------------------------------------------

    /**
     * Gera o CSS completo para este tema.
     * Usado internamente por {@link fabiorodrigues.bricks.core.BricksApplication}.
     *
     * @return {@code String} — CSS pronto a aplicar a uma Scene JavaFX
     */
    public String toCss() {
        ColorScheme c = colorScheme;
        Typography t = typography;
        Shapes s = shapes;

        return """
            .root {
                -fx-font-family: "%s";
                -fx-font-size: %.0fpx;
                -fx-background-color: %s;
            }

            /* ---- Button ---- */
            .button.bricks-button {
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-size: %.0fpx;
                -fx-font-weight: bold;
                -fx-background-radius: %.0f;
                -fx-border-radius: %.0f;
                -fx-padding: 10 24 10 24;
                -fx-cursor: hand;
            }
            .button.bricks-button:hover {
                -fx-background-color: %s;
            }
            .button.bricks-button:pressed {
                -fx-background-color: %s;
            }
            .button.bricks-button:disabled {
                -fx-opacity: 0.38;
            }

            /* ---- Text ---- */
            .label.bricks-text {
                -fx-text-fill: %s;
                -fx-font-size: %.0fpx;
            }

            /* ---- TextField ---- */
            .text-field.bricks-text-field {
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-prompt-text-fill: %s;
                -fx-border-color: %s;
                -fx-border-width: 1;
                -fx-border-radius: %.0f;
                -fx-background-radius: %.0f;
                -fx-font-size: %.0fpx;
                -fx-padding: 8 12 8 12;
            }
            .text-field.bricks-text-field:focused {
                -fx-border-color: %s;
                -fx-border-width: 2;
            }

            /* ---- TextArea ---- */
            .text-area.bricks-text-area {
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-prompt-text-fill: %s;
                -fx-border-color: %s;
                -fx-border-width: 1;
                -fx-border-radius: %.0f;
                -fx-background-radius: %.0f;
                -fx-font-size: %.0fpx;
            }
            .text-area.bricks-text-area:focused {
                -fx-border-color: %s;
                -fx-border-width: 2;
            }
            .text-area.bricks-text-area .content {
                -fx-background-color: %s;
            }

            /* ---- Checkbox ---- */
            .check-box.bricks-checkbox {
                -fx-text-fill: %s;
                -fx-font-size: %.0fpx;
            }
            .check-box.bricks-checkbox .box {
                -fx-background-color: transparent;
                -fx-border-color: %s;
                -fx-border-radius: 2;
                -fx-border-width: 2;
            }
            .check-box.bricks-checkbox:selected .box {
                -fx-background-color: %s;
                -fx-border-color: %s;
            }
            .check-box.bricks-checkbox:selected .mark {
                -fx-background-color: %s;
            }

            /* ---- Dropdown (ComboBox) ---- */
            .combo-box.bricks-dropdown {
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-radius: %.0f;
                -fx-background-radius: %.0f;
                -fx-font-size: %.0fpx;
            }
            .combo-box.bricks-dropdown .list-cell {
                -fx-text-fill: %s;
            }

            /* ---- Slider ---- */
            .slider.bricks-slider .track {
                -fx-background-color: %s;
                -fx-pref-height: 4px;
                -fx-background-radius: 2;
            }
            .slider.bricks-slider .thumb {
                -fx-background-color: %s;
                -fx-background-radius: 50%%;
                -fx-pref-width: 20px;
                -fx-pref-height: 20px;
            }

            /* ---- ProgressBar ---- */
            .progress-bar.bricks-progress-bar .bar {
                -fx-background-color: %s;
                -fx-background-radius: 4;
            }
            .progress-bar.bricks-progress-bar .track {
                -fx-background-color: %s;
                -fx-background-radius: 4;
            }

            /* ---- Divider (Separator) ---- */
            .separator.bricks-divider .line {
                -fx-border-color: %s;
                -fx-border-width: 1;
            }

            /* ---- ScrollView ---- */
            .scroll-pane.bricks-scroll-view {
                -fx-background-color: transparent;
                -fx-background: transparent;
                -fx-border-color: transparent;
            }
            .scroll-pane.bricks-scroll-view > .viewport {
                -fx-background-color: transparent;
            }
            """.formatted(
                // root
                t.fontFamily(), t.bodyMedium(), toHex(c.background()),
                // button
                toHex(c.primary()), toHex(c.onPrimary()), t.labelLarge(),
                s.extraSmall(), s.extraSmall(),
                toHex(darken(c.primary(), 0.1)), toHex(darken(c.primary(), 0.2)),
                // text
                toHex(c.onBackground()), t.bodyMedium(),
                // text-field
                toHex(c.surface()), toHex(c.onSurface()), toHex(c.onSurfaceVariant()),
                toHex(c.outline()), s.extraSmall(), s.extraSmall(), t.bodyLarge(),
                toHex(c.primary()),
                // text-area
                toHex(c.surface()), toHex(c.onSurface()), toHex(c.onSurfaceVariant()),
                toHex(c.outline()), s.extraSmall(), s.extraSmall(), t.bodyLarge(),
                toHex(c.primary()), toHex(c.surface()),
                // checkbox
                toHex(c.onBackground()), t.bodyMedium(),
                toHex(c.outline()), toHex(c.primary()), toHex(c.primary()), toHex(c.onPrimary()),
                // dropdown
                toHex(c.surface()), toHex(c.outline()), s.extraSmall(), s.extraSmall(), t.bodyMedium(),
                toHex(c.onSurface()),
                // slider
                toHex(c.outlineVariant()), toHex(c.primary()),
                // progress-bar
                toHex(c.primary()), toHex(c.surfaceVariant()),
                // divider
                toHex(c.outlineVariant())
            );
    }

    /**
     * Codifica o CSS para uso como URI na Scene.
     * Usado internamente por {@link fabiorodrigues.bricks.core.BricksApplication}.
     *
     * @return {@code String} — URI no formato {@code data:text/css,...}
     */
    public String toDataUri() {
        String encoded = URLEncoder.encode(toCss(), StandardCharsets.UTF_8)
            .replace("+", "%20");
        return "data:text/css," + encoded;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed()   * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue()  * 255));
    }

    private static Color darken(Color color, double factor) {
        return new Color(
            Math.max(0, color.getRed()   - factor),
            Math.max(0, color.getGreen() - factor),
            Math.max(0, color.getBlue()  - factor),
            color.getOpacity()
        );
    }
}
