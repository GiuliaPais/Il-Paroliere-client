package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXSpinner;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Helper class for showing an overlay with a spinner animation while a background task is being executed.
 * @author Giulia Pais
 * @version 0.9.1
 */
public class LoadingAnimationOverlay {
    /*---Fields---*/
    private StackPane overlayContainer;
    private VBox vbox;
    private Pane root;

    private final String OVERLAY_STYLE_ID = "bg-loading";

    /*---Constructors---*/
    public LoadingAnimationOverlay(Pane root, String label) {
        init(label);
        this.root = root;
    }

    /*---Methods---*/
    public void playAnimation() {
        root.getChildren().add(overlayContainer);
    }

    public void stopAnimation() {
        root.getChildren().remove(overlayContainer);
    }

    private void init(String lbl) {
        overlayContainer = new StackPane();
        overlayContainer.setId(OVERLAY_STYLE_ID);
        overlayContainer.setAlignment(Pos.CENTER);
        vbox = new VBox();
        JFXSpinner spinner = new JFXSpinner();
        spinner.setRadius(200);
        Label label = new Label(lbl);
        vbox.getChildren().addAll(spinner, label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);
        overlayContainer.getChildren().add(vbox);
    }
}
