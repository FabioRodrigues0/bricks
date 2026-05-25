package com.bricks.components;

import static org.junit.jupiter.api.Assertions.*;

import com.bricks.JavaFXInit;
import fabiorodrigues.bricks.components.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ImageTest {

    private static final byte[] PNG_1X1 = Base64.getDecoder().decode(
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII="
    );

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveCarregarFicheiroLocalSemBackgroundLoading() throws IOException {
        Path file = Files.createTempFile("bricks-image-test", ".png");
        Files.write(file, PNG_1X1);

        ImageView view = (ImageView) new Image(file.toUri().toString()).render();

        assertFalse(view.getImage().isBackgroundLoading());
    }

    @Test
    void devePermitirForcarBackgroundLoading() throws IOException {
        Path file = Files.createTempFile("bricks-image-test", ".png");
        Files.write(file, PNG_1X1);

        ImageView view = (ImageView) new Image(file.toUri().toString())
            .backgroundLoading(true)
            .render();

        assertTrue(view.getImage().isBackgroundLoading());
    }
}
