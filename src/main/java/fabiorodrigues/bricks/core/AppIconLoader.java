package fabiorodrigues.bricks.core;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

final class AppIconLoader {

    private AppIconLoader() {}

    static Image loadFxImage(String path) {
        if (isBlank(path)) {
            return null;
        }

        try {
            URL url = resolveUrl(path);
            if (url != null) {
                Image image = new Image(url.toExternalForm(), false);
                return trimTransparentPadding(image);
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
                BufferedImage image = ImageIO.read(url);
                if (image != null) {
                    return trimTransparentPadding(image);
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

    private static Image trimTransparentPadding(Image image) {
        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return image;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        Bounds bounds = visibleBounds(width, height, (x, y) -> reader.getArgb(x, y));
        if (bounds == null || bounds.covers(width, height)) {
            return image;
        }

        return new WritableImage(
            reader,
            bounds.minX(),
            bounds.minY(),
            bounds.width(),
            bounds.height()
        );
    }

    private static BufferedImage trimTransparentPadding(BufferedImage image) {
        Bounds bounds = visibleBounds(image.getWidth(), image.getHeight(), image::getRGB);
        if (bounds == null || bounds.covers(image.getWidth(), image.getHeight())) {
            return image;
        }

        return image.getSubimage(
            bounds.minX(),
            bounds.minY(),
            bounds.width(),
            bounds.height()
        );
    }

    private static Bounds visibleBounds(int width, int height, PixelSource source) {
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isVisible(source.argbAt(x, y))) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return null;
        }

        return new Bounds(minX, minY, maxX, maxY);
    }

    private static boolean isVisible(int argb) {
        int alpha = (argb >>> 24) & 0xff;
        return alpha > 8;
    }

    private static java.awt.Image fallbackAwtImage() {
        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    }

    @FunctionalInterface
    private interface PixelSource {
        int argbAt(int x, int y);
    }

    private record Bounds(int minX, int minY, int maxX, int maxY) {
        int width() {
            return maxX - minX + 1;
        }

        int height() {
            return maxY - minY + 1;
        }

        boolean covers(int width, int height) {
            return minX == 0 && minY == 0 && width() == width && height() == height;
        }
    }
}
