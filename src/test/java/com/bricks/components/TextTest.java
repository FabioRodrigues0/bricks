package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Text;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TextTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarLabel() {
        Node node = new Text("Ola").render();

        assertInstanceOf(Label.class, node);
        assertEquals("Ola", ((Label) node).getText());
    }

    @Test
    void deveAplicarFontSizeDirecto() {
        Node node = new Text("Ola").fontSize(20).render();

        Label label = (Label) node;
        assertTrue(label.getStyle().contains("-fx-font-size: 20.0px"));
    }

    @Test
    void deveAplicarModifier() {
        Modifier mod = new Modifier().textColor(Color.RED).bold();
        Node node = new Text("Ola").modifier(mod).render();

        Label label = (Label) node;
        assertTrue(label.getStyle().contains("-fx-text-fill: #ff0000"));
        assertTrue(label.getStyle().contains("-fx-font-weight: bold"));
    }
}
