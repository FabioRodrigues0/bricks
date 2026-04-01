package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Card;
import fabiorodrigues.bricks.components.Text;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CardTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarVBox() {
        Node node = new Card().render();
        assertInstanceOf(VBox.class, node);
    }

    @Test
    void deveAplicarElevacaoComDropShadow() {
        VBox vbox = (VBox) new Card().elevation(2).render();
        assertInstanceOf(DropShadow.class, vbox.getEffect());
    }

    @Test
    void semElevacaoNaoTemSombra() {
        VBox vbox = (VBox) new Card().elevation(0).render();
        assertNull(vbox.getEffect());
    }

    @Test
    void deveAplicarCornerRadiusNoStyle() {
        VBox vbox = (VBox) new Card().cornerRadius(12).render();
        assertTrue(vbox.getStyle().contains("-fx-background-radius: 12.0"));
    }

    @Test
    void deveAplicarBackgroundNoStyle() {
        VBox vbox = (VBox) new Card().background(Color.LIGHTGRAY).render();
        assertTrue(vbox.getStyle().contains("-fx-background-color: #d3d3d3"));
    }

    @Test
    void comMarginEnvolveEmStackPane() {
        Node node = new Card().margin(8).render();
        assertInstanceOf(StackPane.class, node);
    }

    @Test
    void semMarginRetornaVBoxDirectamente() {
        Node node = new Card().render();
        assertInstanceOf(VBox.class, node);
    }

    @Test
    void deveAdicionarFilhos() {
        VBox vbox = (VBox) new Card()
            .children(new Text("A"), new Text("B"))
            .render();
        assertEquals(2, vbox.getChildren().size());
    }

    @Test
    void deveDimensionarComWidthHeight() {
        VBox vbox = (VBox) new Card().width(300).height(200).render();
        assertEquals(300, vbox.getPrefWidth());
        assertEquals(200, vbox.getPrefHeight());
    }
}
