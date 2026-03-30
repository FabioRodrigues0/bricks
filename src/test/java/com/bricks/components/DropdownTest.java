package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Dropdown;
import fabiorodrigues.bricks.core.State;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class DropdownTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarComboBox() {
        assertInstanceOf(ComboBox.class,
            new Dropdown<>(List.of("A", "B")).render());
    }

    @Test
    void deveTerOpcoes() {
        ComboBox<String> cb = (ComboBox<String>)
            new Dropdown<>(List.of("Portugal", "Brasil")).render();

        assertEquals(2, cb.getItems().size());
    }

    @Test
    void deveSelecionarOpcaoInicial() {
        ComboBox<String> cb = (ComboBox<String>)
            new Dropdown<>(List.of("A", "B", "C"))
                .selected("B")
                .render();

        assertEquals("B", cb.getValue());
    }

    @Test
    void deveSelecionarPrimeiraOpcaoSeNaoHouverSelecao() {
        ComboBox<String> cb = (ComboBox<String>)
            new Dropdown<>(List.of("X", "Y")).render();

        assertEquals("X", cb.getValue());
    }

    @Test
    void deveRenderizarComLabelEmVBox() {
        assertInstanceOf(VBox.class,
            new Dropdown<>(List.of("A")).label("Pais:").render());
    }

    @Test
    void deveExecutarOnChange() {
        AtomicReference<String> resultado = new AtomicReference<>();
        ComboBox<String> cb = (ComboBox<String>)
            new Dropdown<>(List.of("A", "B"))
                .onChange(resultado::set)
                .render();

        cb.setValue("B");

        assertEquals("B", resultado.get());
    }

    @Test
    void deveActualizarState() {
        State<String> state = new State<>("A");
        ComboBox<String> cb = (ComboBox<String>)
            new Dropdown<>(List.of("A", "B"))
                .bindTo(state)
                .render();

        cb.setValue("B");

        assertEquals("B", state.get());
    }
}
