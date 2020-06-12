module it.uninsubria.labB717304.clientIP {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens it.uninsubria.labB717304.clientIP to javafx.fxml;
    exports it.uninsubria.labB717304.clientIP;
}