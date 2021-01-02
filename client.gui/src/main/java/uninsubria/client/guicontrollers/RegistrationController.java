package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.Launcher;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the registration screen.
 * @author Giulia Pais
 * @version 0.9.4
 *
 */
public class RegistrationController extends AbstractMainController {
	/*---Fields---*/
	@FXML Rectangle rect;
	@FXML VBox vbox;
	@FXML Glyph icon;
	@FXML JFXTextField name_field, lastName_field, userID_field, email_field;
	@FXML JFXPasswordField pw_field, pw_field_confirm;
	@FXML HBox btn_box;
	@FXML JFXButton back_btn, register_btn, code_btn;
	
	private final StringProperty name_txt;
	private final StringProperty lastName_txt;
	private final StringProperty confirmPw_text;
	private final StringProperty reg_btn_txt;
	private final StringProperty code_btn_txt;
	private final StringProperty required_valid_error;
	private final StringProperty mail_valid_error;
	private final StringProperty pw_length_valid_error;
	private final StringProperty alert_pw_ver_heading;
	private final StringProperty alert_pw_ver_body;
	private final StringProperty notif_connection_loss;
	private final Glyph back_arrow;
	private List<JFXTextField> validatableTextFields;
	private List<JFXPasswordField> validatablePwFields;

	/*---Constructors---*/
	/**
	 * Builds an object of type RegistrationController
	 */
	public RegistrationController() {
		super();
		this.name_txt = new SimpleStringProperty();
		this.lastName_txt = new SimpleStringProperty();
		this.reg_btn_txt = new SimpleStringProperty();
		this.confirmPw_text = new SimpleStringProperty();
		this.required_valid_error = new SimpleStringProperty();
		this.mail_valid_error = new SimpleStringProperty();
		this.code_btn_txt = new SimpleStringProperty();
		this.alert_pw_ver_body = new SimpleStringProperty();
		this.alert_pw_ver_heading = new SimpleStringProperty();
		this.pw_length_valid_error = new SimpleStringProperty();
		this.notif_connection_loss = new SimpleStringProperty();
		back_arrow = new Glyph("FontAwesome", FontAwesome.Glyph.ARROW_LEFT);
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		this.validatableTextFields = new ArrayList<>(List.of(name_field, lastName_field, userID_field, email_field));
		this.validatablePwFields = new ArrayList<>(List.of(pw_field, pw_field_confirm));
		icon.setFontFamily("FontAwesome");
		icon.setIcon(FontAwesome.Glyph.USER);
		back_arrow.setAlignment(Pos.CENTER);
		back_btn.setGraphic(back_arrow);
		bindText();
		bindButtons();
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
		setValidators();
		required_valid_error.addListener((observableValue, s, t1) -> {
			name_field.resetValidation();
			lastName_field.resetValidation();
			email_field.resetValidation();
			userID_field.resetValidation();
			pw_field.resetValidation();
			pw_field_confirm.resetValidation();
		});
		name_field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				name_field.validate();
			}
		});
		lastName_field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				lastName_field.validate();
			}
		});
		userID_field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				userID_field.validate();
			}
		});
		email_field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				email_field.validate();
			}
		});
		pw_field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				pw_field.validate();
			}
		});
		pw_field_confirm.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				pw_field_confirm.validate();
			}
		});
		if (!Launcher.manager.isConnected()) {
			register_btn.setDisable(true);
			code_btn.setDisable(true);
		}
		Launcher.manager.proxyProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				register_btn.setDisable(true);
				code_btn.setDisable(true);
			} else {
				register_btn.setDisable(false);
				code_btn.setDisable(false);
			}
		});
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
	void register() {
		if (!Launcher.manager.isConnected()) {
			serverAlert((StackPane) root, rect.getWidth()).show();
			return;
		}
		if (!allValid()) {
			return;
		}
		if (!verifyPassword()) {
			return;
		}
		LoadingAnimationOverlay animation = new LoadingAnimationOverlay(root, "Wait...");
		Task<Void> task = new Task<>() {
			@SuppressWarnings("CatchMayIgnoreException")
			@Override
			protected Void call() {
				List<String> errMsgs = null;
				try {
					errMsgs = Launcher.manager.requestActivationCode(name_field.getText(),
							lastName_field.getText(), userID_field.getText(), email_field.getText(), pw_field.getText());
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
				assert errMsgs != null;
				if (errMsgs.isEmpty()) {
					Platform.runLater(() -> {
						animation.stopAnimation();
						JFXDialogLayout dialogLayout = new JFXDialogLayout();
						JFXDialog dialog = new JFXDialog((StackPane) root, dialogLayout, JFXDialog.DialogTransition.CENTER);
						JFXButton okBtn = new JFXButton("OK");
						okBtn.setOnAction(e -> dialog.close());
						dialogLayout.setHeading(new Label("Success"));
						dialogLayout.setActions(okBtn);
						dialogLayout.setBody(new Label(Launcher.contrManager.getBundleValue().getString("request_success")));
						dialog.show();
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
					for (Text t : localized) {
						dialogLayout.getBody().add(t);
					}
					dialog.show();
				});
				return null;
			}

			@Override
			protected void scheduled() {
				super.scheduled();
				Platform.runLater(animation::playAnimation);
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	void confirmCode() throws IOException {
		JFXDialog dialog = activationCodeDialog();
		dialog.show();
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		name_txt.set(resBundle.getString("prompt_name"));
		lastName_txt.set(resBundle.getString("prompt_lastName"));
		confirmPw_text.set(resBundle.getString("prompt_confirmpw"));
		reg_btn_txt.set(resBundle.getString("register_reg_btn"));
		required_valid_error.set(resBundle.getString("required_err_label"));
		mail_valid_error.set(resBundle.getString("invalid_email_error"));
		code_btn_txt.set(resBundle.getString("insert_code"));
		alert_pw_ver_heading.set(resBundle.getString("alert_pw_ver_failed_heading"));
		alert_pw_ver_body.set(resBundle.getString("alert_pw_ver_failed_body"));
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
	
	/* ---- Private internal methods for rescaling and other logic ---- */
	private void bindText() {
		name_field.promptTextProperty().bind(name_txt);
		lastName_field.promptTextProperty().bind(lastName_txt);
		pw_field_confirm.promptTextProperty().bind(confirmPw_text);
		register_btn.textProperty().bind(reg_btn_txt);
		code_btn.textProperty().bind(code_btn_txt);
	}
	
	private void rescaleRect(double after) {
		double widthAfter = (after*ref.getReferences().get("REG_RECT_W")) / ref.getReferences().get("REF_RESOLUTION");
		double heigthAfter = (after*ref.getReferences().get("REG_RECT_H")) / ref.getReferences().get("REF_RESOLUTION");
		rect.setWidth(widthAfter); 
		rect.setHeight(heigthAfter);
		vbox.setPrefSize(widthAfter, heigthAfter);
		double newPadding = after*ref.getReferences().get("REG_PADDING_TOP") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setPadding(new Insets(newPadding, 0, 0, 0));
		double newSpacing = after*ref.getReferences().get("REG_SPACING") / ref.getReferences().get("REF_RESOLUTION");
		vbox.setSpacing(newSpacing);
	}
	
	private void rescaleFields(double after) {
		double widthAfter = after*ref.getReferences().get("REG_TXT_FIELD_W") / ref.getReferences().get("REF_RESOLUTION");
		double heightAfter = after*(ref.getReferences().get("REG_TXT_FIELD_H")) / ref.getReferences().get("REF_RESOLUTION");
		double fontsize_adj = currentFontSize.get()-2;
		userID_field.setPrefSize(widthAfter, heightAfter);
		userID_field.setStyle("-fx-font-size: "+ fontsize_adj +";");
		name_field.setPrefSize(widthAfter, heightAfter);
		name_field.setStyle("-fx-font-size: "+ fontsize_adj +";");
		lastName_field.setPrefSize(widthAfter, heightAfter);
		lastName_field.setStyle("-fx-font-size: "+ fontsize_adj +";");
		email_field.setPrefSize(widthAfter, heightAfter);
		email_field.setStyle("-fx-font-size: "+ fontsize_adj +";");
		pw_field.setPrefSize(widthAfter, heightAfter);
		pw_field.setStyle("-fx-font-size: "+ fontsize_adj +";");
		pw_field_confirm.setPrefSize(widthAfter, heightAfter);
		pw_field_confirm.setStyle("-fx-font-size: "+ fontsize_adj +";");
		btn_box.setPrefSize(widthAfter, after*ref.getReferences().get("LOG_BTN_H") / ref.getReferences().get("REF_RESOLUTION"));
		btn_box.setSpacing(after*ref.getReferences().get("REG_BTN_SPACING") / ref.getReferences().get("REF_RESOLUTION"));
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
		code_btn.prefHeightProperty().bind(btn_box.prefHeightProperty());
		DoubleBinding btnWidth = (btn_box.prefWidthProperty().subtract(btn_box.spacingProperty())).divide(3.0);
		register_btn.prefWidthProperty().bind(btnWidth);
		back_btn.prefWidthProperty().bind(btnWidth);
		code_btn.prefWidthProperty().bind(btnWidth);
	}

	private void setValidators() {
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.messageProperty().bind(required_valid_error);
		RegexValidator emailValidator = new RegexValidator();
		emailValidator.setRegexPattern("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
		emailValidator.messageProperty().bind(mail_valid_error);
		RegexValidator pwLengthValidator = new RegexValidator();
		pwLengthValidator.setRegexPattern(".{5,}");
		pwLengthValidator.messageProperty().bind(pw_length_valid_error);
		name_field.setValidators(validator);
		lastName_field.setValidators(validator);
		userID_field.setValidators(validator);
		email_field.setValidators(validator, emailValidator);
		pw_field.setValidators(validator, pwLengthValidator);
		pw_field_confirm.setValidators(validator);
	}

	private boolean allValid() {
		boolean valid = true;
		for (JFXTextField field : validatableTextFields) {
			field.validate();
			valid = valid & field.getValidators().stream()
					.noneMatch(ValidatorBase::getHasErrors);
			if (!valid) {
				return false;
			}
		}
		for (JFXPasswordField pwfield : validatablePwFields) {
			pwfield.validate();
			valid = valid & pwfield.getValidators().stream()
					.noneMatch(ValidatorBase::getHasErrors);
			if (!valid) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyPassword() {
		if (!pw_field.getText().equals(pw_field_confirm.getText())) {
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			dialogLayout.setHeading(new Label(alert_pw_ver_heading.get()));
			dialogLayout.setBody(new Label(alert_pw_ver_body.get()));
			JFXButton ok_btn = new JFXButton("OK");
			JFXDialog dialog = new JFXDialog((StackPane) root, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
			dialogLayout.setActions(ok_btn);
			ok_btn.setOnAction((event) -> dialog.close());
			dialog.show();
			return false;
		}
		return true;
	}

	private JFXDialog activationCodeDialog() throws IOException {
		JFXDialog dialog = new JFXDialog();
		ActivationCodeAlertController controller = new ActivationCodeAlertController();
		LoadingAnimationOverlay anim = new LoadingAnimationOverlay(root, "Checking...");
		controller.setDialog(dialog);
		controller.setAnimation(anim);
		Parent parent = requestParent(ControllerType.ACTIVATION_CODE, controller);
		dialog.setDialogContainer((StackPane) root);
		dialog.setContent((Region) parent);
		dialog.setOverlayClose(false);
		dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		dialog.setOnDialogClosed(e -> {
			if (Launcher.manager.getProfile() != null) {
				Parent mainMenu;
				try {
					mainMenu = requestParent(ControllerType.HOME_VIEW, new HomeController());
					Timeline transition = sceneTransitionAnimation(mainMenu, SlideDirection.TO_LEFT);
					transition.play();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
		return dialog;
	}
}
