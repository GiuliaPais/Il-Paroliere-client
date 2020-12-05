package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.comm.ProxyServer;
import uninsubria.client.gui.Launcher;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the login screen.
 * @author Giulia Pais
 * @version 0.9.2
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

	private StringProperty required_valid_error, pw_length_valid_error, notif_connection_loss;

	/*---Constructors---*/
	
	/**
	 * Builds an object of type LoginController
	 */
	public LoginController() {
		super();
		back_arrow = new Glyph("FontAwesome", FontAwesome.Glyph.ARROW_LEFT);
		this.required_valid_error = new SimpleStringProperty();
		this.pw_length_valid_error = new SimpleStringProperty();
		this.notif_connection_loss = new SimpleStringProperty();
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
		setValidators();
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
		if (!Launcher.manager.isConnected()) {
			login_btn.setDisable(true);
		}
		Launcher.manager.proxyProperty().addListener(new ChangeListener<ProxyServer>() {
			@Override
			public void changed(ObservableValue<? extends ProxyServer> observable, ProxyServer oldValue, ProxyServer newValue) {
				if (newValue == null) {
					login_btn.setDisable(true);
				} else {
					login_btn.setDisable(false);
				}
			}
		});
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		required_valid_error.set(resBundle.getString("required_err_label"));
		pw_length_valid_error.set(resBundle.getString("invalid_pw_length_error"));
		notif_connection_loss.set(resBundle.getString("connection_lost_notif"));
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

	@FXML
	void login() {
		if (!validateAll()) {
			return;
		}
		LoadingAnimationOverlay animation = new LoadingAnimationOverlay(root);
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				List<String> errMsgs = null;
				try {
					errMsgs = Launcher.manager.login(userid_field.getText(), pw_field.getText());
				} catch (Exception e) {
					if (e instanceof SocketException) {
						Platform.runLater(() -> {
							animation.stopAnimation();
							notification(notif_connection_loss.get(), new Duration(8000));
						});
						boolean reconnected = false;
						Launcher.manager.setDisconnected();
						for (int i = 0; i < 3; i++) {
							String ip = Launcher.manager.tryConnectServer();
							if (ip != null) {
								reconnected = true;
								break;
							}
						}
						if (!reconnected) {
							Platform.runLater(() -> {
								animation.stopAnimation();
								serverAlert((StackPane) root, rect.getWidth()).show();
							});
							return null;
						}
					}
				}
				List<Text> localized = new ArrayList<>();
				if (errMsgs.isEmpty()) {
					Platform.runLater(() -> {
						animation.stopAnimation();
						Parent p = null;
						try {
							p = requestParent(ControllerType.HOME_VIEW);
							Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_LEFT);
							anim.play();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					return null;
				}
				for (String err : errMsgs) {
					String localMsg = Launcher.contrManager.getBundleValue().getString(err);
					Text text = new Text(localMsg + "\n");
					text.setFill(Color.RED);
					localized.add(text);
				}
				Platform.runLater(() -> {
					animation.stopAnimation();
					JFXDialogLayout dialogLayout = new JFXDialogLayout();
					JFXDialog dialog = new JFXDialog((StackPane) root, dialogLayout, JFXDialog.DialogTransition.CENTER);
					JFXButton okBtn = new JFXButton("OK");
					okBtn.setOnAction(e -> dialog.close());
					dialogLayout.setHeading(new Label("Error"));
					dialogLayout.setActions(okBtn);
					for (Text t: localized) {
						dialogLayout.getBody().add(t);
					}
					dialog.show();
				});
				return null;
			}

			@Override
			protected void scheduled() {
				super.scheduled();
				Platform.runLater(() -> animation.playAnimation());
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
	
	/* ---- Private internal methods for rescaling ---- */
	private void rescaleRect(double after) {
		double widthAfter = (after*ref.getReferences().get("RECT_WIDTH")) / ref.getReferences().get("REF_RESOLUTION");
		double heightAfter = (after*ref.getReferences().get("RECT_HEIGHT")) / ref.getReferences().get("REF_RESOLUTION");
		rect.setWidth(widthAfter); 
		rect.setHeight(heightAfter);
		vbox.setPrefSize(widthAfter, heightAfter);
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

	private void setValidators() {
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.messageProperty().bind(required_valid_error);
		RegexValidator pwLengthValidator = new RegexValidator();
		pwLengthValidator.setRegexPattern(".{5,}");
		pwLengthValidator.messageProperty().bind(pw_length_valid_error);
		userid_field.setValidators(validator);
		pw_field.setValidators(validator, pwLengthValidator);
	}

	private boolean validateAll() {
		boolean allValid = true;
		allValid = allValid & userid_field.validate();
		allValid = allValid & pw_field.validate();
		return allValid;
	}
}
