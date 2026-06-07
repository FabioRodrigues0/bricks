package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Button;
import fabiorodrigues.bricks.components.Row;
import fabiorodrigues.bricks.components.TextField;
import fabiorodrigues.bricks.core.Component;
import fabiorodrigues.bricks.style.Modifier;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RowTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarHBox() {
        Node node = new Row().render();
        assertInstanceOf(HBox.class, node);
    }

    @Test
    void deveAplicarGap() {
        HBox hbox = (HBox) new Row().gap(8).render();
        assertEquals(8, hbox.getSpacing());
    }

    @Test
    void deveRenderizarFilhos() {
        HBox hbox = (HBox) new Row()
            .children(new Button("A"), new Button("B"))
            .render();

        assertEquals(2, hbox.getChildren().size());
    }

    @Test
    void deveAplicarHgrowQuandoFilhoTemMaxWidthInfinito() {
        Component fillChild = () -> {
            Region region = new Region();
            region.setMaxWidth(Double.MAX_VALUE);
            return region;
        };

        HBox hbox = (HBox) new Row()
            .children(fillChild)
            .render();

        assertEquals(Priority.ALWAYS, HBox.getHgrow(hbox.getChildren().get(0)));
    }

    @Test
    void deveExpandirTextFieldComLabelEFillMaxWidth() {
        HBox hbox = (HBox) new Row()
            .children(new TextField().modifier(new Modifier().fillMaxWidth()).label("A"))
            .render();

        Node child = hbox.getChildren().get(0);
        assertEquals(Double.MAX_VALUE, ((Region) child).getMaxWidth());
        assertEquals(Priority.ALWAYS, HBox.getHgrow(child));
    }
}
