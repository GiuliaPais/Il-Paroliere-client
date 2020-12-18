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
	requires utils.business;
    requires utils.ruleset;

    opens uninsubria.client.gui to javafx.fxml;
    opens uninsubria.client.guicontrollers to javafx.fxml;
    opens uninsubria.client.customcontrols to javafx.graphics;
    exports uninsubria.client.gui;
}