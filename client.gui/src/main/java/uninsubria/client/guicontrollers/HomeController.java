package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.ObservableLobby;
import uninsubria.client.roomserver.RoomCentralManager;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Controller for the home view.
 *
 * @author Giulia Pais
 * @version 0.9.4
 */
public class HomeController extends AbstractMainController {
    /*---Fields---*/
    @FXML AnchorPane headerPane;
    @FXML StackPane profImage;
    @FXML VBox profInfo;
    @FXML Label userLabel, emailLabel, lobbyPlayer_lbl, lobbyLang_lbl, lobbyRule_lbl, lobbyPlayer_cont, lobbyLang_cont,
            lobbyRule_cont, playerList_lbl;
    @FXML JFXTabPane tabPane;
    @FXML Tab roomTab, playerStatsTab, gameStatsTab, wordStatsTab;
    @FXML Glyph tutorialIcon, settingsIcon, hamburger, leaveIcon;
    @FXML TableView roomList;
    @FXML TableColumn<ObservableLobby, String> name_col;
    @FXML TableColumn<ObservableLobby, Integer> players_col;
    @FXML TableColumn<ObservableLobby, Language> lang_col;
    @FXML TableColumn<ObservableLobby, Ruleset> ruleset_col;
    @FXML TableColumn<ObservableLobby, Lobby.LobbyStatus> status_col;
    @FXML JFXButton create_room_btn, join_room_btn;
    @FXML TitledPane lobbyView;

    private final StringProperty roomTab_txt, playerStatsTab_txt, gameStatsTab_txt, wordStatsTab_txt,
            menu_exit_txt, menu_info_txt, menu_logout_txt, name_col_txt, players_col_txt, lang_col_text, rule_col_text,
            status_col_txt, playerLobby_lbltext, langLobby_lbltext, ruleLobby_lbltext, playerList_lbltext;
    private SVGGlyph img;
    private DoubleBinding imgSidelength;
    private ObjectProperty<Background> imgBackground;

    private MapProperty<UUID, ObservableLobby> lobbyMap;
    private ListProperty<ObservableLobby> lobbies_list;
    private ScheduledService<Void> lobbiesRefresher;

    private ObjectProperty<ObservableLobby> activeLobby;

    /*---Constructors---*/
    /**
     * Instantiates a new Home controller.
     */
    public HomeController() {
        this.roomTab_txt = new SimpleStringProperty();
        this.playerStatsTab_txt = new SimpleStringProperty();
        this.menu_info_txt = new SimpleStringProperty();
        this.menu_logout_txt = new SimpleStringProperty();
        this.menu_exit_txt = new SimpleStringProperty();
        this.gameStatsTab_txt = new SimpleStringProperty();
        this.wordStatsTab_txt = new SimpleStringProperty();
        this.name_col_txt = new SimpleStringProperty();
        this.players_col_txt = new SimpleStringProperty();
        this.lang_col_text = new SimpleStringProperty();
        this.rule_col_text = new SimpleStringProperty();
        this.status_col_txt = new SimpleStringProperty();
        this.lobbyMap = new SimpleMapProperty<>();
        this.lobbies_list = new SimpleListProperty();
        this.activeLobby = new SimpleObjectProperty<>(null);
        this.playerLobby_lbltext = new SimpleStringProperty();
        this.langLobby_lbltext = new SimpleStringProperty();
        this.ruleLobby_lbltext = new SimpleStringProperty();
        this.playerList_lbltext = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        try {
            RoomCentralManager.startRoomServer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.imgSidelength = (profImage.prefWidthProperty().divide(2)).multiply(Math.sqrt(2));
        initIcons();
        loadImagePreset();
        loadProfileInfo();
        initPopupMenu();
        initRoomTable();
        initLobby();
        join_room_btn.setDisable(true);
        lobbyView.setVisible(false);
        lobbiesRefresher = getReferesherService();
        lobbiesRefresher.start();
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        roomTab_txt.set(resBundle.getString("room_tab"));
        playerStatsTab_txt.set(resBundle.getString("playerstats_tab"));
        menu_exit_txt.set(resBundle.getString("main_menu_exit"));
        menu_logout_txt.set(resBundle.getString("main_menu_logout"));
        menu_info_txt.set(resBundle.getString("main_menu_about"));
        gameStatsTab_txt.set(resBundle.getString("gamestats_tab"));
        wordStatsTab_txt.set(resBundle.getString("wordstats_tab"));
        name_col_txt.set(resBundle.getString("name_col"));
        players_col_txt.set(resBundle.getString("players_col"));
        lang_col_text.set(resBundle.getString("language_col"));
        rule_col_text.set(resBundle.getString("rule_col"));
        status_col_txt.set(resBundle.getString("status_col"));
        playerLobby_lbltext.set(resBundle.getString("max_players_lbl"));
        langLobby_lbltext.set(resBundle.getString("lang_lbl"));
        ruleLobby_lbltext.set(resBundle.getString("ruleset_lbl"));
        playerList_lbltext.set(resBundle.getString("player_list"));
    }

    @FXML void showSettings() throws IOException {
        OptionsController controller = new OptionsController();
        controller.setRequestOrigin(this);
        Parent p = requestParent(ControllerType.OPTIONS_MAIN, controller);
        Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_TOP);
        anim.play();
    }

    @FXML void createRoom() throws IOException {
        JFXDialog dialog = new JFXDialog();
        CreateRoomAlertController controller = new CreateRoomAlertController();
        controller.setFontSize(currentFontSize.get() - 5);
        controller.setDialog(dialog);
        controller.setHomeRef(this);
        Parent parent = requestParent(ControllerType.CREATE_ROOM_ALERT, controller);
        Region content = (Region) parent;
        content.setPrefSize(root.getPrefWidth()/2, root.getPrefHeight()/2);
        dialog.setContent(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.setDialogContainer((StackPane) root);
        dialog.show();
    }

    @FXML void leaveRoom() throws IOException {
        Launcher.manager.leaveRoom(activeLobby.get().getRoomId());
        if (activeLobby.get() != null) {
            activeLobby.set(null);
        }
    }

    public void setActiveLobby(ObservableLobby lobby) {
        this.activeLobby.set(lobby);
    }

    /*----------- Private methods for initialization and scaling -----------*/
    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleProfile(after);
        rescaleTabPane(after);
        rescaleIcons(after);
        rescaleButton(after);
    }

    private ScheduledService<Void> getReferesherService() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            Map<UUID, Lobby> map = Launcher.manager.requestRoomUpdate();
                            ObservableMap<UUID, ObservableLobby> obmap = FXCollections.observableMap(new HashMap<>());
                            for (Map.Entry<UUID, Lobby> entry : map.entrySet()) {
                                ObservableLobby oblobby = ObservableLobby.toObservableLobby(entry.getValue());
                                obmap.put(entry.getKey(), oblobby);
                            }
                            lobbyMap.setValue(obmap);
                            lobbies_list.setValue(FXCollections.observableArrayList(lobbyMap.values()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(2));
        service.setRestartOnFailure(true);
        return service;
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
        if (playerStatsTab.getGraphic() == null) {
            /* Converts simple text of tab headers in stylable labels*/
            Label playerStatsTabLabel = new Label();
            playerStatsTabLabel.textProperty().bind(playerStatsTab_txt);
            playerStatsTab.setText("");
            playerStatsTab.setGraphic(playerStatsTabLabel);
        }
        playerStatsTab.getGraphic().setStyle("-fx-font-size: " + currentFontSize.get() + "px;");
        if (gameStatsTab.getGraphic() == null) {
            /* Converts simple text of tab headers in stylable labels*/
            Label gameStatsTabLabel = new Label();
            gameStatsTabLabel.textProperty().bind(gameStatsTab_txt);
            gameStatsTab.setText("");
            gameStatsTab.setGraphic(gameStatsTabLabel);
        }
        gameStatsTab.getGraphic().setStyle("-fx-font-size: " + currentFontSize.get() + "px;");
        if (wordStatsTab.getGraphic() == null) {
            /* Converts simple text of tab headers in stylable labels*/
            Label wordStatsTabLabel = new Label();
            wordStatsTabLabel.textProperty().bind(wordStatsTab_txt);
            wordStatsTab.setText("");
            wordStatsTab.setGraphic(wordStatsTabLabel);
        }
        wordStatsTab.getGraphic().setStyle("-fx-font-size: " + currentFontSize.get() + "px;");
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

    private void rescaleButton(double after) {
        double h = after*ref.getReferences().get("HOME_ROOM_BTN_H") / ref.getReferences().get("REF_RESOLUTION");
        double w = after*ref.getReferences().get("HOME_ROOM_BTN_W") / ref.getReferences().get("REF_RESOLUTION");
        create_room_btn.setPrefSize(w, h);
        create_room_btn.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        join_room_btn.setPrefSize(w, h);
        join_room_btn.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
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
                try {
                    Launcher.manager.quit();
                    RoomCentralManager.stopRoomServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void initRoomTable() {
        /* Set column headers */
        roomList.setItems(lobbies_list);
        lobbies_list.addListener((observable, oldValue, newValue) -> {
            roomList.setItems(newValue);
        });
        name_col.setText("");
        name_col.setGraphic(generateTableHeaderTxtField(name_col_txt));
        name_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.3));
        name_col.setCellValueFactory(param -> param.getValue().roomNameProperty());
        players_col.setText("");
        players_col.setGraphic(generateTableHeaderCombo(players_col_txt));
        players_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        players_col.setCellValueFactory(param -> param.getValue().numPlayersProperty().asObject());
        lang_col.setText("");
        lang_col.setGraphic(generateTableHeaderCombo(lang_col_text));
        lang_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        lang_col.setCellValueFactory(param -> param.getValue().languageProperty());
        ruleset_col.setText("");
        ruleset_col.setGraphic(generateTableHeaderCombo(rule_col_text));
        ruleset_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        ruleset_col.setCellValueFactory(param -> param.getValue().rulesetProperty());
        status_col.setText("");
        status_col.setGraphic(generateTableHeaderCombo(status_col_txt));
        status_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        status_col.setCellValueFactory(param -> param.getValue().statusProperty());
    }

    private VBox generateTableHeaderTxtField(StringProperty title_txt) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(5);
        Label title = new Label();
        title.textProperty().bind(title_txt);
        title.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        JFXTextField textField = new JFXTextField();
        textField.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
        vBox.getChildren().addAll(title, textField);
        return vBox;
    }

    private VBox generateTableHeaderCombo(StringProperty title_txt) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(5);
        Label title = new Label();
        title.textProperty().bind(title_txt);
        title.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        JFXComboBox comboBox = new JFXComboBox();
        comboBox.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
        //init combo box
        vBox.getChildren().addAll(title, comboBox);
        return vBox;
    }

    private void initLobby() {
        leaveIcon.setFontFamily("FontAwesome");
        leaveIcon.setIcon(FontAwesome.Glyph.SIGN_OUT);
        leaveIcon.setFontSize(currentFontSize.get());
        currentFontSize.addListener((observable, oldValue, newValue) -> {
            leaveIcon.setFontSize(newValue.doubleValue());
        });
        activeLobby.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                /* Means the player is not in a room */
                lobbyView.setVisible(false);
                create_room_btn.setDisable(false);
                roomList.setDisable(false);
                //other stuff?
                return;
            }
            lobbyView.setText(newValue.getRoomName());
            lobbyPlayer_cont.setText(String.valueOf(newValue.getNumPlayers()));
            lobbyLang_cont.setText(newValue.getLanguage().name());
            lobbyRule_cont.setText(newValue.getRuleset().name());
            create_room_btn.setDisable(true);
            roomList.setDisable(true);
            lobbyView.setVisible(true);
        });
    }

}
