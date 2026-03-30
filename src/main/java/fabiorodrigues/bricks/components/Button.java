package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.core.State;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Componente de botao. Renderiza como um {@link javafx.scene.control.Button} JavaFX.
 *
 * <pre>{@code
 * new Button("Clica").onClick(() -> System.out.println("clicado!"));
 * }</pre>
 *
 * <p>Com condicao de ativacao:</p>
 * <pre>{@code
 * // Desativa visualmente (fica cinzento)
 * new Button("Decode").enabled(false).onClick(() -> ...)
 *
 * // Reativo a um State
 * new Button("Decode").enabled(prontoState).onClick(() -> ...)
 *
 * // Bloqueia mas mostra alerta ao tentar clicar
 * new Button("Decode")
 *     .enabled(false)
 *     .onDisabledClick(() -> Alert.warning("Aviso", "Escreve algo primeiro."))
 *     .onClick(() -> ...)
 * }</pre>
 */
public class Button implements Component {

    private final String label;
    private Runnable onClick;
    private Runnable onDisabledClick;
    private Modifier modifier;
    private boolean enabled = true;
    private State<Boolean> enabledState;

    /**
     * Cria um botao com o texto dado.
     *
     * @param label {@code String} — texto do botao
     */
    public Button(String label) {
        this.label = label;
    }

    /**
     * Define a acao a executar quando o botao e clicado.
     *
     * @param action {@code Runnable} — acao a executar
     * @return este componente para encadeamento
     */
    public Button onClick(Runnable action) {
        this.onClick = action;
        return this;
    }

    /**
     * Ativa ou desativa o botao.
     * Quando desativado, fica visualmente cinzento e nao responde a cliques normais.
     *
     * @param enabled {@code boolean} — true para ativo, false para desativado
     * @return este componente para encadeamento
     */
    public Button enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Liga o estado de ativacao do botao a um {@link State}.
     * O botao ativa/desativa automaticamente quando o state muda.
     *
     * @param state {@code State<Boolean>} — state booleano que controla se o botao esta ativo
     * @return este componente para encadeamento
     */
    public Button enabled(State<Boolean> state) {
        this.enabledState = state;
        return this;
    }

    /**
     * Define uma acao a executar quando o utilizador tenta clicar num botao desativado.
     * Util para mostrar um alerta explicativo em vez de simplesmente ignorar o clique.
     *
     * <pre>{@code
     * new Button("Enviar")
     *     .enabled(false)
     *     .onDisabledClick(() -> Alert.warning("Aviso", "Preenche o formulario primeiro."))
     *     .onClick(() -> enviar())
     * }</pre>
     *
     * @param action {@code Runnable} — acao a executar quando clicado no estado desativado
     * @return este componente para encadeamento
     */
    public Button onDisabledClick(Runnable action) {
        this.onDisabledClick = action;
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier {@code Modifier} — o modifier a aplicar
     * @return este componente para encadeamento
     */
    public Button modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        javafx.scene.control.Button btn = new javafx.scene.control.Button(label);
        btn.getStyleClass().add("bricks-button");

        boolean isEnabled = enabledState != null ? enabledState.get() : enabled;

        if (onDisabledClick != null) {
            // quando ha acao para clique desativado, nao usamos setDisable
            // em vez disso controlamos manualmente via filtro de eventos
            btn.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                boolean currentlyEnabled = enabledState != null ? enabledState.get() : enabled;
                if (!currentlyEnabled) {
                    onDisabledClick.run();
                    e.consume();
                }
            });
            if (!isEnabled) {
                btn.setOpacity(0.5);
            }
        } else {
            btn.setDisable(!isEnabled);
        }

        if (onClick != null) {
            btn.setOnAction(e -> {
                boolean currentlyEnabled = enabledState != null ? enabledState.get() : enabled;
                if (currentlyEnabled) {
                    onClick.run();
                }
            });
        }

        if (modifier != null) {
            modifier.applyTo(btn);
        }

        return btn;
    }
}
