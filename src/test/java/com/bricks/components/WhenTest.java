package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Column;
import fabiorodrigues.bricks.components.Text;
import fabiorodrigues.bricks.components.When;
import fabiorodrigues.bricks.core.State;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WhenTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void naoRenderizaQuandoCondicaoFalsa() {
        Node node = new When(false)
            .children(new Text("Escondido"))
            .render();

        assertNull(node);
    }

    @Test
    void renderizaFilhoUnicoQuandoCondicaoVerdadeira() {
        Node node = new When(true)
            .children(new Text("Visivel"))
            .render();

        assertInstanceOf(Label.class, node);
        assertEquals("Visivel", ((Label) node).getText());
    }

    @Test
    void renderizaMultiplosFilhosNumWrapper() {
        Node node = new When(true)
            .children(new Text("A"), new Text("B"))
            .render();

        assertInstanceOf(VBox.class, node);
        assertEquals(2, ((VBox) node).getChildren().size());
    }

    @Test
    void containerIgnoraQuandoFalsoSemAdicionarEspaco() {
        VBox vbox = (VBox) new Column()
            .children(
                new Text("A"),
                new When(false).children(new Text("B")),
                new Text("C")
            )
            .render();

        assertEquals(2, vbox.getChildren().size());
    }

    @Test
    void avaliaStateNoMomentoDoRender() {
        State<Boolean> mostrar = new State<>(false);
        When when = new When(mostrar).children(new Text("Detalhes"));

        assertNull(when.render());

        mostrar.set(true);

        assertInstanceOf(Label.class, when.render());
    }

    @Test
    void avaliaSupplierNoMomentoDoRender() {
        State<String> email = new State<>("");
        When when = new When(() -> email.get().contains("@"))
            .then(new Text("Email valido"));

        assertNull(when.render());

        email.set("fabio@example.com");

        assertInstanceOf(Label.class, when.render());
    }
}
