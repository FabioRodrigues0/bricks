package fabiorodrigues.bricks.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bricks.JavaFXInit;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppIconLoaderTest {

    @BeforeAll
    static void setup() {
        JavaFXInit.init();
    }

    @Test
    void deveCortarPaddingTransparenteNoIconeJavaFx() throws IOException {
        Path file = createPaddedIcon();

        Image icon = AppIconLoader.loadFxImage(file.toString());

        assertEquals(6, icon.getWidth());
        assertEquals(4, icon.getHeight());
    }

    @Test
    void deveCortarPaddingTransparenteNoIconeAwt() throws IOException {
        Path file = createPaddedIcon();

        java.awt.Image icon = AppIconLoader.loadAwtImage(file.toString());

        assertEquals(6, icon.getWidth(null));
        assertEquals(4, icon.getHeight(null));
    }

    private static Path createPaddedIcon() throws IOException {
        BufferedImage image = new BufferedImage(12, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(new Color(0, 120, 255, 255));
            graphics.fillRect(3, 2, 6, 4);
        } finally {
            graphics.dispose();
        }

        Path file = Files.createTempFile("bricks-padded-icon", ".png");
        ImageIO.write(image, "png", file.toFile());
        return file;
    }
}
