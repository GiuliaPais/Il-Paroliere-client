package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.ObservableLobby;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.net.SocketException;
import java.util.ResourceBundle;

/**
 * Custom alert for creation of the game lobby.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class CreateRoomAlertController extends AbstractCustomAlertController {
    /*---Fields---*/
    @FXML JFXTextField lobbyName;
    @FXML JFXComboBox<Integer> numPlayersCombo;
    @FXML JFXComboBox<Language> langCombo;
    @FXML JFXComboBox<Ruleset> ruleCombo;
    @FXML Label title;

    private StringProperty required_msg;
    private HomeController homeRef;
    private double fontSize;

    /*---Constructors---*/
    public CreateRoomAlertController() {
        this.required_msg = new SimpleStringProperty();
    }

    /*---Methods---*/
    public void setHomeRef(HomeController controller) {
        this.homeRef = controller;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void initialize() {
        super.initialize();
        stackPaneAlert.setStyle("-fx-font-size: " + fontSize + ";");
        title.setStyle("-fx-font-size: " + (fontSize + 2) + ";");
        initPlayersCombo();
        initLangCombo();
        initRuleCombo();
        setValidators();
    }

    @FXML void back() {
        dialog.close();
    }

    @FXML void createRoom() {
        if (!validateAll()) {
            return;
        }
        Lobby lobby = new Lobby(lobbyName.getText(), numPlayersCombo.getValue(), langCombo.getValue(), ruleCombo.getValue(), Lobby.LobbyStatus.OPEN);
        LoadingAnimationOverlay animation = new LoadingAnimationOverlay(stackPaneAlert, "Creating room...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    boolean created = Launcher.manager.createRoom(lobby);
                    if (created) {
                        Platform.runLater(() -> {
                            animation.stopAnimation();
                            homeRef.setActiveLobby(ObservableLobby.toObservableLobby(lobby));
                            back();
                        });
                        return null;
                    }
                } catch (Exception e) {
                    if (e instanceof SocketException) {
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
                                getSubDialog(Launcher.contrManager.getBundleValue().getString("alert_heading"),
                                        Launcher.contrManager.getBundleValue().getString("alert_body")).show();
                            });
                            return null;
                        }
                    }
                }
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

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        required_msg.set(resBundle.getString("required_err_label"));
    }

    private void initPlayersCombo() {
        ObservableList<Integer> players = FXCollections.observableArrayList();
        for (int i = 2; i <= 6; i++) {
            players.add(i);
        }
        numPlayersCombo.setItems(players);
        numPlayersCombo.setValue(players.get(0));
    }

    private void initLangCombo() {
        ObservableList<Language> languages = FXCollections.observableArrayList();
        for (Language l : Language.values()) {
            languages.add(l);
        }
        langCombo.setItems(languages);
        langCombo.setValue(languages.get(0));
    }

    private void initRuleCombo() {
        ObservableList<Ruleset> rulesets = FXCollections.observableArrayList();
        for (Ruleset r : Ruleset.values()) {
            rulesets.add(r);
        }
        ruleCombo.setItems(rulesets);
        ruleCombo.setValue(rulesets.get(0));
    }

    private void setValidators() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.messageProperty().bind(required_msg);
        lobbyName.setValidators(validator);
        numPlayersCombo.setValidators(validator);
        langCombo.setValidators(validator);
        ruleCombo.setValidators(validator);
    }

    private boolean validateAll() {
        return lobbyName.validate() & numPlayersCombo.validate() & langCombo.validate() & ruleCombo.validate();
    }
}
