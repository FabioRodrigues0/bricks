package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.VBox;

/**
 * Lista de opcoes. Renderiza como um {@link ComboBox} JavaFX.
 *
 * <pre>{@code
 * new Dropdown<>(List.of("Opcao A", "Opcao B", "Opcao C"))
 *     .selected("Opcao A")
 *     .onChange(opcao -> System.out.println("Selecionado: " + opcao))
 * }</pre>
 *
 * <p>Com label e binding:</p>
 * <pre>{@code
 * State<String> pais = state("Portugal");
 * new Dropdown<>(List.of("Portugal", "Brasil", "Angola"))
 *     .label("Pais:")
 *     .bindTo(pais)
 * }</pre>
 *
 * @param <T> o tipo dos items da lista
 */
public class Dropdown<T> implements Component {

    private final List<T> options;
    private T selected;
    private String label;
    private Modifier modifier;
    private Consumer<T> onChange;
    private State<T> boundState;

    public Dropdown(List<T> options) {
        this.options = options;
    }

    public Dropdown<T> selected(T selected) {
        this.selected = selected;
        return this;
    }

    public Dropdown<T> label(String label) {
        this.label = label;
        return this;
    }

    public Dropdown<T> bindTo(State<T> state) {
        this.boundState = state;
        return this;
    }

    public Dropdown<T> onChange(Consumer<T> callback) {
        this.onChange = callback;
        return this;
    }

    public Dropdown<T> modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("bricks-dropdown");
        comboBox.getItems().addAll(options);

        T initialValue = boundState != null ? boundState.get() : selected;
        if (initialValue != null) {
            comboBox.setValue(initialValue);
        } else if (!options.isEmpty()) {
            comboBox.setValue(options.get(0));
        }

        // onShown dispara depois do popup estar completamente inicializado
        // propaga as stylesheets da Scene principal para o popup
        comboBox.setOnShown(e -> {
            if (comboBox.getSkin() instanceof ComboBoxListViewSkin<?> skin) {
                Node popupContent = skin.getPopupContent();
                if (
                    popupContent != null &&
                    popupContent.getScene() != null &&
                    comboBox.getScene() != null
                ) {
                    popupContent
                        .getScene()
                        .getStylesheets()
                        .setAll(comboBox.getScene().getStylesheets());
                }
            }
        });

        // Células do popup
        comboBox.setCellFactory(lv ->
            new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            }
        );

        // Célula do botão
        comboBox.setButtonCell(
            new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            }
        );

        if (boundState != null) {
            comboBox.valueProperty().addListener((obs, oldVal, newVal) -> boundState.set(newVal));
        }

        if (onChange != null) {
            comboBox.valueProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
        }

        if (modifier != null) {
            modifier.applyTo(comboBox);
        }

        if (label != null) {
            Label labelNode = new Label(label);
            VBox container = new VBox(4, labelNode, comboBox);
            container.setFillWidth(true);
            return container;
        }

        return comboBox;
    }
}
