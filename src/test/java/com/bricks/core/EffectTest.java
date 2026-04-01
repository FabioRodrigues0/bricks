package com.bricks.core;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.core.Effect;
import fabiorodrigues.bricks.core.State;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class EffectTest {

    @Test
    void executaImediatamenteNoConstrutor() {
        AtomicInteger chamadas = new AtomicInteger(0);
        new Effect(chamadas::incrementAndGet);
        assertEquals(1, chamadas.get());
    }

    @Test
    void executaQuandoDependenciaMuda() {
        State<Integer> estado = new State<>(0);
        AtomicInteger chamadas = new AtomicInteger(0);

        new Effect(chamadas::incrementAndGet, estado);
        assertEquals(1, chamadas.get()); // execucao inicial

        estado.set(1);
        assertEquals(2, chamadas.get());

        estado.set(2);
        assertEquals(3, chamadas.get());
    }

    @Test
    void executaComMultiplasDependencias() {
        State<String> a = new State<>("a");
        State<String> b = new State<>("b");
        AtomicInteger chamadas = new AtomicInteger(0);

        new Effect(chamadas::incrementAndGet, a, b);
        assertEquals(1, chamadas.get());

        a.set("x");
        assertEquals(2, chamadas.get());

        b.set("y");
        assertEquals(3, chamadas.get());
    }

    @Test
    void semDependenciasExecutaSoUmaVez() {
        AtomicInteger chamadas = new AtomicInteger(0);
        new Effect(chamadas::incrementAndGet);
        assertEquals(1, chamadas.get());
    }

    @Test
    void acaoLeValorAtualDoDependente() {
        State<Integer> valor = new State<>(10);
        AtomicInteger capturado = new AtomicInteger(0);

        new Effect(() -> capturado.set(valor.get()), valor);
        assertEquals(10, capturado.get());

        valor.set(42);
        assertEquals(42, capturado.get());
    }
}
