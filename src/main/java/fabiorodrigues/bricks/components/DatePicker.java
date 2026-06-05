package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.VBox;

/**
 * Seletor de data com popup de calendario nativo do JavaFX.
 * Com {@link #time()} usa LocalDateTime em vez de LocalDate.
 *
 * <pre>{@code
 * // So data
 * State<LocalDate> data = state(LocalDate.now());
 * new DatePicker().label("Data:").bindTo(data)
 *
 * // Data + hora
 * State<LocalDateTime> dataHora = state(LocalDateTime.now());
 * new DatePicker().time().bindTo(dataHora)
 * }</pre>
 */
public class DatePicker implements Component {

    private String label = null;
    private String placeholder = null;
    private boolean withTime = false;
    private State<?> boundState = null;
    private Modifier modifier;

    public DatePicker label(String label) {
        this.label = label;
        return this;
    }

    public DatePicker placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Ativa o modo data + hora. O bind deve ser a State&lt;LocalDateTime&gt;.
     */
    public DatePicker time() {
        this.withTime = true;
        return this;
    }

    /**
     * Liga o picker a um State&lt;LocalDate&gt; ou State&lt;LocalDateTime&gt;.
     * Com {@link #time()}, o valor guardado e LocalDateTime.
     *
     * @param state o state a ligar
     * @return este componente para encadeamento
     */
    public <T> DatePicker bindTo(State<T> state) {
        this.boundState = state;
        if (state != null && state.get() instanceof LocalDateTime) {
            this.withTime = true;
        }
        return this;
    }

    public DatePicker modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        javafx.scene.control.DatePicker picker = new javafx.scene.control.DatePicker();
        picker.getStyleClass().add("bricks-date-picker");

        if (placeholder != null) {
            picker.setPromptText(placeholder);
        }

        if (boundState != null) {
            bindPicker(picker);
        }

        picker.setOnShown(e -> Platform.runLater(() -> applyThemeToPopup(picker)));

        if (modifier != null) {
            modifier.applyTo(picker);
        }

        if (label != null) {
            Label labelNode = (Label) new Text(label).render();
            labelNode.getStyleClass().add("bricks-label");
            VBox container = new VBox(4, labelNode, picker);
            container.setFillWidth(true);
            return container;
        }

        return picker;
    }

    private void applyThemeToPopup(javafx.scene.control.DatePicker picker) {
        if (!(picker.getSkin() instanceof DatePickerSkin skin)) {
            return;
        }

        Node popupContent = skin.getPopupContent();
        if (
            popupContent != null &&
            popupContent.getScene() != null &&
            picker.getScene() != null
        ) {
            popupContent
                .getScene()
                .getStylesheets()
                .setAll(picker.getScene().getStylesheets());
        }
    }

    @SuppressWarnings("unchecked")
    private void bindPicker(javafx.scene.control.DatePicker picker) {
        Object value = boundState.get();

        if (withTime) {
            State<LocalDateTime> state = (State<LocalDateTime>) boundState;
            if (value instanceof LocalDateTime dateTime) {
                picker.setValue(dateTime.toLocalDate());
            }
            picker.valueProperty().addListener((obs, old, selectedDate) -> {
                if (selectedDate == null) return;

                LocalDateTime current = state.get();
                LocalTime time = current != null ? current.toLocalTime() : LocalTime.MIDNIGHT;
                state.set(LocalDateTime.of(selectedDate, time));
            });
            return;
        }

        State<LocalDate> state = (State<LocalDate>) boundState;
        if (value instanceof LocalDate date) {
            picker.setValue(date);
        }
        picker.valueProperty().addListener((obs, old, selectedDate) -> {
            if (selectedDate != null) {
                state.set(selectedDate);
            }
        });
    }
}
