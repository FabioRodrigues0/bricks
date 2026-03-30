package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Button;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ButtonTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarButton() {
        Node node = new Button("Clica").render();

        assertInstanceOf(javafx.scene.control.Button.class, node);
        assertEquals("Clica", ((javafx.scene.control.Button) node).getText());
    }

    @Test
    void deveExecutarOnClick() {
        AtomicBoolean clicado = new AtomicBoolean(false);
        Node node = new Button("Clica")
            .onClick(() -> clicado.set(true))
            .render();

        javafx.scene.control.Button btn = (javafx.scene.control.Button) node;
        btn.fire();

        assertTrue(clicado.get());
    }

    @Test
    void deveRenderizarSemOnClick() {
        Node node = new Button("Clica").render();

        javafx.scene.control.Button btn = (javafx.scene.control.Button) node;
        assertDoesNotThrow(btn::fire);
    }
}
