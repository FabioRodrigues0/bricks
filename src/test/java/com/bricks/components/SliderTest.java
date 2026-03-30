package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Slider;
import fabiorodrigues.bricks.core.State;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

class SliderTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarSlider() {
        assertInstanceOf(javafx.scene.control.Slider.class,
            new Slider(0, 100).render());
    }

    @Test
    void deveTerMinEMax() {
        javafx.scene.control.Slider s =
            (javafx.scene.control.Slider) new Slider(10, 50).render();

        assertEquals(10, s.getMin(), 0.001);
        assertEquals(50, s.getMax(), 0.001);
    }

    @Test
    void deveTerValorInicial() {
        javafx.scene.control.Slider s =
            (javafx.scene.control.Slider) new Slider(0, 100).value(75).render();

        assertEquals(75, s.getValue(), 0.001);
    }

    @Test
    void deveExecutarOnChange() {
        AtomicReference<Double> resultado = new AtomicReference<>();
        javafx.scene.control.Slider s =
            (javafx.scene.control.Slider) new Slider(0, 100)
                .onChange(resultado::set)
                .render();

        s.setValue(42);

        assertEquals(42, resultado.get(), 0.001);
    }

    @Test
    void deveActualizarState() {
        State<Double> state = new State<>(0.0);
        javafx.scene.control.Slider s =
            (javafx.scene.control.Slider) new Slider(0, 100)
                .bindTo(state)
                .render();

        s.setValue(60);

        assertEquals(60, state.get(), 0.001);
    }

    @Test
    void deveReflectirValorDoState() {
        State<Double> state = new State<>(30.0);
        javafx.scene.control.Slider s =
            (javafx.scene.control.Slider) new Slider(0, 100)
                .bindTo(state)
                .render();

        assertEquals(30, s.getValue(), 0.001);
    }
}
