package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.client.gui.Launcher;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the activation code dialog.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public class ActivationCodeAlertController extends AbstractCustomAlertController {
    /*---Fields---*/
    @FXML JFXTextField email_textfield, code_textfield;
    @FXML TextFlow message_area;
    @FXML JFXButton back_btn, sendCode_btn, resend_btn;

    private final StringProperty email_err_txt;
    private final StringProperty required_txt;
    private final StringProperty notif_connection_loss;
    private LoadingAnimationOverlay animation;

    /*---Constructors---*/
    /**
     * Instantiates a new Activation code alert controller.
     */
    public ActivationCodeAlertController() {
        super();
        this.email_err_txt = new SimpleStringProperty();
        this.required_txt = new SimpleStringProperty();
        this.notif_connection_loss = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.messageProperty().bind(required_txt);
        RegexValidator emailValidator = new RegexValidator();
        emailValidator.setRegexPattern("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
        emailValidator.messageProperty().bind(email_err_txt);
        email_textfield.setValidators(validator, emailValidator);
        code_textfield.setValidators(validator);
        if (dialog != null) {
            back_btn.setOnAction(e -> dialog.close());
        }
        if (!Launcher.manager.isConnected()) {
            sendCode_btn.setDisable(true);
            resend_btn.setDisable(true);
        }
        Launcher.manager.proxyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                sendCode_btn.setDisable(true);
                resend_btn.setDisable(true);
            } else {
                sendCode_btn.setDisable(false);
                resend_btn.setDisable(false);
            }
        });
    }

    @FXML
    void sendCode() {
        if (!(email_textfield.validate() & code_textfield.validate())) {
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<String> errMsgs = null;
                //noinspection CatchMayIgnoreException
                try {
                    errMsgs = Launcher.manager.confirmActivationCode(email_textfield.getText(), code_textfield.getText());
                } catch (Exception e) {
                    if (e instanceof SocketException) {
                        Platform.runLater(() -> {
                            animation.stopAnimation();
                            Text text = new Text(notif_connection_loss.get() + "\n");
                            text.setFill(Color.RED);
                            message_area.getChildren().add(text);
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
                                Text msg = new Text(Launcher.contrManager.getBundleValue().getString("alert_body") + "\n");
                                msg.setFill(Color.RED);
                                message_area.getChildren().add(msg);
                            });
                            return null;
                        }
                    }
                }
                List<Text> localized = new ArrayList<>();
                if (Objects.requireNonNull(errMsgs).isEmpty()) {
                    Text text = new Text(Launcher.contrManager.getBundleValue().getString("reg_success") + "\n");
                    Platform.runLater(() -> message_area.getChildren().add(text));
                    Platform.runLater(() -> animation.stopAnimation());
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
                    for (Text t : localized) {
                        message_area.getChildren().add(t);
                    }
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

    @FXML
    void resend() {
        clearValidation();
        if (!email_textfield.validate()) {
            return;
        }
        Task<Void> task = new Task<>() {
            @SuppressWarnings("CatchMayIgnoreException")
            @Override
            protected Void call() {
                List<String> errMsgs = null;
                try {
                    errMsgs = Launcher.manager.resendCode(email_textfield.getText(), "ACTIVATION");
                } catch (Exception e) {
                    if (e instanceof SocketException) {
                        Platform.runLater(() -> {
                            animation.stopAnimation();
                            Text text = new Text(notif_connection_loss.get() + "\n");
                            text.setFill(Color.RED);
                            message_area.getChildren().add(text);
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
                                Text msg = new Text(Launcher.contrManager.getBundleValue().getString("alert_body") + "\n");
                                msg.setFill(Color.RED);
                                message_area.getChildren().add(msg);
                            });
                            return null;
                        }
                    }
                }
                List<Text> localized = new ArrayList<>();
                if (Objects.requireNonNull(errMsgs).isEmpty()) {
                    Text text = new Text(Launcher.contrManager.getBundleValue().getString("resend_success") + "\n");
                    Platform.runLater(() -> message_area.getChildren().add(text));
                    Platform.runLater(() -> animation.stopAnimation());
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
                    for (Text t : localized) {
                        message_area.getChildren().add(t);
                    }
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

    /**
     * Sets the animation.
     *
     * @param anim the anim
     */
    public void setAnimation(LoadingAnimationOverlay anim) {
        this.animation = anim;
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        required_txt.set(resBundle.getString("required_err_label"));
        email_err_txt.set(resBundle.getString("invalid_email_error"));
        notif_connection_loss.set(resBundle.getString("connection_lost_notif"));
    }

    private void clearValidation() {
        email_textfield.resetValidation();
        code_textfield.resetValidation();
    }
}
