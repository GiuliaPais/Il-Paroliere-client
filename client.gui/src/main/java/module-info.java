module uninsubria.client.gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens uninsubria.client.gui to javafx.fxml;
    exports uninsubria.client.gui;
}