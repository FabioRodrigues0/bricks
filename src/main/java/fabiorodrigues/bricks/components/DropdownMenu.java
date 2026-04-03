package fabiorodrigues.bricks.components;

import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.BricksTheme;
import fabiorodrigues.bricks.style.Modifier;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;

/**
 * Menu com items simples e/ou com checkbox. Renderiza como um {@link MenuButton} JavaFX
 * com {@link CustomMenuItem} contendo nodes Bricks — herda o tema automaticamente.
 *
 * <pre>{@code
 * new DropdownMenu()
 *     .icon("fas-cog")
 *     .tooltip("Colunas visíveis")
 *     .item(new DropdownCheckItem("Nome").checked(true).onChange(v -> ...))
 *     .item(new DropdownCheckItem("Turma").checked(false).onChange(v -> ...))
 *     .item(new DropdownItem("Exportar").onClick(() -> ...))
 * }</pre>
 */
public class DropdownMenu implements Component {

    private String iconCode;
    private String tooltipText;
    private String label;
    private Modifier modifier;
    private final List<Object> items = new ArrayList<>();

    /**
     * Define o icone do botao (codigo Ikonli, ex: {@code "fas-cog"}).
     *
     * @param iconCode codigo do icone
     * @return este componente para encadeamento
     */
    public DropdownMenu icon(String iconCode) {
        this.iconCode = iconCode;
        return this;
    }

    /**
     * Define o tooltip do botao.
     *
     * @param text texto do tooltip
     * @return este componente para encadeamento
     */
    public DropdownMenu tooltip(String text) {
        this.tooltipText = text;
        return this;
    }

    /**
     * Define o texto do botao (alternativa ou complemento ao icone).
     *
     * @param label texto do botao
     * @return este componente para encadeamento
     */
    public DropdownMenu label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Adiciona um {@link DropdownItem} ao menu.
     *
     * @param item item a adicionar
     * @return este componente para encadeamento
     */
    public DropdownMenu item(DropdownItem item) {
        this.items.add(item);
        return this;
    }

    /**
     * Adiciona um {@link DropdownCheckItem} ao menu.
     *
     * @param item item a adicionar
     * @return este componente para encadeamento
     */
    public DropdownMenu item(DropdownCheckItem item) {
        this.items.add(item);
        return this;
    }

    /**
     * Adiciona varios {@link DropdownCheckItem} ao menu de uma vez.
     *
     * @param items items a adicionar
     * @return este componente para encadeamento
     */
    public DropdownMenu items(DropdownCheckItem... items) {
        for (DropdownCheckItem item : items) this.items.add(item);
        return this;
    }

    /**
     * Adiciona varios {@link DropdownItem} ao menu de uma vez.
     *
     * @param items items a adicionar
     * @return este componente para encadeamento
     */
    public DropdownMenu items(DropdownItem... items) {
        for (DropdownItem item : items) this.items.add(item);
        return this;
    }

    /**
     * Aplica um {@link Modifier} com propriedades visuais reutilizaveis.
     *
     * @param modifier o modifier a aplicar
     * @return este componente para encadeamento
     */
    public DropdownMenu modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public Node render() {
        MenuButton menuButton = new MenuButton();
        menuButton.getStyleClass().add("bricks-dropdown-menu");

        if (label != null) {
            menuButton.setText(label);
        }

        if (iconCode != null) {
            // Usa Icon da Bricks
            Node iconNode = new Icon(iconCode)
                .size(14)
                .color(BricksTheme.current().colorScheme().onPrimary())
                .render();
            menuButton.setGraphic(iconNode);
        }

        if (tooltipText != null) {
            menuButton.setTooltip(new Tooltip(tooltipText));
        }

        for (Object raw : items) {
            if (raw instanceof DropdownCheckItem checkItem) {
                // Usa Checkbox da Bricks
                Node cbNode = new Checkbox(checkItem.getLabel())
                    .checked(checkItem.isChecked())
                    .onChange(val -> {
                        if (checkItem.getOnChange() != null) {
                            checkItem.getOnChange().accept(val);
                        }
                    })
                    .render();

                CustomMenuItem menuItem = new CustomMenuItem(cbNode);
                menuItem.setHideOnClick(false);
                menuButton.getItems().add(menuItem);
            } else if (raw instanceof DropdownItem dropItem) {
                // Usa Text da Bricks
                Node textNode = new Text(dropItem.getLabel()).render();

                CustomMenuItem menuItem = new CustomMenuItem(textNode);
                menuItem.setHideOnClick(true);
                if (dropItem.getOnClick() != null) {
                    menuItem.setOnAction(e -> dropItem.getOnClick().run());
                }
                menuButton.getItems().add(menuItem);
            }
        }

        // Propaga stylesheets ao popup e faz debug da arvore
        menuButton.setOnShown(e -> {
            javafx.application.Platform.runLater(() -> {
                for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                    if (window instanceof javafx.stage.PopupWindow popup) {
                        javafx.scene.Scene popupScene = popup.getScene();
                        if (popupScene != null && menuButton.getScene() != null) {
                            // Propaga stylesheets
                            popupScene
                                .getStylesheets()
                                .setAll(menuButton.getScene().getStylesheets());
                            // Alinha popup pela direita do botão
                            javafx.geometry.Bounds bounds = menuButton.localToScreen(
                                menuButton.getBoundsInLocal()
                            );
                            popup.setX(bounds.getMaxX() - popup.getWidth());
                        }
                    }
                }
            });
        });

        if (modifier != null) {
            modifier.applyTo(menuButton);
        }

        return menuButton;
    }
}
