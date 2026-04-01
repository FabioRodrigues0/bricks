package com.bricks.core;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.core.StateList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class StateListTest {

    @Test
    void deveGuardarValoresIniciais() {
        StateList<String> lista = new StateList<>(List.of("A", "B", "C"));
        assertEquals(List.of("A", "B", "C"), lista.get());
    }

    @Test
    void deveDevolverSnapshotImutavel() {
        StateList<String> lista = new StateList<>(List.of("A"));
        assertThrows(UnsupportedOperationException.class, () -> lista.get().add("B"));
    }

    @Test
    void addNotificaListeners() {
        StateList<String> lista = new StateList<>(new ArrayList<>());
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        lista.add("X");

        assertEquals(1, chamadas.get());
        assertEquals(1, lista.size());
    }

    @Test
    void addPorIndiceInsereNaPosicaoCerta() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A", "C")));
        lista.add(1, "B");
        assertEquals(List.of("A", "B", "C"), lista.get());
    }

    @Test
    void removeObjectoNotificaListeners() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A", "B")));
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        boolean removido = lista.remove("A");

        assertTrue(removido);
        assertEquals(1, chamadas.get());
        assertEquals(List.of("B"), lista.get());
    }

    @Test
    void removeObjectoInexistenteNaoNotifica() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A")));
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        boolean removido = lista.remove("Z");

        assertFalse(removido);
        assertEquals(0, chamadas.get());
    }

    @Test
    void removePorIndiceNotificaListeners() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A", "B", "C")));
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        String removido = lista.remove(1);

        assertEquals("B", removido);
        assertEquals(1, chamadas.get());
        assertEquals(List.of("A", "C"), lista.get());
    }

    @Test
    void setSubstituiElemento() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A", "B")));
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        lista.set(0, "Z");

        assertEquals("Z", lista.get(0));
        assertEquals(1, chamadas.get());
    }

    @Test
    void clearRemoveTudo() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("A", "B", "C")));
        lista.clear();
        assertTrue(lista.isEmpty());
        assertEquals(0, lista.size());
    }

    @Test
    void addAllAdicionaVariosElementosComUmaNotificacao() {
        StateList<String> lista = new StateList<>(new ArrayList<>());
        AtomicInteger chamadas = new AtomicInteger(0);
        lista.addListener(chamadas::incrementAndGet);

        lista.addAll(List.of("X", "Y", "Z"));

        assertEquals(3, lista.size());
        assertEquals(1, chamadas.get());
    }

    @Test
    void containsVerificaPresenca() {
        StateList<String> lista = new StateList<>(List.of("A", "B"));
        assertTrue(lista.contains("A"));
        assertFalse(lista.contains("Z"));
    }

    @Test
    void multiplosListenersSaoNotificados() {
        StateList<String> lista = new StateList<>(new ArrayList<>());
        AtomicInteger c1 = new AtomicInteger(0);
        AtomicInteger c2 = new AtomicInteger(0);
        lista.addListener(c1::incrementAndGet);
        lista.addListener(c2::incrementAndGet);

        lista.add("X");

        assertEquals(1, c1.get());
        assertEquals(1, c2.get());
    }
}
