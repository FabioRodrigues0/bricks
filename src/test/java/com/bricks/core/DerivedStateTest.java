package com.bricks.core;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.core.DerivedState;
import fabiorodrigues.bricks.core.State;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class DerivedStateTest {

    @Test
    void deveCalcularValorNaPrimeiraLeitura() {
        State<Integer> base = new State<>(5);
        DerivedState<Integer> dobro = new DerivedState<>(() -> base.get() * 2, base);

        assertEquals(10, dobro.get());
    }

    @Test
    void deveUsarCacheSeNenhumaDependenciaMudou() {
        AtomicInteger chamadas = new AtomicInteger(0);
        State<Integer> base = new State<>(3);
        DerivedState<Integer> derived = new DerivedState<>(() -> {
            chamadas.incrementAndGet();
            return base.get() * 2;
        }, base);

        derived.get();
        derived.get();
        derived.get();

        assertEquals(1, chamadas.get());
    }

    @Test
    void deveRecalcularQuandoDependenciaMuda() {
        State<Integer> base = new State<>(5);
        DerivedState<Integer> dobro = new DerivedState<>(() -> base.get() * 2, base);

        assertEquals(10, dobro.get());
        base.set(7);
        assertEquals(14, dobro.get());
    }

    @Test
    void deveRecalcularQuandoQualquerDependenciaMuda() {
        State<String> primeiro = new State<>("Ola");
        State<String> segundo = new State<>("Mundo");
        DerivedState<String> frase = new DerivedState<>(
            () -> primeiro.get() + " " + segundo.get(),
            primeiro, segundo
        );

        assertEquals("Ola Mundo", frase.get());

        primeiro.set("Bom dia");
        assertEquals("Bom dia Mundo", frase.get());

        segundo.set("Portugal");
        assertEquals("Bom dia Portugal", frase.get());
    }

    @Test
    void deveFuncionarComMultiplasDependencias() {
        State<Integer> a = new State<>(10);
        State<Integer> b = new State<>(20);
        State<Integer> c = new State<>(30);
        DerivedState<Integer> soma = new DerivedState<>(() -> a.get() + b.get() + c.get(), a, b, c);

        assertEquals(60, soma.get());

        b.set(5);
        assertEquals(45, soma.get());
    }
}
