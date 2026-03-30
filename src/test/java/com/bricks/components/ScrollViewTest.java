package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.ScrollView;
import fabiorodrigues.bricks.components.Text;
import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ScrollViewTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarScrollPane() {
        assertInstanceOf(ScrollPane.class,
            new ScrollView(new Text("conteudo")).render());
    }

    @Test
    void deveActivarAmbosScrollsPorDefeito() {
        ScrollPane sp = (ScrollPane) new ScrollView(new Text("x")).render();

        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, sp.getHbarPolicy());
        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, sp.getVbarPolicy());
    }

    @Test
    void deveActivarApenasScrollVertical() {
        ScrollPane sp = (ScrollPane) new ScrollView(new Text("x")).vertical().render();

        assertEquals(ScrollPane.ScrollBarPolicy.NEVER, sp.getHbarPolicy());
        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, sp.getVbarPolicy());
    }

    @Test
    void deveActivarApenasScrollHorizontal() {
        ScrollPane sp = (ScrollPane) new ScrollView(new Text("x")).horizontal().render();

        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, sp.getHbarPolicy());
        assertEquals(ScrollPane.ScrollBarPolicy.NEVER, sp.getVbarPolicy());
    }
}
