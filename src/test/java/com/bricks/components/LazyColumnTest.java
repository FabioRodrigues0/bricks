package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.LazyColumn;
import fabiorodrigues.bricks.components.Text;
import fabiorodrigues.bricks.core.StateList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class LazyColumnTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveRenderizarWrapperVBox() {
        Node node = new LazyColumn<String>()
            .items(List.of("A", "B"))
            .item(s -> new Text(s))
            .render();

        assertInstanceOf(VBox.class, node);
    }

    @Test
    void wrapperContemListView() {
        VBox wrapper = (VBox) new LazyColumn<String>()
            .items(List.of("A", "B"))
            .item(s -> new Text(s))
            .render();

        assertEquals(1, wrapper.getChildren().size());
        assertInstanceOf(ListView.class, wrapper.getChildren().get(0));
    }

    @Test
    void listaVaziaComEmptyStateMostraEmptyState() {
        Node node = new LazyColumn<String>()
            .items(List.of())
            .emptyState(new Text("Vazio"))
            .item(s -> new Text(s))
            .render();

        assertInstanceOf(javafx.scene.control.Label.class, node);
    }

    @Test
    void listaVaziaSemEmptyStateMostraVBox() {
        Node node = new LazyColumn<String>()
            .items(List.of())
            .item(s -> new Text(s))
            .render();

        assertInstanceOf(VBox.class, node);
    }

    @Test
    void aceitaStateList() {
        StateList<String> lista = new StateList<>(new ArrayList<>(List.of("X", "Y")));

        VBox wrapper = (VBox) new LazyColumn<String>()
            .items(lista)
            .item(s -> new Text(s))
            .render();

        ListView<?> lv = (ListView<?>) wrapper.getChildren().get(0);
        assertEquals(2, lv.getItems().size());
    }

    @Test
    void itemHeightDefineFixedCellSize() {
        VBox wrapper = (VBox) new LazyColumn<String>()
            .items(List.of("A"))
            .itemHeight(80)
            .item(s -> new Text(s))
            .render();

        ListView<?> lv = (ListView<?>) wrapper.getChildren().get(0);
        assertEquals(80, lv.getFixedCellSize());
    }
}
