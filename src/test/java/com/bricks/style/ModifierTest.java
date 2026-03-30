package com.bricks.style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.style.Modifier;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ModifierTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveAplicarPaddingUniforme() {
        Modifier mod = new Modifier().padding(10);
        VBox vbox = new VBox();
        mod.applyTo(vbox);

        Insets padding = vbox.getPadding();
        assertEquals(10, padding.getTop());
        assertEquals(10, padding.getRight());
        assertEquals(10, padding.getBottom());
        assertEquals(10, padding.getLeft());
    }

    @Test
    void deveAplicarPaddingVerticalHorizontal() {
        Modifier mod = new Modifier().padding(10, 20);
        VBox vbox = new VBox();
        mod.applyTo(vbox);

        Insets padding = vbox.getPadding();
        assertEquals(10, padding.getTop());
        assertEquals(20, padding.getRight());
        assertEquals(10, padding.getBottom());
        assertEquals(20, padding.getLeft());
    }

    @Test
    void deveAplicarPaddingIndividual() {
        Modifier mod = new Modifier().padding(1, 2, 3, 4);
        VBox vbox = new VBox();
        mod.applyTo(vbox);

        Insets padding = vbox.getPadding();
        assertEquals(1, padding.getTop());
        assertEquals(2, padding.getRight());
        assertEquals(3, padding.getBottom());
        assertEquals(4, padding.getLeft());
    }

    @Test
    void deveAplicarDimensoes() {
        Modifier mod = new Modifier().width(200).height(100);
        VBox vbox = new VBox();
        mod.applyTo(vbox);

        assertEquals(200, vbox.getPrefWidth());
        assertEquals(100, vbox.getPrefHeight());
    }

    @Test
    void deveAplicarFontSizeALabel() {
        Modifier mod = new Modifier().fontSize(24);
        Label label = new Label("test");
        mod.applyTo(label);

        assertTrue(label.getStyle().contains("-fx-font-size: 24.0px"));
    }

    @Test
    void deveAplicarBoldALabel() {
        Modifier mod = new Modifier().bold();
        Label label = new Label("test");
        mod.applyTo(label);

        assertTrue(label.getStyle().contains("-fx-font-weight: bold"));
    }

    @Test
    void deveAplicarCorDeTextoALabel() {
        Modifier mod = new Modifier().textColor(Color.RED);
        Label label = new Label("test");
        mod.applyTo(label);

        assertTrue(label.getStyle().contains("-fx-text-fill: #ff0000"));
    }

    @Test
    void deveAplicarBackground() {
        Modifier mod = new Modifier().background(Color.BLUE);
        VBox vbox = new VBox();
        mod.applyTo(vbox);

        assertTrue(vbox.getStyle().contains("-fx-background-color"));
    }

    @Test
    void deveSuportarEncadeamento() {
        Modifier mod = new Modifier()
            .padding(10)
            .width(200)
            .height(100)
            .fontSize(16)
            .bold()
            .textColor(Color.BLACK)
            .background(Color.WHITE);

        assertNotNull(mod);
        assertEquals(16, mod.getFontSize());
        assertTrue(mod.isBold());
        assertEquals(Color.BLACK, mod.getTextColor());
    }
}
