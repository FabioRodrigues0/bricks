package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Divider;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DividerTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarSeparador() {
        assertInstanceOf(Separator.class, new Divider().render());
    }

    @Test
    void deveSerhorizontalPorDefeito() {
        Separator sep = (Separator) new Divider().render();

        assertEquals(Orientation.HORIZONTAL, sep.getOrientation());
    }

    @Test
    void deveSerVertical() {
        Separator sep = (Separator) new Divider().vertical().render();

        assertEquals(Orientation.VERTICAL, sep.getOrientation());
    }

    @Test
    void deveVoltarHorizontal() {
        Separator sep = (Separator) new Divider().vertical().horizontal().render();

        assertEquals(Orientation.HORIZONTAL, sep.getOrientation());
    }
}
