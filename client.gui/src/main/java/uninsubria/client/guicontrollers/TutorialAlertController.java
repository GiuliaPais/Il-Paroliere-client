package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.util.ResourceBundle;

/**
 * Controller for tutorial alert.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class TutorialAlertController extends AbstractCustomAlertController {
    /*---Fields---*/
    @FXML StackPane view1, view2, view3, view4;
    @FXML AnchorPane centralPane;
    @FXML JFXButton backBtn, nextBtn;

    private int selectedIndex;
    private Glyph nextGlyph, backGlyph;

    /*---Constructors---*/
    public TutorialAlertController() {
        selectedIndex = 0;
        nextGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.ANGLE_RIGHT);
        backGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.ANGLE_LEFT);
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        nextBtn.setGraphic(nextGlyph);
        backBtn.setGraphic(backGlyph);
    }

    @FXML void next() {
        translateAnimationForward(selectedIndex);
        selectedIndex = selectedIndex == 3 ? 0 : selectedIndex + 1;
    }

    @FXML void back() {
        translateAnimationBackward(selectedIndex);
        selectedIndex = selectedIndex == 0 ? 3 : selectedIndex - 1;
    }

    private void translateAnimationForward(int index) {
        StackPane toLoad = getByIndex((index+1) % 4);
        Rectangle clip = new Rectangle(centralPane.getWidth(), centralPane.getHeight());
        clip.translateXProperty().set(0);
        clip.setWidth(0);
        toLoad.setClip(clip);
        toLoad.translateXProperty().set(centralPane.getWidth());
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        toLoad.toFront();
        final KeyValue kv0 = new KeyValue(toLoad.visibleProperty(), true);
        final KeyValue kv1 = new KeyValue(clip.translateXProperty(), 0);
        final KeyValue kv2 = new KeyValue(toLoad.translateXProperty(), 0);
        final KeyValue kv3 = new KeyValue(clip.widthProperty(), centralPane.getWidth());
        final KeyFrame kf = new KeyFrame(Duration.millis(200), kv1, kv2, kv3, kv0);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void translateAnimationBackward(int index) {
        StackPane toLoad = getByIndex((index+3) % 4);
        Rectangle clip = new Rectangle(centralPane.getWidth(), centralPane.getHeight());
        clip.translateXProperty().set(centralPane.getWidth());
        clip.setWidth(0);
        toLoad.setClip(clip);
        toLoad.translateXProperty().set(-centralPane.getWidth());
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        toLoad.toFront();
        final KeyValue kv0 = new KeyValue(toLoad.visibleProperty(), true);
        final KeyValue kv1 = new KeyValue(clip.translateXProperty(), 0);
        final KeyValue kv2 = new KeyValue(toLoad.translateXProperty(), 0);
        final KeyValue kv3 = new KeyValue(clip.widthProperty(), centralPane.getWidth());
        final KeyFrame kf = new KeyFrame(Duration.millis(200), kv1, kv2, kv3, kv0);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private StackPane getByIndex(int i) {
        return switch(i) {
            case 1 -> view2;
            case 2 -> view3;
            case 3 -> view4;
            default -> view1;
        };
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
    }

}
