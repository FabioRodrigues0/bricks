package com.bricks.components;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.TextField;
import fabiorodrigues.bricks.core.State;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TextFieldTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarTextFieldPorDefeito() {
        assertInstanceOf(javafx.scene.control.TextField.class, new TextField().render());
    }

    @Test
    void deveRenderizarTextAreaNoModoMultiline() {
        assertInstanceOf(javafx.scene.control.TextArea.class, new TextField().multiline().render());
    }

    @Test
    void deveDefinirValorInicial() {
        javafx.scene.control.TextField field = (javafx.scene.control.TextField) new TextField().value("ola").render();
        assertEquals("ola", field.getText());
    }

    @Test
    void deveDefinirPlaceholder() {
        javafx.scene.control.TextField field = (javafx.scene.control.TextField) new TextField().placeholder("Escreve...").render();
        assertEquals("Escreve...", field.getPromptText());
    }

    @Test
    void deveNotificarOnChange() {
        AtomicReference<String> ultimo = new AtomicReference<>("");
        javafx.scene.control.TextField field = (javafx.scene.control.TextField) new TextField().onChange(ultimo::set).render();

        field.setText("novo texto");

        assertEquals("novo texto", ultimo.get());
    }

    @Test
    void deveDefinirRowsNoModoMultiline() {
        javafx.scene.control.TextArea area = (javafx.scene.control.TextArea) new TextField().multiline().rows(6).render();
        assertEquals(6, area.getPrefRowCount());
    }

    @Test
    void deveActivarWrapTextPorDefeitoNoModoMultiline() {
        javafx.scene.control.TextArea area = (javafx.scene.control.TextArea) new TextField().multiline().render();
        assertTrue(area.isWrapText());
    }

    @Test
    void deveRenderizarVBoxComLabelAcimaDoInput() {
        var node = new TextField().label("Username:").render();

        assertInstanceOf(VBox.class, node);
        VBox vbox = (VBox) node;
        assertInstanceOf(Label.class, vbox.getChildren().get(0));
        assertInstanceOf(javafx.scene.control.TextField.class, vbox.getChildren().get(1));
        assertEquals("Username:", ((Label) vbox.getChildren().get(0)).getText());
    }

    @Test
    void deveUsarValorDoStateComoValorInicial() {
        State<String> state = new State<>("valor inicial");
        javafx.scene.control.TextField field = (javafx.scene.control.TextField) new TextField().bindTo(state).render();

        assertEquals("valor inicial", field.getText());
    }

    @Test
    void deveAtualizarStateQuandoUtilizadorEscreve() {
        State<String> state = new State<>("");
        javafx.scene.control.TextField field = (javafx.scene.control.TextField) new TextField().bindTo(state).render();

        field.setText("novo texto");

        assertEquals("novo texto", state.get());
    }

    @Test
    void deveDevolvValorAtualComGetValue() {
        TextField campo = new TextField().value("inicial");
        campo.render();
        assertEquals("inicial", campo.getValue());
    }
}
