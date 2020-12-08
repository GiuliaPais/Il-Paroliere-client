package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.Launcher;
import uninsubria.utils.business.Player;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the profile settings.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ProfileSettingsAlertController extends AbstractCustomAlertController {
    /*---Fields---*/
    @FXML JFXTabPane tabPane;
    @FXML Tab profileImgTab, profileInfoTab, changeUidTab;
    @FXML AnchorPane imgPreview;
    @FXML Label profileOptTitle;
    @FXML JFXColorPicker bgColorPicker, imgColorPicker;
    @FXML JFXComboBox<ProfileImagePreset> imgList;
    @FXML JFXTextField userId, name, lastname, email, currentId, newId;
    @FXML JFXPasswordField oldPwField, newPwField;
    @FXML Glyph nameGlyph, lastnameGlyph;

    private double fontSize;
    private final Player afterMod;
    private final StringProperty imgTabText;
    private final StringProperty infoTabText;
    private final StringProperty changeIdTabText;
    private final StringProperty required_valid_error;
    private final StringProperty pw_length_valid_error;
    private ObjectProperty<Color> bgColor, imgColor;
    private ObjectProperty<Background> background;
    private final StackPane imgStackPane;
    private DoubleBinding binding;

    /*---Constructors---*/
    /**
     * Instantiates a new Profile settings alert controller.
     */
    public ProfileSettingsAlertController() {
        super();
        this.afterMod = Launcher.manager.getProfile();
        this.imgTabText = new SimpleStringProperty();
        this.infoTabText = new SimpleStringProperty();
        this.changeIdTabText = new SimpleStringProperty();
        this.imgStackPane = new StackPane();
        this.required_valid_error = new SimpleStringProperty();
        this.pw_length_valid_error = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        binding = (imgPreview.prefWidthProperty().divide(2)).multiply(Math.sqrt(2));
        stackPaneAlert.setStyle("-fx-font-size: " + fontSize + ";");
        profileOptTitle.setStyle("-fx-font-size: " + (fontSize + 2) + ";");
        bgColor = new SimpleObjectProperty<>(Color.valueOf(Launcher.manager.getProfile().getBgColor()));
        imgColor = new SimpleObjectProperty<>(Color.valueOf(Launcher.manager.getProfile().getImgColor()));
        background = new SimpleObjectProperty<>(new Background(
                new BackgroundFill(bgColor.get(), new CornerRadii(0.0),
                        new Insets(0,0,0,0))));
        bgColorPicker.setValue(bgColor.getValue());
        imgColorPicker.setValue(imgColor.getValue());
        bgColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            bgColor.set(newValue);
            afterMod.setBgColor(bgColor.get().toString());
        });
        imgColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            imgColor.set(newValue);
            afterMod.setImgColor(imgColor.get().toString());
        });
        bgColor.addListener((observable, oldValue, newValue) -> {
            Background newBg = new Background(new BackgroundFill(newValue, new CornerRadii(0.0),
                    new Insets(0,0,0,0)));
            background.set(newBg);
        });
        convertTabsLabels();
        loadImgPreview();
        setImgList();
        initTextFields();
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        imgTabText.set(resBundle.getString("profile_img_tab"));
        infoTabText.set(resBundle.getString("profile_info_tab"));
        changeIdTabText.set(resBundle.getString("change_uid_tab"));
        required_valid_error.set(resBundle.getString("required_err_label"));
        pw_length_valid_error.set(resBundle.getString("invalid_pw_length_error"));
    }

    @FXML void undo() {
        dialog.close();
    }

    @FXML void save() {
        afterMod.setName(name.getText());
        afterMod.setSurname(lastname.getText());
        Launcher.manager.setProfile(afterMod);
        Launcher.manager.commitProfileChanges();
        dialog.close();
    }

    @FXML void requestIDChange() {
        if (!newId.validate()) {
            return;
        }
        if (newId.getText().equals(currentId.getText())) {
            return;
        }
        LoadingAnimationOverlay animation = new LoadingAnimationOverlay(stackPaneAlert, "");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<String> errMsgs = null;
                //noinspection CatchMayIgnoreException
                try {
                    errMsgs = Launcher.manager.changeUserID(currentId.getText(), newId.getText());
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
                List<String> localized = new ArrayList<>();
                assert errMsgs != null;
                if (errMsgs.isEmpty()) {
                    Platform.runLater(() -> {
                        animation.stopAnimation();
                        getSubDialog("SUCCESS",
                                Launcher.contrManager.getBundleValue().getString("change_uid_success_body")).show();
                        afterMod.setPlayerID(Launcher.manager.getProfile().getPlayerID());
                        currentId.setText(afterMod.getPlayerID());
                        newId.clear();
                    });
                    return null;
                }
                for (String err : errMsgs) {
                    String localMsg = Launcher.contrManager.getBundleValue().getString(err) + "\n";
                    localized.add(localMsg);
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (String t : localized) {
                    stringBuilder.append(t);
                }
                JFXDialog diag = getSubDialog("ERROR", stringBuilder.toString());
                Platform.runLater(() -> {
                    animation.stopAnimation();
                    diag.show();
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

    @FXML void requestPasswordChange() {
        if (!(oldPwField.validate() & newPwField.validate())) {
            return;
        }
        if (oldPwField.getText().equals(newPwField.getText())) {
            getSubDialog("ERROR", Launcher.contrManager.getBundleValue().getString("change_pw_dupl_error")).show();
            return;
        }
        LoadingAnimationOverlay animation = new LoadingAnimationOverlay(stackPaneAlert, "");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<String> errMsgs = null;
                //noinspection CatchMayIgnoreException
                try {
                    errMsgs = Launcher.manager.changePassword(afterMod.getEmail(),
                            oldPwField.getText(), newPwField.getText());
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
                List<String> localized = new ArrayList<>();
                assert errMsgs != null;
                if (errMsgs.isEmpty()) {
                    Platform.runLater(() -> {
                        animation.stopAnimation();
                        getSubDialog("SUCCESS",
                                Launcher.contrManager.getBundleValue().getString("change_pw_success_body")).show();
                        afterMod.setPassword(Launcher.manager.getProfile().getPassword());
                        oldPwField.clear();
                        newPwField.clear();
                    });
                    return null;
                }
                for (String err : errMsgs) {
                    String localMsg = Launcher.contrManager.getBundleValue().getString(err) + "\n";
                    localized.add(localMsg);
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (String t : localized) {
                    stringBuilder.append(t);
                }
                JFXDialog diag = getSubDialog("ERROR", stringBuilder.toString());
                Platform.runLater(() -> {
                    animation.stopAnimation();
                    diag.show();
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

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    private void convertTabsLabels() {
        Label imgLabel = new Label();
        imgLabel.setText(imgTabText.get());
        profileImgTab.setText("");
        profileImgTab.setGraphic(imgLabel);
        profileImgTab.getGraphic().setStyle("-fx-font-size: " + fontSize + ";");
        Label infoLabel = new Label();
        infoLabel.setText(infoTabText.get());
        profileInfoTab.setText("");
        profileInfoTab.setGraphic(infoLabel);
        profileInfoTab.getGraphic().setStyle("-fx-font-size: " + fontSize + ";");
        Label changeIdLabel = new Label();
        changeIdLabel.setText(changeIdTabText.get());
        changeUidTab.setText("");
        changeUidTab.setGraphic(changeIdLabel);
        changeUidTab.getGraphic().setStyle("-fx-font-size: " + fontSize + ";");
    }

    private void loadImgPreview() {
        imgStackPane.setId("profile-image");
        imgStackPane.backgroundProperty().bind(background);
        ProfileImagePreset preset = ProfileImagePreset.getByCode(afterMod.getProfileImage());
        assert preset != null;
        SVGGlyph glyph = new SVGGlyph(preset.getSvgPath());
        if (preset.getOrientation().equals(ProfileImagePreset.Orientation.VERTICAL)) {
            glyph.setSizeForHeight(binding.get());
        } else {
            glyph.setSizeForWidth(binding.get());
        }
        glyph.fillProperty().bind(imgColor);
        imgStackPane.getChildren().add(glyph);
        imgStackPane.setAlignment(Pos.CENTER);
        imgPreview.getChildren().clear();
        imgPreview.getChildren().add(imgStackPane);
        AnchorPane.setLeftAnchor(imgStackPane, 0.0);
        AnchorPane.setRightAnchor(imgStackPane, 0.0);
        AnchorPane.setTopAnchor(imgStackPane, 0.0);
        AnchorPane.setBottomAnchor(imgStackPane, 0.0);
    }

    private void setImgList() {
        imgList.setItems(FXCollections.observableArrayList(ProfileImagePreset.values()));
        imgList.setValue(ProfileImagePreset.getByCode(afterMod.getProfileImage()));
        imgList.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateGlyph(newValue);
            afterMod.setProfileImage(newValue.getCode());
        });
    }

    private void updateGlyph(ProfileImagePreset preset) {
        SVGGlyph glyph = new SVGGlyph(preset.getSvgPath());
        if (preset.getOrientation().equals(ProfileImagePreset.Orientation.VERTICAL)) {
            glyph.setSizeForHeight(binding.get());
        } else {
            glyph.setSizeForWidth(binding.get());
        }
        glyph.fillProperty().bind(imgColor);
        imgStackPane.getChildren().clear();
        imgStackPane.getChildren().add(glyph);
        imgStackPane.setAlignment(Pos.CENTER);
    }

    private void initTextFields() {
        nameGlyph.setFontFamily("FontAwesome");
        nameGlyph.setIcon(FontAwesome.Glyph.EDIT);
        nameGlyph.setFontSize(fontSize+10);
        nameGlyph.setOnMouseClicked(event -> {
            name.setEditable(true);
            name.requestFocus();
        });
        lastnameGlyph.setFontFamily("FontAwesome");
        lastnameGlyph.setIcon(FontAwesome.Glyph.EDIT);
        lastnameGlyph.setFontSize(fontSize+10);
        lastnameGlyph.setOnMouseClicked(event -> {
            lastname.setEditable(true);
            lastname.requestFocus();
        });
        userId.setText(afterMod.getPlayerID());
        name.setText(afterMod.getName());
        lastname.setText(afterMod.getSurname());
        email.setText(afterMod.getEmail());
        userId.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & userId.isEditable()) {
                userId.setEditable(false);
            }
        });
        name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & name.isEditable()) {
                name.setEditable(false);
            }
        });
        lastname.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & lastname.isEditable()) {
                lastname.setEditable(false);
            }
        });
        currentId.setText(afterMod.getPlayerID());
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.messageProperty().bind(required_valid_error);
        RegexValidator pwLengthValidator = new RegexValidator();
        pwLengthValidator.setRegexPattern(".{5,}");
        pwLengthValidator.messageProperty().bind(pw_length_valid_error);
        newId.setValidators(requiredFieldValidator);
        oldPwField.setValidators(requiredFieldValidator);
        newPwField.setValidators(requiredFieldValidator, pwLengthValidator);
    }
}
