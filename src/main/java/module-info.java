module fabiorodrigues.bricks {
    requires javafx.controls;
    requires javafx.graphics;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    exports fabiorodrigues.bricks.core;
    exports fabiorodrigues.bricks.components;
    exports fabiorodrigues.bricks.style;
    exports fabiorodrigues.bricks.data;
    exports fabiorodrigues.bricks.data.config;
}
