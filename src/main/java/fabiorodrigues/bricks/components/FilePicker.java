package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Componente que abre um dialogo de selecao de ficheiro.
 * Renderiza como um {@link javafx.scene.control.Button} que ao ser clicado abre o explorador de ficheiros.
 *
 * <pre>{@code
 * new FilePicker()
 *     .title("Escolhe uma imagem")
 *     .filter("Imagens", "*.png", "*.jpg", "*.jpeg")
 *     .onSelect(file -> caminho.set(file.getAbsolutePath()))
 * }</pre>
 *
 * <p>Com binding a um {@link State}:</p>
 * <pre>{@code
 * State<File> ficheiro = state(null);
 * new FilePicker().bindTo(ficheiro)
 * }</pre>
 */
public class FilePicker implements Component {

    private String buttonLabel = "Escolher ficheiro...";
    private String dialogTitle = "Selecionar ficheiro";
    private FileChooser.ExtensionFilter[] filters;
    private Consumer<File> onSelect;
    private State<File> boundState;
    private Function<File, String> saveTo = null;
    private Modifier modifier;

    /**
     * Define o texto do botao que abre o dialogo.
     *
     * @param label {@code String} — texto do botao (ex: "Escolher imagem...")
     * @return este componente para encadeamento
     */
    public FilePicker label(String label) {
        this.buttonLabel = label;
        return this;
    }

    /**
     * Define o titulo da janela do dialogo de selecao.
     *
     * @param title {@code String} — titulo da janela (ex: "Abrir ficheiro")
     * @return este componente para encadeamento
     */
    public FilePicker title(String title) {
        this.dialogTitle = title;
        return this;
    }

    /**
     * Adiciona um filtro de extensoes de ficheiro ao dialogo.
     *
     * <pre>{@code
     * .filter("Imagens", "*.png", "*.jpg", "*.jpeg")
     * .filter("PDFs", "*.pdf")
     * }</pre>
     *
     * @param description {@code String} — descricao do filtro (ex: "Imagens")
     * @param extensions {@code String...} — extensoes permitidas (ex: "*.png", "*.jpg")
     * @return este componente para encadeamento
     */
    public FilePicker filter(String description, String... extensions) {
        if (filters == null) {
            this.filters = new FileChooser.ExtensionFilter[]{
                new FileChooser.ExtensionFilter(description, extensions)
            };
        } else {
            FileChooser.ExtensionFilter[] newFilters = new FileChooser.ExtensionFilter[filters.length + 1];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            newFilters[filters.length] = new FileChooser.ExtensionFilter(description, extensions);
            this.filters = newFilters;
        }
        return this;
    }

    /**
     * Define um callback chamado com o ficheiro selecionado.
     * Nao e chamado se o utilizador cancelar o dialogo.
     *
     * @param callback {@code Consumer<File>} — funcao que recebe o {@link File} selecionado
     * @return este componente para encadeamento
     */
    public FilePicker onSelect(Consumer<File> callback) {
        this.onSelect = callback;
        return this;
    }

    /**
     * Liga o ficheiro selecionado a um {@link State}.
     * Atualiza o state com o ficheiro escolhido (ou null se cancelar).
     *
     * @param state {@code State<File>} — state onde guardar o ficheiro selecionado
     * @return este componente para encadeamento
     */
    public FilePicker bindTo(State<File> state) {
        this.boundState = state;
        return this;
    }

    /**
     * Define uma funcao que calcula o caminho de destino onde o ficheiro sera guardado.
     * A pasta e criada automaticamente se nao existir.
     * O callback onSelect recebe o ficheiro destino em vez do original.
     *
     * <pre>{@code
     * .saveTo(file -> "veiculos/" + id + "/" + file.getName())
     * .saveTo(file -> "documentos/" + tipo + "/" + file.getName())
     * }</pre>
     *
     * @param pathFunction funcao que recebe o ficheiro original e devolve o caminho destino
     * @return este componente para encadeamento
     */
    public FilePicker saveTo(Function<File, String> pathFunction) {
        this.saveTo = pathFunction;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier {@code Modifier} — o modifier a aplicar ao botao
     * @return este componente para encadeamento
     */
    public FilePicker modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        javafx.scene.control.Button btn = new javafx.scene.control.Button(buttonLabel);
        btn.getStyleClass().add("bricks-button");

        btn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle(dialogTitle);
            if (filters != null) {
                chooser.getExtensionFilters().addAll(filters);
            }
            File selected = chooser.showOpenDialog(btn.getScene() != null ? btn.getScene().getWindow() : null);
            if (selected != null) {
                File ficheiro = selected;

                // Copiar para destino se saveTo estiver definido
                if (saveTo != null) {
                    try {
                        String caminhoDestino = saveTo.apply(selected);
                        Path destino = Path.of(caminhoDestino);
                        Files.createDirectories(destino.getParent());
                        Files.copy(selected.toPath(), destino,
                            StandardCopyOption.REPLACE_EXISTING);
                        ficheiro = destino.toFile();
                    } catch (Exception ex) {
                        System.err.println("[FilePicker] Erro ao guardar ficheiro: "
                            + ex.getMessage());
                        // Continua com o ficheiro original se falhar
                    }
                }

                if (boundState != null) {
                    boundState.set(ficheiro);
                }
                if (onSelect != null) {
                    onSelect.accept(ficheiro);
                }
            }
        });

        if (modifier != null) {
            modifier.applyTo(btn);
        }

        return btn;
    }
}
