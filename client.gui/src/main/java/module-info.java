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
	requires utils.connection;
	requires utils.managers.api;
    requires utils.serviceResults;
    requires utils.dictionary;
    requires java.desktop;

    opens uninsubria.client.gui to javafx.fxml;
    opens uninsubria.client.guicontrollers to javafx.fxml;
	opens uninsubria.client.customcontrols to javafx.graphics, javafx.fxml;
	exports uninsubria.client.gui;
}