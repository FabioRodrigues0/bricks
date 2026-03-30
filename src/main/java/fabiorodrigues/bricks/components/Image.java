package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
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
        String resolvedUrl = url.startsWith("/")
            ? getClass().getResource(url).toExternalForm()
            : url;

        javafx.scene.image.Image img = new javafx.scene.image.Image(resolvedUrl, true);
        ImageView view = new ImageView(img);
        view.setPreserveRatio(preserveRatio);

        if (width >= 0) view.setFitWidth(width);
        if (height >= 0) view.setFitHeight(height);

        return view;
    }
}
