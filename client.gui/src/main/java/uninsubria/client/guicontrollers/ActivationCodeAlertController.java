package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.text.TextFlow;

/**
 * Controller for the activation code dialog.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ActivationCodeAlertController {
    /*---Fields---*/
    @FXML JFXTextField email_textfield, code_textfield;
    @FXML TextFlow message_area;
    @FXML JFXButton back_btn, sendCode_btn, resend_btn;

    /*---Constructors---*/
    public ActivationCodeAlertController() {
    }

    /*---Methods---*/
    public void initialize() {
        //TODO
        RequiredFieldValidator validator = new RequiredFieldValidator("Required");
        email_textfield.setValidators(validator);
        email_textfield.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                email_textfield.validate();
            }
        });
    }
}
