/**
 * 
 */
package uninsubria.client.guicontrollers;

import java.io.IOException;
import java.util.ResourceBundle;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import uninsubria.client.gui.Launcher;

/**
 * Controller class for the login screen.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class LoginController extends AbstractMainController {
	/*---Fields---*/
	@FXML Rectangle rect;
	@FXML VBox vbox;
	@FXML Glyph icon;
	@FXML JFXTextField userid_field;
	@FXML JFXPasswordField pw_field;
	@FXML HBox btn_box;
	@FXML JFXButton back_btn, login_btn;
	
	private Glyph back_arrow;
	private BooleanBinding fields_not_empty;

	/*---Constructors---*/
	
	/**
	 * Builds an object of type LoginController
	 */
	public LoginController() {
		super();
		back_arrow = new Glyph("FontAwesome", FontAwesome.Glyph.ARROW_LEFT);
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		icon.setFontFamily("FontAwesome");
		icon.setIcon(FontAwesome.Glyph.UNLOCK_ALT);
		icon.useGradientEffect();
		back_arrow.setAlignment(Pos.CENTER);
		back_btn.setGraphic(back_arrow);
		bindButtons();
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
		login_btn.setDisable(true);
		fields_not_empty = ((userid_field.textProperty().isNotEmpty()).and(pw_field.textProperty().isNotEmpty())).not();
		fields_not_empty.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				login_btn.setDisable(newValue);				
			}
			
		});
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
	}

	@Override
	protected void rescaleAll(double after) {
		super.rescaleAll(after);
		rescaleRect(after);
		rescaleFields(after);
		rescaleIcon(after);
	}
	
	/**
	 * Method that gets called when the "back" button is pressed. Simply loads the main menu screen.
	 * @throws IOException If loading was unsuccessful
	 */
	@FXML
	void back() throws IOException {
		Parent mainMenu = requestParent(ControllerType.MAIN_MENU);
		Timeline anim = sceneTransitionAnimation(mainMenu, SlideDirection.TO_RIGHT);
		anim.play();
	}
	
	/* ---- Private internal methods for rescaling ---- */
	private void rescaleRect(double after) {
		double widthAfter = (after*ref.getReferences().get("RECT_WIDTH")) / ref.getReferences().get("REF_RESOLUTION");
		double heigthAfter = (after*ref.getReferences().get("RECT_HEIGHT")) / ref.getReferences().get("REF_RESOLUTION");
		rect.setWidth(widthAfter); 
		rect.setHeight(heigthAfter);
		vbox.setPrefSize(widthAfter, heigthAfter);
		double newPadding = after*ref.getReferences().get("LOG_PADDING_TOP") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setPadding(new Insets(newPadding, 0, 0, 0));
		double newSpacing = after*ref.getReferences().get("LOG_SPACING") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setSpacing(newSpacing);
	}
	
	private void rescaleFields(double after) {
		double widthAfter = after*ref.getReferences().get("LOG_TXT_FIELD_W") / ref.getReferences().get("REF_RESOLUTION");
		double heightAfter = after*ref.getReferences().get("LOG_TXT_FIELD_H") / ref.getReferences().get("REF_RESOLUTION");
		userid_field.setPrefSize(widthAfter, heightAfter);
		pw_field.setPrefSize(widthAfter, heightAfter);
		btn_box.setPrefSize(widthAfter, after*ref.getReferences().get("LOG_BTN_H") / ref.getReferences().get("REF_RESOLUTION"));
		btn_box.setSpacing(after*ref.getReferences().get("LOG_BTN_SPACING") / ref.getReferences().get("REF_RESOLUTION"));
		double iconSizeAfter = after*ref.getReferences().get("LOG_BTN_ARROW_SIZE") / ref.getReferences().get("REF_RESOLUTION");
		back_arrow.setFontSize(iconSizeAfter);
	}
	
	private void rescaleIcon(double after) {
		double dimAfter = after*ref.getReferences().get("LOG_ICON_DIM") / ref.getReferences().get("REF_RESOLUTION");
		double sizeAfter = after*ref.getReferences().get("LOG_ICON_SIZE") / ref.getReferences().get("REF_RESOLUTION");
		icon.setPrefSize(dimAfter, dimAfter);
		icon.setFontSize(sizeAfter);
	}
	
	private void bindButtons() {
		login_btn.prefHeightProperty().bind(btn_box.prefHeightProperty());
		back_btn.prefHeightProperty().bind(btn_box.prefHeightProperty());
		DoubleBinding btnWidth = (btn_box.prefWidthProperty().subtract(btn_box.spacingProperty())).divide(2.0);
		login_btn.prefWidthProperty().bind(btnWidth);
		back_btn.prefWidthProperty().bind(btnWidth);
	}
}
