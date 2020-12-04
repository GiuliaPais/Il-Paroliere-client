package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.Resolution;

/**
 * Abstract implementation of a controller that refers to the main scene.
 * @author Giulia Pais
 * @version 0.9.1
 *
 */
public abstract class AbstractMainController extends AbstractController {
	/*---Fields---*/
	@FXML AnchorPane rootContainer;
	@FXML Pane root;
	private double xOffset = 0;
    private double yOffset = 0;
    protected JFXSnackbar notificationPane;
	
    /**
     * Internal enum class for sliding transition direction.
     * @author Giulia Pais
     *
     */
    enum SlideDirection {
    	TO_BOTTOM, TO_TOP, TO_LEFT, TO_RIGHT;
    }

	/*---Constructors---*/
    /**
     * Builds an object of type AbstractMainController
     */
	public AbstractMainController() {
		super();
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		setUndecoratedMovable(!Launcher.contrManager.getSettings().isFullscreen());
		notificationPane = new JFXSnackbar(root);
		root.setPrefSize(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0], Launcher.contrManager.getCurrentresolution().getWidthHeight()[1]);
		notificationPane.setPrefWidth(root.getPrefWidth()/3);
		rootContainer.setPrefSize(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0], Launcher.contrManager.getCurrentresolution().getWidthHeight()[1]);
		Launcher.contrManager.getSettings().fullscreenProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setUndecoratedMovable(!newValue);				
			}
			
		});
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
		Launcher.contrManager.currentresolutionBinding().addListener(new ChangeListener<Resolution>() {
			@Override
			public void changed(ObservableValue<? extends Resolution> observable, Resolution oldValue,
					Resolution newValue) {
				root.setPrefSize(newValue.getWidthHeight()[0], newValue.getWidthHeight()[1]);
				notificationPane.setPrefWidth(root.getPrefWidth()/3);
				rootContainer.setPrefSize(newValue.getWidthHeight()[0], newValue.getWidthHeight()[1]);
			}
			
		});
	}
	
	@Override
	protected void rescaleAll(double after) {
		scaleFontSize(after);
	};
	
	
	@Override
	protected void scaleFontSize(double after) {
		this.currentFontSize.set(after*ref.getReferences().get("FONT_SIZE") / ref.getReferences().get("REF_RESOLUTION"));
		root.setStyle("-fx-font-size: "+ currentFontSize.get() + "px;");
	}
	
	private void setUndecoratedMovable(boolean movable) {
		if (movable) {
			/*Make undecorated stage movable*/
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = event.getSceneX();
	                yOffset = event.getSceneY();
	            }
	        });
	        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                Launcher.mainStage.setX(event.getScreenX() - xOffset);
	                Launcher.mainStage.setY(event.getScreenY() - yOffset);
	            }
	        });
		} else {
			root.setOnMousePressed(null);
			root.setOnMouseDragged(null);
		}
	}
	
	/**
	 * Creates a sliding transition between scenes.
	 * @param newSceneContent The new Parent to load on change request
	 * @param direction Direction of the sliding animation
	 * @return a Timeline object
	 */
	protected Timeline sceneTransitionAnimation(Parent newSceneContent, SlideDirection direction) {
		Timeline animation = new Timeline();
		KeyValue keyVal;
		KeyFrame keyFrame;
		Scene scene = rootContainer.getScene();
		switch(direction) {
		case TO_BOTTOM:
			newSceneContent.translateYProperty().set(-scene.getHeight());
			rootContainer.getChildren().add(newSceneContent);
			keyVal = new KeyValue(newSceneContent.translateYProperty(), 0, Interpolator.EASE_IN);
			keyFrame = new KeyFrame(Duration.millis(300), keyVal);
			animation.getKeyFrames().add(keyFrame);
			animation.setOnFinished(t -> {
				rootContainer.getChildren().remove(root);
			});
			break;
		case TO_LEFT:
			newSceneContent.translateXProperty().set(scene.getWidth());
			rootContainer.getChildren().add(newSceneContent);
			keyVal = new KeyValue(newSceneContent.translateXProperty(), 0, Interpolator.EASE_IN);
			keyFrame = new KeyFrame(Duration.millis(300), keyVal);
			animation.getKeyFrames().add(keyFrame);
			animation.setOnFinished(t -> {
				rootContainer.getChildren().remove(root);
			});
			break;
		case TO_RIGHT:
			newSceneContent.translateXProperty().set(-scene.getWidth());
			rootContainer.getChildren().add(newSceneContent);
			keyVal = new KeyValue(newSceneContent.translateXProperty(), 0, Interpolator.EASE_IN);
			keyFrame = new KeyFrame(Duration.millis(300), keyVal);
			animation.getKeyFrames().add(keyFrame);
			animation.setOnFinished(t -> {
				rootContainer.getChildren().remove(root);
			});
			break;
		case TO_TOP:
			newSceneContent.translateYProperty().set(scene.getHeight());
			rootContainer.getChildren().add(newSceneContent);
			keyVal = new KeyValue(newSceneContent.translateYProperty(), 0, Interpolator.EASE_IN);
			keyFrame = new KeyFrame(Duration.millis(300), keyVal);
			animation.getKeyFrames().add(keyFrame);
			animation.setOnFinished(t -> {
				rootContainer.getChildren().remove(root);
			});
			break;
		}
		return animation;
	}

	protected void notification(String message, Duration duration) {
		JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
		JFXSnackbar.SnackbarEvent event = new JFXSnackbar.SnackbarEvent(layout, duration);
		notificationPane.enqueue(event);
	}
}
