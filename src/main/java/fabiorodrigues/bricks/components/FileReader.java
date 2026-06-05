package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

/**
 * Le o conteudo de um ficheiro selecionado pelo {@link FilePicker}.
 * Suporta texto simples e PDF (requer PDFBox no classpath).
 * Ficheiros de tipo nao suportado disparam o callback {@link #onUnsupported(Consumer)}.
 *
 * <p>O componente nao tem representacao visual — devolve um {@link Region} invisivel
 * e nao gerido. E um componente logico, nao visual. Deve ser incluido no {@code root()}
 * ou {@code render()} da scene para que o listener interno seja ligado.</p>
 *
 * <p><b>Importante:</b> manter a instancia como field da aplicacao ou scene. Criar um
 * novo {@code FileReader} em cada re-render acumula listeners no {@link State} ligado.</p>
 *
 * <pre>{@code
 * private final State<File> ficheiro = state(null);
 * private final FileReader leitor = new FileReader()
 *     .bindTo(ficheiro)
 *     .onText(conteudo -> texto.set(conteudo))
 *     .onPdf(conteudo -> texto.set(conteudo))
 *     .onUnsupported(file -> Alert.warning("Aviso", "Formato nao suportado: " + file.getName()));
 *
 * @Override
 * public Component root() {
 *     return new Column().children(
 *         new FilePicker().bindTo(ficheiro),
 *         leitor
 *     );
 * }
 * }</pre>
 */
public class FileReader implements Component {

    private static final List<String> EXTENSOES_TEXTO = List.of(
        ".txt", ".md", ".csv", ".json", ".xml",
        ".java", ".html", ".css", ".js", ".ts", ".py"
    );

    private State<File> boundFile = null;
    private Consumer<String> onText = null;
    private Consumer<String> onPdf = null;
    private Consumer<String> onContent = null;
    private Consumer<File> onUnsupported = null;
    private Consumer<Exception> onError = null;
    private boolean listenerRegistered = false;

    /**
     * Liga o leitor a um {@link State} de {@link File} — tipicamente proveniente
     * de um {@link FilePicker}. Quando o ficheiro muda, le automaticamente o conteudo.
     *
     * @param fileState {@code State<File>} — estado a observar
     * @return este componente para encadeamento
     */
    public FileReader bindTo(State<File> fileState) {
        this.boundFile = fileState;
        return this;
    }

    /**
     * Callback chamado quando o ficheiro e de texto simples.
     * Recebe o conteudo como {@link String}.
     *
     * @param callback {@code Consumer<String>} — recebe o conteudo lido
     * @return este componente para encadeamento
     */
    public FileReader onText(Consumer<String> callback) {
        this.onText = callback;
        return this;
    }

    /**
     * Callback chamado quando o ficheiro e PDF.
     * Recebe o texto extraido como {@link String}.
     *
     * <p>Requer PDFBox no classpath do projeto que consome a lib:</p>
     * <pre>{@code
     * <dependency>
     *     <groupId>org.apache.pdfbox</groupId>
     *     <artifactId>pdfbox</artifactId>
     *     <version>3.0.1</version>
     * </dependency>
     * }</pre>
     *
     * @param callback {@code Consumer<String>} — recebe o texto extraido do PDF
     * @return este componente para encadeamento
     */
    public FileReader onPdf(Consumer<String> callback) {
        this.onPdf = callback;
        return this;
    }

    /**
     * Callback unico para qualquer tipo suportado.
     * Alternativa a usar {@link #onText(Consumer)} e {@link #onPdf(Consumer)} em separado.
     *
     * @param callback {@code Consumer<String>} — recebe o conteudo lido
     * @return este componente para encadeamento
     */
    public FileReader onContent(Consumer<String> callback) {
        this.onContent = callback;
        return this;
    }

    /**
     * Callback chamado quando o ficheiro nao e de um tipo suportado
     * (extensao nao reconhecida ou PDF sem PDFBox no classpath).
     *
     * @param callback {@code Consumer<File>} — recebe o {@link File} para o utilizador
     *                  poder mostrar mensagem com o nome
     * @return este componente para encadeamento
     */
    public FileReader onUnsupported(Consumer<File> callback) {
        this.onUnsupported = callback;
        return this;
    }

    /**
     * Callback chamado quando ocorre erro ao ler o ficheiro.
     * Se nao definido, o erro e ignorado silenciosamente.
     *
     * @param callback {@code Consumer<Exception>} — recebe a excecao capturada
     * @return este componente para encadeamento
     */
    public FileReader onError(Consumer<Exception> callback) {
        this.onError = callback;
        return this;
    }

    @Override
    public Node render() {
        if (boundFile != null && !listenerRegistered) {
            listenerRegistered = true;
            boundFile.addListener(() -> {
                File file = boundFile.get();
                if (file != null) {
                    lerFicheiro(file);
                }
            });
        }
        Region invisible = new Region();
        invisible.setManaged(false);
        invisible.setVisible(false);
        return invisible;
    }

    private void lerFicheiro(File file) {
        String nome = file.getName().toLowerCase();

        try {
            if (EXTENSOES_TEXTO.stream().anyMatch(nome::endsWith)) {
                String conteudo = Files.readString(file.toPath());
                if (onText != null) onText.accept(conteudo);
                if (onContent != null) onContent.accept(conteudo);
                return;
            }

            if (nome.endsWith(".pdf")) {
                String conteudo = lerPdf(file);
                if (conteudo != null) {
                    if (onPdf != null) onPdf.accept(conteudo);
                    if (onContent != null) onContent.accept(conteudo);
                    return;
                }
                return;
            }

            if (onUnsupported != null) onUnsupported.accept(file);

        } catch (Exception e) {
            if (onError != null) onError.accept(e);
        }
    }

    /**
     * Le o conteudo de um PDF via PDFBox 3.x usando reflexao.
     * Devolve {@code null} se PDFBox nao estiver no classpath
     * (e dispara {@link #onUnsupported} para sinalizar ao utilizador).
     */
    private String lerPdf(File file) {
        try {
            Class<?> loaderClass     = Class.forName("org.apache.pdfbox.Loader");
            Class<?> pdDocumentClass = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
            Class<?> stripperClass   = Class.forName("org.apache.pdfbox.text.PDFTextStripper");

            Object doc      = loaderClass.getMethod("loadPDF", File.class).invoke(null, file);
            Object stripper = stripperClass.getDeclaredConstructor().newInstance();

            String texto = (String) stripperClass.getMethod("getText", pdDocumentClass)
                .invoke(stripper, doc);

            pdDocumentClass.getMethod("close").invoke(doc);

            return texto;

        } catch (ClassNotFoundException e) {
            if (onUnsupported != null) onUnsupported.accept(file);
            return null;
        } catch (Exception e) {
            if (onError != null) onError.accept(e);
            return null;
        }
    }
}
