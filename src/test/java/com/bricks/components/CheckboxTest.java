package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Checkbox;
import fabiorodrigues.bricks.core.State;
import javafx.scene.control.CheckBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

class CheckboxTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarCheckBox() {
        assertInstanceOf(CheckBox.class, new Checkbox("Aceito").render());
    }

    @Test
    void deveTerTexto() {
        CheckBox cb = (CheckBox) new Checkbox("Aceito os termos").render();

        assertEquals("Aceito os termos", cb.getText());
    }

    @Test
    void deveComecarDesmarcadoPorDefeito() {
        CheckBox cb = (CheckBox) new Checkbox("Aceito").render();

        assertFalse(cb.isSelected());
    }

    @Test
    void deveComecarMarcado() {
        CheckBox cb = (CheckBox) new Checkbox("Aceito").checked(true).render();

        assertTrue(cb.isSelected());
    }

    @Test
    void deveExecutarOnChange() {
        AtomicBoolean resultado = new AtomicBoolean(false);
        CheckBox cb = (CheckBox) new Checkbox("Aceito")
            .onChange(resultado::set)
            .render();

        cb.setSelected(true);

        assertTrue(resultado.get());
    }

    @Test
    void deveActualizarState() {
        State<Boolean> state = new State<>(false);
        CheckBox cb = (CheckBox) new Checkbox("Aceito")
            .bindTo(state)
            .render();

        cb.setSelected(true);

        assertTrue(state.get());
    }

    @Test
    void deveReflectirValorDoState() {
        State<Boolean> state = new State<>(true);
        CheckBox cb = (CheckBox) new Checkbox("Aceito")
            .bindTo(state)
            .render();

        assertTrue(cb.isSelected());
    }
}
