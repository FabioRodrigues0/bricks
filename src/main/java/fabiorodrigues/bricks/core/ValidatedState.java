package fabiorodrigues.bricks.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * State com validacao declarativa. Inspirado no Livewire (Laravel).
 * A validacao fica no ViewModel junto do dado que valida.
 *
 * <p>Os erros so ficam visiveis depois de {@link #validate()} ser chamado
 * (normalmente pelo {@link fabiorodrigues.bricks.components.Form} ao submeter).</p>
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
 * // Na View — o TextField deteta automaticamente o ValidatedState
 * new Form()
 *     .field(new TextField().label("Nome:").bindTo(vm.nome))
 *     .field(new TextField().label("Email:").bindTo(vm.email))
 *     .onSubmit(() -> vm.guardar())
 *     .submitLabel("Guardar")
 * }</pre>
 *
 * @param <T> o tipo do valor guardado
 */
public class ValidatedState<T> extends State<T> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    private final List<ValidationRule<T>> rules = new ArrayList<>();
    private String currentError = null;
    private boolean validated = false;

    /**
     * Cria um novo ValidatedState com o valor inicial dado.
     * Preferir {@link BricksViewModel#validatedState(Object)} para ficar ligado ao re-render.
     *
     * @param initial o valor inicial
     */
    public ValidatedState(T initial) {
        super(initial);
    }

    // ── Regras de validacao ───────────────────────────────────────────────────

    /**
     * Campo obrigatorio — falha se null, ou vazio/em branco (para String).
     *
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> required(String errorMessage) {
        rules.add(value -> {
            if (value == null) return errorMessage;
            if (value instanceof String s && s.isBlank()) return errorMessage;
            return null;
        });
        return this;
    }

    /**
     * Comprimento minimo — so para String.
     *
     * @param min          comprimento minimo
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> minLength(int min, String errorMessage) {
        rules.add(value -> value instanceof String s && s.length() < min ? errorMessage : null);
        return this;
    }

    /**
     * Comprimento maximo — so para String.
     *
     * @param max          comprimento maximo
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> maxLength(int max, String errorMessage) {
        rules.add(value -> value instanceof String s && s.length() > max ? errorMessage : null);
        return this;
    }

    /**
     * Formato de email valido. Valores vazios sao ignorados (combinar com {@link #required}).
     *
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> email(String errorMessage) {
        return matches(EMAIL_PATTERN, errorMessage);
    }

    /**
     * Valor minimo numerico — so para Number.
     *
     * @param minVal       valor minimo (inclusive)
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> min(double minVal, String errorMessage) {
        rules.add(value -> value instanceof Number n && n.doubleValue() < minVal ? errorMessage : null);
        return this;
    }

    /**
     * Valor maximo numerico — so para Number.
     *
     * @param maxVal       valor maximo (inclusive)
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> max(double maxVal, String errorMessage) {
        rules.add(value -> value instanceof Number n && n.doubleValue() > maxVal ? errorMessage : null);
        return this;
    }

    /**
     * Regex personalizado — so para String. Valores vazios sao ignorados.
     *
     * @param regex        expressao regular que o valor completo deve satisfazer
     * @param errorMessage mensagem de erro
     * @return este state para encadeamento
     */
    public ValidatedState<T> matches(String regex, String errorMessage) {
        return matches(Pattern.compile(regex), errorMessage);
    }

    private ValidatedState<T> matches(Pattern pattern, String errorMessage) {
        rules.add(value -> value instanceof String s && !s.isBlank() && !pattern.matcher(s).matches()
            ? errorMessage
            : null);
        return this;
    }

    /**
     * Regra completamente personalizada via lambda.
     *
     * <pre>{@code
     * .rule(v -> "admin".equals(v) ? "Nome reservado" : null)
     * }</pre>
     *
     * @param customRule regra a adicionar
     * @return este state para encadeamento
     */
    public ValidatedState<T> rule(ValidationRule<T> customRule) {
        rules.add(customRule);
        return this;
    }

    // ── Estado de validacao ───────────────────────────────────────────────────

    /**
     * Valida o valor atual contra todas as regras (para na primeira que falha).
     * Marca o state como validado — o erro passa a ser visivel — e notifica os
     * listeners para a UI mostrar/esconder a mensagem.
     *
     * @return true se valido, false se tem erros
     */
    public boolean validate() {
        validated = true;
        currentError = firstError();
        notifyListeners();
        return currentError == null;
    }

    /**
     * Verifica se o valor atual passa todas as regras, sem alterar o erro visivel.
     *
     * @return true se valido
     */
    public boolean isValid() {
        return firstError() == null;
    }

    /**
     * Devolve a mensagem de erro atual, ou null se valido ou ainda nao validado.
     *
     * @return a mensagem de erro visivel, ou null
     */
    public String getError() {
        return validated ? currentError : null;
    }

    /**
     * Limpa o estado de validacao — remove o erro visivel.
     * Util depois de submeter com sucesso e limpar o form.
     */
    public void clearError() {
        validated = false;
        currentError = null;
        notifyListeners();
    }

    /**
     * Indica se tem pelo menos uma regra de validacao definida.
     *
     * @return true se tem regras
     */
    public boolean hasRules() {
        return !rules.isEmpty();
    }

    private String firstError() {
        for (ValidationRule<T> rule : rules) {
            String error = rule.validate(get());
            if (error != null) return error;
        }
        return null;
    }
}
