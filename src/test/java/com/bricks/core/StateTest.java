package com.bricks.core;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.core.State;
import org.junit.jupiter.api.Test;

class StateTest {

    @Test
    void deveGuardarValorInicial() {
        State<String> state = new State<>("hello");
        assertEquals("hello", state.get());
    }

    @Test
    void deveAtualizarComSet() {
        State<Integer> state = new State<>(0);
        state.set(42);
        assertEquals(42, state.get());
    }

    @Test
    void deveAtualizarComUpdate() {
        State<Integer> state = new State<>(10);
        state.update(v -> v + 5);
        assertEquals(15, state.get());
    }

    @Test
    void naoDeveFalharSemCallback() {
        State<String> state = new State<>("test");
        assertDoesNotThrow(() -> state.set("novo"));
    }
}
