package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Button;
import fabiorodrigues.bricks.components.Row;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RowTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarHBox() {
        Node node = new Row().render();
        assertInstanceOf(HBox.class, node);
    }

    @Test
    void deveAplicarGap() {
        HBox hbox = (HBox) new Row().gap(8).render();
        assertEquals(8, hbox.getSpacing());
    }

    @Test
    void deveRenderizarFilhos() {
        HBox hbox = (HBox) new Row()
            .children(new Button("A"), new Button("B"))
            .render();

        assertEquals(2, hbox.getChildren().size());
    }
}
