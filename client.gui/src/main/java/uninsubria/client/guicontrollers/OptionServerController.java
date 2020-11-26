package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.Launcher;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller class for the "Server" pane in the options menu.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class OptionServerController extends AbstractOptionPaneController{
    /*---Fields---*/
    @FXML
    JFXListView<String> listView;
    @FXML
    Label add_ip_label;
    @FXML
    JFXButton add_btn, serverBtn;
    @FXML
    JFXTextField textField;
    @FXML
    HBox hBox;
    @FXML
    StackPane stackPane;

    private StringProperty ip_text, addbtntext, serverbtntext, validation_error;

    /*---Constructors---*/
    public OptionServerController() {
        super();
        this.ip_text = new SimpleStringProperty();
        this.addbtntext = new SimpleStringProperty();
        this.serverbtntext = new SimpleStringProperty();
        this.validation_error = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        bindText();
        bindLblDim();
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                IpAddressListCell cell = null;
                try {
                    cell = new IpAddressListCell(getMainOptionsController().currentFontSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return cell;
            }
        });
        listView.itemsProperty().bindBidirectional(Launcher.contrManager.getSettings().getConnectionPrefs().server_addressesProperty());
        setUpTextValidator();
        validation_error.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                textField.resetValidation();
            }
        });
    }

    @FXML
    void addAddress() {
        boolean valid = textField.validate();
        String address = textField.getText();
        if (valid) {
            if (!listView.getItems().contains(address)) {
                listView.getItems().add(address);
                textField.clear();
            } else {
                textField.clear();
            }
        }
    }

    @FXML
    void connectToServer() {
        String addressConnected = Launcher.manager.tryConnectServer();
        if (addressConnected == null) {
            getMainOptionsController().displayServerAlert();
        } else {
            getMainOptionsController().displayServerConnectedAlert(addressConnected);
        }
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        ip_text.set(resBundle.getString("add_ip_lbl"));
        addbtntext.set(resBundle.getString("add_btn"));
        serverbtntext.set(resBundle.getString("connect_btn"));
        validation_error.set(resBundle.getString("ip_valid_error"));
    }

    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleListView(after);
        rescaleTextField(after);
    }

    /* ---- Private internal methods for rescaling and init ---- */
    private void rescaleListView(double after) {
        double widthAfter = after*ref.getReferences().get("OPT_LIST_W") / ref.getReferences().get("REF_RESOLUTION");
        listView.setPrefWidth(widthAfter);
    }

    private void rescaleTextField(double after) {
        double widthAfter = after*ref.getReferences().get("OPT_TXTFIELD_W") / ref.getReferences().get("REF_RESOLUTION");
        double heightAfter = after*ref.getReferences().get("OPT_TXTFIELD_H") / ref.getReferences().get("REF_RESOLUTION");
        textField.setPrefSize(widthAfter, heightAfter);
    }

    private void bindLblDim() {
        add_ip_label.prefWidthProperty().bind(hBox.widthProperty());
    }

    private void bindText() {
        add_ip_label.textProperty().bind(ip_text);
        add_btn.textProperty().bind(addbtntext);
        serverBtn.textProperty().bind(serverbtntext);
    }

    private void setUpTextValidator() {
        String ip_v4 = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        RegexValidator validator = new RegexValidator();
        validator.setRegexPattern(ip_v4);
        validator.messageProperty().bind(validation_error);
        Glyph icon = new Glyph("FontAwesome", FontAwesome.Glyph.EXCLAMATION);
        icon.setFontSize(getMainOptionsController().currentFontSize.get());
        validator.setIcon(icon);
        textField.setValidators(validator);
    }
}
