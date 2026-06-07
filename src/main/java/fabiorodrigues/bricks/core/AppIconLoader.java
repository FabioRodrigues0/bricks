package fabiorodrigues.bricks.core;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;

final class AppIconLoader {

    private AppIconLoader() {}

    static Image loadFxImage(String path) {
        if (isBlank(path)) {
            return null;
        }

        try {
            URL url = resolveUrl(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
        } catch (Exception ignored) {
            // fallback below
        }

        return null;
    }

    static java.awt.Image loadAwtImage(String path) {
        if (isBlank(path)) {
            return fallbackAwtImage();
        }

        try {
            URL url = resolveUrl(path);
            if (url != null) {
                java.awt.Image image = ImageIO.read(url);
                if (image != null) {
                    return image;
                }
                return Toolkit.getDefaultToolkit().getImage(url);
            }
        } catch (Exception ignored) {
            // fallback below
        }

        return fallbackAwtImage();
    }

    private static URL resolveUrl(String path) {
        String trimmed = path.trim();

        URL classpathUrl = resolveClasspathUrl(trimmed);
        if (classpathUrl != null) {
            return classpathUrl;
        }

        try {
            URI uri = URI.create(trimmed);
            if (uri.isAbsolute()) {
                return uri.toURL();
            }
        } catch (Exception ignored) {
            // not a URI
        }

        try {
            File file = new File(trimmed);
            if (file.exists()) {
                return file.toURI().toURL();
            }
        } catch (Exception ignored) {
            // unresolved
        }

        return null;
    }

    private static URL resolveClasspathUrl(String path) {
        URL url = AppIconLoader.class.getResource(path);
        if (url != null) {
            return url;
        }

        String normalized = path.startsWith("/") ? path.substring(1) : path;
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            url = contextLoader.getResource(normalized);
            if (url != null) {
                return url;
            }
        }

        return AppIconLoader.class.getClassLoader().getResource(normalized);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static java.awt.Image fallbackAwtImage() {
        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    }
}
