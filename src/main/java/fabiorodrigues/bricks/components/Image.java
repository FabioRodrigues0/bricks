package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.BricksPaths;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 * Componente de imagem. Renderiza como um {@link ImageView} JavaFX.
 *
 * <p>A partir de URL ou caminho de ficheiro:</p>
 * <pre>{@code
 * new Image("https://exemplo.com/foto.png")
 * new Image("file:/caminho/para/foto.png")
 * new Image("/imagens/logo.png") // recurso no classpath
 * new Image("clientes/1/foto.png").userData() // relativo a setPathUserData(...)
 * }</pre>
 *
 * <p>Com dimensoes definidas:</p>
 * <pre>{@code
 * new Image("/logo.png").width(200).height(100)
 * new Image("/avatar.png").size(64) // quadrado
 * }</pre>
 */
public class Image implements Component {

    private final String url;
    private double width = -1;
    private double height = -1;
    private boolean preserveRatio = true;
    private Boolean backgroundLoading;
    private boolean userData;
    private Modifier modifier;

    /**
     * Cria uma imagem a partir de um URL ou caminho.
     *
     * @param url o URL da imagem (http://, file:// ou caminho de recurso /imagem.png)
     */
    public Image(String url) {
        this.url = url;
    }

    /**
     * Cria uma imagem relativa a pasta configurada por
     * {@link fabiorodrigues.bricks.core.BricksApplication#setPathUserData(String)}.
     *
     * @param path caminho relativo a user data ou caminho absoluto
     * @return componente de imagem
     */
    public static Image userData(String path) {
        return new Image(path).userData();
    }

    /**
     * Define a largura da imagem em pixels.
     *
     * @param width largura em pixels
     * @return este componente para encadeamento
     */
    public Image width(double width) {
        this.width = width;
        return this;
    }

    /**
     * Define a altura da imagem em pixels.
     *
     * @param height altura em pixels
     * @return este componente para encadeamento
     */
    public Image height(double height) {
        this.height = height;
        return this;
    }

    /**
     * Define largura e altura iguais (imagem quadrada).
     *
     * @param size tamanho em pixels
     * @return este componente para encadeamento
     */
    public Image size(double size) {
        this.width = size;
        this.height = size;
        return this;
    }

    /**
     * Define se a proporcao original da imagem e preservada ao redimensionar (por defeito: true).
     *
     * @param preserve true para preservar proporcao
     * @return este componente para encadeamento
     */
    public Image preserveRatio(boolean preserve) {
        this.preserveRatio = preserve;
        return this;
    }

    /**
     * Define se a imagem deve carregar em background.
     *
     * <p>Quando nao definido, apenas URLs remotos http/https carregam em background;
     * recursos locais carregam de forma sincrona para evitar flicker.</p>
     *
     * @param backgroundLoading true para carregar em background
     * @return este componente para encadeamento
     */
    public Image backgroundLoading(boolean backgroundLoading) {
        this.backgroundLoading = backgroundLoading;
        return this;
    }

    /**
     * Interpreta o caminho desta imagem como relativo a pasta de user data.
     * Se o caminho for absoluto, e usado diretamente.
     *
     * <pre>{@code
     * new Image("clientes/1/foto.png").userData()
     * Image.userData("clientes/1/foto.png")
     * }</pre>
     *
     * @return este componente para encadeamento
     */
    public Image userData() {
        this.userData = true;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Image modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        String resolvedUrl = resolveUrl();
        boolean loadInBackground = backgroundLoading != null
            ? backgroundLoading
            : isRemoteUrl(url);

        javafx.scene.image.Image img = new javafx.scene.image.Image(resolvedUrl, loadInBackground);
        ImageView view = new ImageView(img);
        view.setPreserveRatio(preserveRatio);

        if (width >= 0) view.setFitWidth(width);
        if (height >= 0) view.setFitHeight(height);

        return view;
    }

    private String resolveUrl() {
        if (userData) {
            return BricksPaths.resolveUserData(url).toUri().toString();
        }

        if (!url.startsWith("/")) {
            return url;
        }

        java.net.URL resource = getClass().getResource(url);
        if (resource == null) {
            throw new IllegalArgumentException("Imagem nao encontrada no classpath: " + url);
        }
        return resource.toExternalForm();
    }

    private boolean isRemoteUrl(String value) {
        String normalized = value.toLowerCase();
        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }
}
