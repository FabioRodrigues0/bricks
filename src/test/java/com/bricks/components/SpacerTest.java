package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Spacer;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SpacerTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarRegion() {
        assertInstanceOf(Region.class, new Spacer().render());
    }

    @Test
    void spacerFlexivelDeveExpandir() {
        Region region = (Region) new Spacer().render();

        assertEquals(Priority.ALWAYS, HBox.getHgrow(region));
        assertEquals(Priority.ALWAYS, VBox.getVgrow(region));
    }

    @Test
    void spacerFixoDeveTerTamanho() {
        Region region = (Region) new Spacer(16).render();

        assertEquals(16, region.getMinWidth(), 0.001);
        assertEquals(16, region.getMinHeight(), 0.001);
        assertEquals(16, region.getMaxWidth(), 0.001);
        assertEquals(16, region.getMaxHeight(), 0.001);
    }
}
