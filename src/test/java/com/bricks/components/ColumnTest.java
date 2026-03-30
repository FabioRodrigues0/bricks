package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Column;
import fabiorodrigues.bricks.components.Text;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ColumnTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarVBox() {
        Node node = new Column().render();
        assertInstanceOf(VBox.class, node);
    }

    @Test
    void deveAplicarGap() {
        VBox vbox = (VBox) new Column().gap(12).render();
        assertEquals(12, vbox.getSpacing());
    }

    @Test
    void deveAplicarPadding() {
        VBox vbox = (VBox) new Column().padding(16).render();
        assertEquals(16, vbox.getPadding().getTop());
    }

    @Test
    void deveRenderizarFilhos() {
        VBox vbox = (VBox) new Column()
            .children(new Text("A"), new Text("B"), new Text("C"))
            .render();

        assertEquals(3, vbox.getChildren().size());
    }

    @Test
    void deveAplicarModifier() {
        Modifier mod = new Modifier().width(300);
        VBox vbox = (VBox) new Column().modifier(mod).render();

        assertEquals(300, vbox.getPrefWidth());
    }

    @Test
    void deveSuportarNesting() {
        VBox outer = (VBox) new Column()
            .children(new Column().children(new Text("inner")))
            .render();

        assertEquals(1, outer.getChildren().size());
        assertInstanceOf(VBox.class, outer.getChildren().get(0));

        VBox inner = (VBox) outer.getChildren().get(0);
        assertEquals(1, inner.getChildren().size());
    }
}
