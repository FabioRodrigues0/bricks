package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.ValidatedState;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Agrupa campos com {@link ValidatedState}, valida ao submeter e mostra erros inline.
 * A validacao e declarada no ViewModel — o Form so liga os campos e submete.
 *
 * <pre>{@code
 * // No ViewModel
 * public final ValidatedState<String> nome = validatedState("")
 *     .required("Campo obrigatorio")
 *     .minLength(3, "Minimo 3 caracteres");
 *
 * public final ValidatedState<String> email = validatedState("")
 *     .required("Campo obrigatorio")
 *     .email("Email invalido");
 *
 * // Na View
 * new Form()
 *     .field(new TextField().label("Nome:").bindTo(vm.nome))
 *     .field(new TextField().label("Email:").bindTo(vm.email))
 *     .onSubmit(() -> vm.guardar())
 *     .submitLabel("Guardar")
 * }</pre>
 *
 * <p>Para campos que nao sao {@link TextField} (ex: Dropdown, DatePicker),
 * registar o ValidatedState explicitamente:</p>
 * <pre>{@code
 * new Form()
 *     .field(new Dropdown<>(...).bindTo(vm.categoria), vm.categoria)
 *     .onSubmit(() -> vm.guardar())
 * }</pre>
 */
public class Form implements Component {

    private final List<Component> fields = new ArrayList<>();
    private final List<ValidatedState<?>> explicitStates = new ArrayList<>();
    private Runnable onSubmit;
    private String submitLabel = "Submeter";
    private double gap = 12;
    private Modifier modifier;

    /**
     * Adiciona um campo ao form.
     * Se o campo for um {@link TextField} ligado a um {@link ValidatedState},
     * a validacao e registada automaticamente.
     *
     * @param field o campo a adicionar
     * @return este componente para encadeamento
     */
    public Form field(Component field) {
        fields.add(field);
        return this;
    }

    /**
     * Adiciona um campo com {@link ValidatedState} explicito.
     * Util para Dropdown, DatePicker e outros campos que nao sao TextField.
     *
     * @param field o campo a adicionar
     * @param state o state a validar ao submeter
     * @return este componente para encadeamento
     */
    public Form field(Component field, ValidatedState<?> state) {
        fields.add(field);
        explicitStates.add(state);
        return this;
    }

    /**
     * Define o callback chamado quando todos os campos sao validos e o form e submetido.
     *
     * @param callback acao a executar
     * @return este componente para encadeamento
     */
    public Form onSubmit(Runnable callback) {
        this.onSubmit = callback;
        return this;
    }

    /**
     * Define o texto do botao de submit (por defeito: "Submeter").
     *
     * @param label texto do botao
     * @return este componente para encadeamento
     */
    public Form submitLabel(String label) {
        this.submitLabel = label;
        return this;
    }

    /**
     * Define o espaco entre campos em pixels (por defeito: 12).
     *
     * @param gap espacamento em pixels
     * @return este componente para encadeamento
     */
    public Form gap(double gap) {
        this.gap = gap;
        return this;
    }

    /**
     * Aplica um {@link Modifier} ao container do form.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Form modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Valida todos os campos e, se forem todos validos, chama o {@link #onSubmit(Runnable)}.
     * Todos os campos sao validados (nao para no primeiro erro) para mostrar todos os erros de uma vez.
     */
    public void submit() {
        boolean allValid = true;
        for (ValidatedState<?> vs : collectStates()) {
            if (!vs.validate()) allValid = false;
        }
        if (allValid && onSubmit != null) {
            onSubmit.run();
        }
    }

    // Recolhido no submit (e nao no field()) para funcionar mesmo que bindTo seja chamado depois
    private List<ValidatedState<?>> collectStates() {
        List<ValidatedState<?>> states = new ArrayList<>(explicitStates);
        for (Component field : fields) {
            if (field instanceof TextField tf && tf.getValidatedState() != null
                && !states.contains(tf.getValidatedState())) {
                states.add(tf.getValidatedState());
            }
        }
        return states;
    }

    @Override
    public Node render() {
        VBox form = new VBox(gap);
        form.getStyleClass().add("bricks-form");
        form.setFillWidth(true);

        for (Component field : fields) {
            form.getChildren().add(field.render());
        }
        form.getChildren().add(new Button(submitLabel).onClick(this::submit).render());

        if (modifier != null) {
            modifier.applyTo(form);
        }
        return form;
    }
}
