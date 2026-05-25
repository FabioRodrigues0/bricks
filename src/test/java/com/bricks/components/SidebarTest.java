package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Sidebar;
import fabiorodrigues.bricks.components.Text;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SidebarTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveAceitarComponenteComoLogo() {
        VBox sidebar = (VBox) new Sidebar()
            .logo(new Text("Bricks"))
            .render();

        assertInstanceOf(Label.class, sidebar.getChildren().get(0));
        assertEquals("Bricks", ((Label) sidebar.getChildren().get(0)).getText());
    }
}
