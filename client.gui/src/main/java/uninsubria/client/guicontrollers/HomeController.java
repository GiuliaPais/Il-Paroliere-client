package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.Launcher;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller for the home view.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class HomeController extends AbstractMainController {
    /*---Fields---*/
    @FXML AnchorPane headerPane;
    @FXML StackPane profImage;
    @FXML VBox profInfo;
    @FXML Label userLabel, emailLabel;
    @FXML JFXTabPane tabPane;
    @FXML Tab roomTab, statsTab;
    @FXML Glyph tutorialIcon, settingsIcon, hamburger;

    private final StringProperty roomTab_txt;
    private final StringProperty statsTab_txt;
    private final StringProperty menu_exit_txt;
    private final StringProperty menu_info_txt;
    private final StringProperty menu_logout_txt;
    private SVGGlyph img;
    private DoubleBinding imgSidelength;
    private ObjectProperty<Background> imgBackground;

    /*---Constructors---*/
    public HomeController() {
        this.roomTab_txt = new SimpleStringProperty();
        this.statsTab_txt = new SimpleStringProperty();
        this.menu_info_txt = new SimpleStringProperty();
        this.menu_logout_txt = new SimpleStringProperty();
        this.menu_exit_txt = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        this.imgSidelength = (profImage.prefWidthProperty().divide(2)).multiply(Math.sqrt(2));
        initIcons();
        loadImagePreset();
        loadProfileInfo();
        initPopupMenu();
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        roomTab_txt.set(resBundle.getString("room_tab"));
        statsTab_txt.set(resBundle.getString("stats_tab"));
        menu_exit_txt.set(resBundle.getString("main_menu_exit"));
        menu_logout_txt.set(resBundle.getString("main_menu_logout"));
        menu_info_txt.set(resBundle.getString("main_menu_about"));
    }

    @FXML void showSettings() throws IOException {
        OptionsController controller = new OptionsController();
        controller.setRequestOrigin(this);
        Parent p = requestParent(ControllerType.OPTIONS_MAIN, controller);
        Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_TOP);
        anim.play();
    }

    /*----------- Private methods for initialization and scaling -----------*/
    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleProfile(after);
        rescaleTabPane(after);
        rescaleIcons(after);
    }

    private void initIcons() {
        tutorialIcon.setFontFamily("FontAwesome");
        tutorialIcon.setIcon(FontAwesome.Glyph.QUESTION);
        tutorialIcon.setFontSize(ref.getReferences().get("HOME_ICONS_SIZE"));
        settingsIcon.setFontFamily("FontAwesome");
        settingsIcon.setIcon(FontAwesome.Glyph.GEAR);
        settingsIcon.setFontSize(ref.getReferences().get("HOME_ICONS_SIZE"));
        hamburger.setFontFamily("FontAwesome");
        hamburger.setIcon(FontAwesome.Glyph.BARS);
        hamburger.setFontSize(ref.getReferences().get("HOME_ICONS_SIZE"));
    }

    private void rescaleProfile(double after) {
        double afterSize = (after*ref.getReferences().get("HOME_PROFILE_IMG_DIM")) / ref.getReferences().get("REF_RESOLUTION");
        profImage.setPrefSize(afterSize, afterSize);
        double infoH = (after*ref.getReferences().get("HOME_PROFILE_INFO_H")) / ref.getReferences().get("REF_RESOLUTION");
        double infoW = (after*ref.getReferences().get("HOME_PROFILE_INFO_W")) / ref.getReferences().get("REF_RESOLUTION");
        profInfo.setPrefSize(infoW, infoH);
        double paddingAfter = (after*ref.getReferences().get("HOME_PROFILE_INFO_PAD")) / ref.getReferences().get("REF_RESOLUTION");
        profInfo.setPadding(new Insets(0, 0, 0, paddingAfter));
        double spacingAfter = (after*ref.getReferences().get("HOME_PROFILE_INFO_SPACING")) / ref.getReferences().get("REF_RESOLUTION");
        profInfo.setSpacing(spacingAfter);
        AnchorPane.setLeftAnchor(profInfo, 20.0 + afterSize/2);
        double emailFont = currentFontSize.get() - 5.0;
        double userFont = currentFontSize.get() + 2.0;
        emailLabel.setStyle("-fx-font-size: " + emailFont + "px;");
        userLabel.setStyle("-fx-font-size: " + userFont + "px;");
    }

    private void rescaleTabPane(double after) {
        double minTabHeight = (after*ref.getReferences().get("HOME_TAB_MIN_H")) / ref.getReferences().get("REF_RESOLUTION");
        tabPane.setTabMinHeight(minTabHeight);
        if (roomTab.getGraphic() == null) {
            /* Converts simple text of tab headers in stylable labels*/
            Label roomTabLabel = new Label();
            roomTabLabel.textProperty().bind(roomTab_txt);
            roomTab.setText("");
            roomTab.setGraphic(roomTabLabel);
        }
        roomTab.getGraphic().setStyle("-fx-font-size: " + currentFontSize.get() + "px;");
        if (statsTab.getGraphic() == null) {
            /* Converts simple text of tab headers in stylable labels*/
            Label statsTabLabel = new Label();
            statsTabLabel.textProperty().bind(statsTab_txt);
            statsTab.setText("");
            statsTab.setGraphic(statsTabLabel);
        }
        statsTab.getGraphic().setStyle("-fx-font-size: " + currentFontSize.get() + "px;");
    }

    private void rescaleIcons(double after) {
        double dimAfter = (after*ref.getReferences().get("HOME_ICONS_SIZE")) / ref.getReferences().get("REF_RESOLUTION");
        tutorialIcon.setPrefSize(dimAfter, dimAfter);
        settingsIcon.setPrefSize(dimAfter, dimAfter);
        hamburger.setPrefSize(dimAfter, dimAfter);
        tutorialIcon.setFontSize(dimAfter);
        settingsIcon.setFontSize(dimAfter);
        hamburger.setFontSize(dimAfter);
    }

    private void loadImagePreset() {
        /* Sets the background color */
        imgBackground = new SimpleObjectProperty<>(new Background(
                new BackgroundFill(Color.valueOf(Launcher.manager.getProfile().getBgColor()),
                        new CornerRadii(0.0), new Insets(0,0,0,0))));
        profImage.backgroundProperty().bind(imgBackground);
        /* Loads the actual image vector */
        ProfileImagePreset preset = ProfileImagePreset.getByCode(Launcher.manager.getProfile().getProfileImage());
        assert preset != null;
        this.img = new SVGGlyph(preset.getSvgPath());
        if (preset.getOrientation().equals(ProfileImagePreset.Orientation.VERTICAL)) {
            img.setSizeForHeight(imgSidelength.get());
            imgSidelength.addListener((observable, oldValue, newValue) -> img.setSizeForHeight(newValue.doubleValue()));
        } else {
            img.setSizeForWidth(imgSidelength.get());
            imgSidelength.addListener((observable, oldValue, newValue) -> img.setSizeForWidth(newValue.doubleValue()));
        }
        img.setFill(Color.valueOf(Launcher.manager.getProfile().getImgColor()));
        profImage.getChildren().clear();
        profImage.getChildren().add(img);
        profImage.toFront();
        Effect shadow = profImage.getEffect();
        profImage.setOnMouseEntered(e -> {
            Bloom bloom = new Bloom();
            bloom.setThreshold(0.1);
            bloom.setInput(shadow);
            profImage.setEffect(bloom);
        });
        profImage.setOnMouseExited(e -> profImage.setEffect(shadow));
        profImage.setOnMouseClicked(e -> {
            try {
                profileSettingsDialog().show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void updateImage() {
        /* Sets the background color */
        imgBackground.set(new Background(
                new BackgroundFill(Color.valueOf(Launcher.manager.getProfile().getBgColor()),
                        new CornerRadii(0.0), new Insets(0,0,0,0))));
        /* Loads the actual image vector */
        ProfileImagePreset preset = ProfileImagePreset.getByCode(Launcher.manager.getProfile().getProfileImage());
        assert preset != null;
        this.img = new SVGGlyph(preset.getSvgPath());
        if (preset.getOrientation().equals(ProfileImagePreset.Orientation.VERTICAL)) {
            img.setSizeForHeight(imgSidelength.get());
            imgSidelength.addListener((observable, oldValue, newValue) -> img.setSizeForHeight(newValue.doubleValue()));
        } else {
            img.setSizeForWidth(imgSidelength.get());
            imgSidelength.addListener((observable, oldValue, newValue) -> img.setSizeForWidth(newValue.doubleValue()));
        }
        img.setFill(Color.valueOf(Launcher.manager.getProfile().getImgColor()));
        profImage.getChildren().clear();
        profImage.getChildren().add(img);
        profImage.toFront();
    }

    private void initPopupMenu() {
        ObservableList<StringProperty> localizedOptions = FXCollections.observableArrayList(menu_info_txt, menu_logout_txt, menu_exit_txt);
        /* Content of the menu */
        JFXListView<StringProperty> menuContent = new JFXListView<>();
        menuContent.setCellFactory(new Callback<>() {
            @Override
            public ListCell<StringProperty> call(ListView<StringProperty> param) {
                return new JFXListCell<>() {
                    @Override
                    protected void updateItem(StringProperty item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.get());
                        }
                    }
                };
            }
        });
        menuContent.setItems(localizedOptions);
        menuContent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(menu_exit_txt)) {
                Platform.exit();
                System.exit(0);
                return;
            }
            if (newValue.equals(menu_info_txt)) {
                //do something
                return;
            }
            if (newValue.equals(menu_logout_txt)) {
                //do something
                return;
            }
        });

        JFXPopup menu = new JFXPopup(menuContent);
        hamburger.setOnMouseClicked(e -> menu.show(hamburger, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT));
    }

    private void loadProfileInfo() {
        userLabel.setText(Launcher.manager.getProfile().getPlayerID());
        emailLabel.setText(Launcher.manager.getProfile().getEmail());
    }

    private JFXDialog profileSettingsDialog() throws IOException {
        JFXDialog dialog = new JFXDialog();
        ProfileSettingsAlertController controller = new ProfileSettingsAlertController();
        controller.setFontSize(currentFontSize.get() - 5);
        controller.setDialog(dialog);
        Parent parent = requestParent(ControllerType.PROFILE_SETTINGS, controller);
        Region content = (Region) parent;
        content.setPrefSize(root.getPrefWidth()/2, root.getPrefHeight()/2);
        dialog.setContent(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.setDialogContainer((StackPane) root);
        dialog.setOnDialogClosed(e -> {
            updateImage();
            loadProfileInfo();
        });
        return dialog;
    }
}
