module visualizer {
    requires javafx.controls;
    requires java.desktop;
    requires TarsosDSP.core;
    requires TarsosDSP.jvm;
    requires org.slf4j;
    requires javafx.fxml;
    requires hipparchus.core;

    exports com.lazydash.audio.visualizer to javafx.graphics;

    exports com.lazydash.audio.visualizer.ui.fxml.settings to javafx.fxml;
    opens com.lazydash.audio.visualizer.ui.fxml.settings to javafx.fxml;

    exports com.lazydash.audio.visualizer.ui.fxml.settings.categories to javafx.fxml;
    opens com.lazydash.audio.visualizer.ui.fxml.settings.categories to javafx.fxml;
}