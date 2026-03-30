package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Icon;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kordamp.ikonli.javafx.FontIcon;

class IconTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarFontIcon() {
        Node node = new Icon("fas-home").render();

        assertInstanceOf(FontIcon.class, node);
    }

    @Test
    void deveTerTamanhoDefinido() {
        FontIcon icon = (FontIcon) new Icon("fas-home").size(24).render();

        assertEquals(24, icon.getIconSize());
    }

    @Test
    void deveTerTamanhoPortDefeito() {
        FontIcon icon = (FontIcon) new Icon("fas-home").render();

        assertEquals(16, icon.getIconSize());
    }

    @Test
    void deveTerCorDefinida() {
        FontIcon icon = (FontIcon) new Icon("fas-home").color(Color.RED).render();

        assertEquals(Color.RED, icon.getIconColor());
    }
}
