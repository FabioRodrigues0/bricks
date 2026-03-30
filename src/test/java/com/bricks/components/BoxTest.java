package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Box;
import fabiorodrigues.bricks.components.Text;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BoxTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarStackPane() {
        Node node = new Box().render();
        assertInstanceOf(StackPane.class, node);
    }

    @Test
    void deveAplicarPadding() {
        StackPane stack = (StackPane) new Box().padding(10).render();
        assertEquals(10, stack.getPadding().getTop());
    }

    @Test
    void deveRenderizarFilhos() {
        StackPane stack = (StackPane) new Box()
            .children(new Text("fundo"), new Text("topo"))
            .render();

        assertEquals(2, stack.getChildren().size());
    }
}
