package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.ProgressBar;
import fabiorodrigues.bricks.core.State;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProgressBarTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarProgressBar() {
        assertInstanceOf(javafx.scene.control.ProgressBar.class,
            new ProgressBar().render());
    }

    @Test
    void deveTerValorDefinido() {
        javafx.scene.control.ProgressBar pb =
            (javafx.scene.control.ProgressBar) new ProgressBar().value(0.75).render();

        assertEquals(0.75, pb.getProgress(), 0.001);
    }

    @Test
    void deveComecarEmZero() {
        javafx.scene.control.ProgressBar pb =
            (javafx.scene.control.ProgressBar) new ProgressBar().render();

        assertEquals(0.0, pb.getProgress(), 0.001);
    }

    @Test
    void deveSerIndeterminado() {
        javafx.scene.control.ProgressBar pb =
            (javafx.scene.control.ProgressBar) new ProgressBar().indeterminate().render();

        assertEquals(javafx.scene.control.ProgressBar.INDETERMINATE_PROGRESS, pb.getProgress(), 0.001);
    }

    @Test
    void deveReflectirValorDoState() {
        State<Double> state = new State<>(0.5);
        javafx.scene.control.ProgressBar pb =
            (javafx.scene.control.ProgressBar) new ProgressBar().bindTo(state).render();

        assertEquals(0.5, pb.getProgress(), 0.001);
    }
}
