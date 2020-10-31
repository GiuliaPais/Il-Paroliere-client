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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import uninsubria.client.gui.Launcher;

/**
 * Controller class for the registration screen.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class RegistrationController extends AbstractMainController {
	/*---Fields---*/
	@FXML Rectangle rect;
	@FXML VBox vbox;
	@FXML Glyph icon;
	@FXML JFXTextField name_field, lastName_field, userID_field, email_field;
	@FXML JFXPasswordField pw_field;
	@FXML HBox btn_box;
	@FXML JFXButton back_btn, register_btn;
	
	private StringProperty name_txt, lastName_txt, reg_btn_txt;
	private Glyph back_arrow;

	/*---Constructors---*/
	/**
	 * Builds an object of type RegistrationController
	 */
	public RegistrationController() {
		super();
		this.name_txt = new SimpleStringProperty();
		this.lastName_txt = new SimpleStringProperty();
		this.reg_btn_txt = new SimpleStringProperty();
		back_arrow = new Glyph("FontAwesome", FontAwesome.Glyph.ARROW_LEFT);
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		icon.setFontFamily("FontAwesome");
		icon.setIcon(FontAwesome.Glyph.USER);
		back_arrow.setAlignment(Pos.CENTER);
		back_btn.setGraphic(back_arrow);
		bindText();
		bindButtons();
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
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
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		name_txt.set(resBundle.getString("prompt_name"));
		lastName_txt.set(resBundle.getString("prompt_lastName"));
		reg_btn_txt.set(resBundle.getString("register_reg_btn"));
	}
	
	@Override
	protected void rescaleAll(double after) {
		super.rescaleAll(after);
		rescaleRect(after);
		rescaleFields(after);
		rescaleIcon(after);
	}
	
	/* ---- Private internal methods for rescaling ---- */
	private void bindText() {
		name_field.promptTextProperty().bind(name_txt);
		lastName_field.promptTextProperty().bind(lastName_txt);
		register_btn.textProperty().bind(reg_btn_txt);
	}
	
	private void rescaleRect(double after) {
		double widthAfter = (after*ref.getReferences().get("RECT_WIDTH")) / ref.getReferences().get("REF_RESOLUTION");
		double heigthAfter = (after*ref.getReferences().get("RECT_HEIGHT")) / ref.getReferences().get("REF_RESOLUTION");
		rect.setWidth(widthAfter); 
		rect.setHeight(heigthAfter);
		vbox.setPrefSize(widthAfter, heigthAfter);
		double newPadding = after*ref.getReferences().get("REG_PADDING_TOP") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setPadding(new Insets(newPadding, 0, 0, 0));
		double newSpacing = after*ref.getReferences().get("REG_SPACING") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setSpacing(newSpacing);
	}
	
	private void rescaleFields(double after) {
		double widthAfter = after*ref.getReferences().get("LOG_TXT_FIELD_W") / ref.getReferences().get("REF_RESOLUTION");
		double heightAfter = after*ref.getReferences().get("LOG_TXT_FIELD_H") / ref.getReferences().get("REF_RESOLUTION");
		userID_field.setPrefSize(widthAfter, heightAfter);
		name_field.setPrefSize(widthAfter, heightAfter);
		lastName_field.setPrefSize(widthAfter, heightAfter);
		email_field.setPrefSize(widthAfter, heightAfter);
		pw_field.setPrefSize(widthAfter, heightAfter);
		btn_box.setPrefSize(widthAfter, after*ref.getReferences().get("LOG_BTN_H") / ref.getReferences().get("REF_RESOLUTION"));
		btn_box.setSpacing(after*ref.getReferences().get("LOG_BTN_SPACING") / ref.getReferences().get("REF_RESOLUTION"));
		double iconSizeAfter = after*ref.getReferences().get("LOG_BTN_ARROW_SIZE") / ref.getReferences().get("REF_RESOLUTION");
		back_arrow.setFontSize(iconSizeAfter);
	}
	
	private void rescaleIcon(double after) {
		double dimAfter = after*ref.getReferences().get("REG_ICON_DIM") / ref.getReferences().get("REF_RESOLUTION");
		double sizeAfter = after*ref.getReferences().get("REG_ICON_SIZE") / ref.getReferences().get("REF_RESOLUTION");
		icon.setPrefSize(dimAfter, dimAfter);
		icon.setFontSize(sizeAfter);
	}
	
	private void bindButtons() {
		register_btn.prefHeightProperty().bind(btn_box.prefHeightProperty());
		back_btn.prefHeightProperty().bind(btn_box.prefHeightProperty());
		DoubleBinding btnWidth = (btn_box.prefWidthProperty().subtract(btn_box.spacingProperty())).divide(2.0);
		register_btn.prefWidthProperty().bind(btnWidth);
		back_btn.prefWidthProperty().bind(btnWidth);
	}
}
