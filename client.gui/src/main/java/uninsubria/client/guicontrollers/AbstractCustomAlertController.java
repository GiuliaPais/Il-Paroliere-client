package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import uninsubria.client.gui.Controller;
import uninsubria.client.gui.Launcher;

/**
 * Abstract controller for complex custom dialogs that require separate logic.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
abstract class AbstractCustomAlertController implements Controller {
    /*---Fields---*/
    @FXML StackPane stackPaneAlert;

    protected JFXDialog dialog;

    /*---Constructors---*/
    public AbstractCustomAlertController() {
    }

    /*---Methods---*/
    /**
     * Initializes the controller.
     */
    public void initialize() {
        setTextResources(Launcher.contrManager.getBundleValue());
    }

    /**
     * Sets a dialog reference for this controller.
     *
     * @param dialog the dialog
     */
    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }

    protected JFXDialog getSubDialog(String header, String bodyContent) {
        Label heading = new Label(header);
        Label body = new Label(bodyContent);
        JFXDialogLayout content = new JFXDialogLayout();
        content.getStylesheets().add(Launcher.contrManager.getAppTheme());
        content.setMaxWidth(stackPaneAlert.getWidth()*0.8);
        content.setHeading(heading);
        content.setBody(body);
        JFXButton ok_button = new JFXButton("OK");
        JFXDialog dialog = new JFXDialog(stackPaneAlert, content, JFXDialog.DialogTransition.CENTER);
        ok_button.setOnAction((actionEvent -> dialog.close()));
        content.setActions(ok_button);
        return dialog;
    }
}
