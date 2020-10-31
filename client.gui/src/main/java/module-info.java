module uninsubria.client.gui {
    requires javafx.controls;
    requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.prefs;
	requires transitive client.core;
	requires javafx.base;
	requires utils.languages;
	requires java.base;
	requires org.controlsfx.controls;
	requires com.jfoenix;

    opens uninsubria.client.gui to javafx.fxml;
    opens uninsubria.client.guicontrollers to javafx.fxml;
    exports uninsubria.client.gui;
}