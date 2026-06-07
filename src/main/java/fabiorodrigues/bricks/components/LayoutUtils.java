package fabiorodrigues.bricks.components;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class LayoutUtils {

    private LayoutUtils() {
    }

    static void applyExpansionToWrapper(Region source, Region wrapper) {
        if (source.getMaxWidth() == Double.MAX_VALUE) {
            wrapper.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(wrapper, Priority.ALWAYS);
        }
        if (source.getMaxHeight() == Double.MAX_VALUE) {
            wrapper.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(wrapper, Priority.ALWAYS);
        }
    }
}
