package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXSpinner;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Helper class for showing an overlay with a spinner animation while a background task is being executed.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class LoadingAnimationOverlay {
    /*---Fields---*/
    private StackPane overlayContainer;
    private JFXSpinner spinner;
    private Pane root;

    private final String OVERLAY_STYLE_ID = "bg-loading";

    /*---Constructors---*/
    public LoadingAnimationOverlay(Pane root) {
        this.overlayContainer = new StackPane();
        overlayContainer.setId(OVERLAY_STYLE_ID);
        this.spinner = new JFXSpinner();
        spinner.setRadius(100);
        overlayContainer.getChildren().add(spinner);
        this.root = root;
    }

    /*---Methods---*/
    public void playAnimation() {
        root.getChildren().add(overlayContainer);
    }

    public void stopAnimation() {
        root.getChildren().remove(overlayContainer);
    }
}
