package uninsubria.client.guicontrollers;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.client.gui.*;
import uninsubria.client.roomserver.RoomCentralManager;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.PlayerStatResult;
import uninsubria.utils.business.TurnsResult;
import uninsubria.utils.business.WordGameStatResult;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultAggregate;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;
import java.net.SocketException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller for the home view.
 *
 * @author Giulia Pais
 * @version 0.9.8
 */
public class HomeController extends AbstractMainController {
    /*---Fields---*/
    @FXML AnchorPane headerPane;
    @FXML StackPane profImage;
    @FXML VBox profInfo;
    @FXML Label userLabel, emailLabel, lobbyPlayer_lbl, lobbyLang_lbl, lobbyRule_lbl, lobbyPlayer_cont, lobbyLang_cont,
            lobbyRule_cont, playerList_lbl, titleCard1p, titleCard2p, titleCard3p, titleCard4p, titleCard5p,
            titleCard6p, titleCard7p, titleCard1g, titleCard2g, titleCard3g, gListTitle, titleCard1w, titleCard2w,
            titleCard3w;
    @FXML JFXTabPane tabPane;
    @FXML Tab roomTab, playerStatsTab, gameStatsTab, wordStatsTab;
    @FXML Glyph tutorialIcon, settingsIcon, hamburger, leaveIcon, refreshIcon, infoCard1, infoCard2, infoCard3,
            infoCard4, infoCard5, infoCard6, infoCard7, infoCard8, infoCard9, infoCard10, infoCard11, infoCard12, infoCard13;
    @FXML JFXButton searchBtn;
    @FXML TableView<ObservableLobby> roomList;
    @FXML TableColumn<ObservableLobby, String> name_col;
    @FXML TableColumn<ObservableLobby, Integer> players_col;
    @FXML TableColumn<ObservableLobby, Language> lang_col;
    @FXML TableColumn<ObservableLobby, Ruleset> ruleset_col;
    @FXML TableColumn<ObservableLobby, Lobby.LobbyStatus> status_col;
    @FXML JFXButton create_room_btn, join_room_btn;
    @FXML TitledPane lobbyView;
    @FXML JFXListView<Label> playersList, gList;
    @FXML JFXMasonryPane playerStatsMasonry, gameStatsMasonry, wordStatsMasonry;
    @FXML TableView<PlayerStatTuple> bestPTable, bestPMatchTable, avgBestPTable, avgBestPMatchTable, maxGamesTable,
            maxWrongWTable, maxDuplWTable;
    @FXML TableColumn<PlayerStatTuple, String> idCol1, idCol2, idCol3, idCol4, idCol5, idCol6, idCol7;
    @FXML TableColumn<PlayerStatTuple, Integer> scoreCol1, scoreCol2, gamesCol1, wordCol1, wordCol2;
    @FXML TableColumn<PlayerStatTuple, Double> scoreCol3, scoreCol4;
    @FXML TableView<TurnResultTuple> turnPlayersTable;
    @FXML TableColumn<TurnResultTuple, Integer> turnIdCol, minTurns, maxTurns;
    @FXML TableColumn<TurnResultTuple, Double> avgTurns;
    @FXML BarChart<String, Number> turnsGraph, gridGraph;
    @FXML JFXTextField wordSearch;
    @FXML TableView<WordTuple> wTable1, wTable2;
    @FXML TableColumn<WordTuple, String> wCol1, wCol2;
    @FXML TableColumn<WordTuple, Integer> occCol1, occCol2;
    @FXML TableView<WGPTuple> wTable3;
    @FXML TableColumn<WGPTuple, String> wCol3;
    @FXML TableColumn<WGPTuple, Integer> scoreColw1;
    @FXML TableColumn<WGPTuple, UUID> gameCol1;

    private final StringProperty roomTab_txt, playerStatsTab_txt, gameStatsTab_txt, wordStatsTab_txt,
            menu_exit_txt, menu_info_txt, menu_logout_txt, name_col_txt, players_col_txt, lang_col_text, rule_col_text,
            status_col_txt, playerLobby_lbltext, langLobby_lbltext, ruleLobby_lbltext, playerList_lbltext,
            notif_connection_loss, titlePCard1, titlePCard2, titlePCard3, titlePCard4, titlePCard5,
            titlePCard6, titlePCard7, titleGCard1, titleGCard2, titleGCard3, scoreColLbl, wordColLbl, gamesColLbl,
            descCard1, descCard2, descCard3, descCard4, descCard5, descCard6, descCard7, descCard8, descCard9, descCard10,
            descCard11, descCard12, descCard13,
            playersCol, maxTurnsCol, minTurnsCol, avgTurnsCol, letters_text, avgOccurr_text, gameCol, occurCol, wordCol,
            titleWCard1, titleWCard2, titleWCard3;
    private SVGGlyph img;
    private DoubleBinding imgSidelength;
    private ObjectProperty<Background> imgBackground;
    private MapProperty<UUID, ObservableLobby> lobbyMap;
    private ObservableList<ObservableLobby> lobbiesList;
    private ScheduledService<Void> lobbiesRefresher, roomPlayersUpdater;
    private ObjectProperty<ObservableLobby> activeLobby;
    private ListProperty<Label> observablePlayerList;
    private ObservableList<Label> observableGlist;
    private ObservableList<PlayerStatTuple> bestPlayerGame, bestPlayerMatch, bestAvgPlayerGame, bestAvgPlayerMatch,
            maxGamesPlayed, maxWrongWords, maxDuplWords;
    private ObservableList<TurnResultTuple> turnStats;
    private ObservableList<WordTuple> validWordRankingList, reqWordRankingList;
    private ObservableList<WGPTuple> wordGamePointsList;
    private XYChart.Series<String, Number> turnSeries1, turnSeries2, turnSeries3;

    //+++ Services, tasks, loading +++//
    //only one task at time
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private LoadingAnimationOverlay generalLoadingOverlay;
    private Service<ObservableLobby> activeLobbyService;
    private Task<Void> gameLoadingService;

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
        this.lobbyMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
        this.lobbiesList = FXCollections.observableArrayList();
        this.activeLobby = new SimpleObjectProperty<>(null);
        this.playerLobby_lbltext = new SimpleStringProperty();
        this.langLobby_lbltext = new SimpleStringProperty();
        this.ruleLobby_lbltext = new SimpleStringProperty();
        this.playerList_lbltext = new SimpleStringProperty();
        this.observablePlayerList = new SimpleListProperty<>();
        this.notif_connection_loss = new SimpleStringProperty();
        this.bestPlayerGame = FXCollections.observableArrayList();
        this.bestPlayerMatch = FXCollections.observableArrayList();
        this.bestAvgPlayerGame = FXCollections.observableArrayList();
        this.bestAvgPlayerMatch = FXCollections.observableArrayList();
        this.maxGamesPlayed = FXCollections.observableArrayList();
        this.maxWrongWords = FXCollections.observableArrayList();
        this.maxDuplWords = FXCollections.observableArrayList();
        this.turnStats = FXCollections.observableArrayList();
        this.titlePCard1 = new SimpleStringProperty();
        this.titlePCard2 = new SimpleStringProperty();
        this.titlePCard3 = new SimpleStringProperty();
        this.titlePCard4 = new SimpleStringProperty();
        this.titlePCard5 = new SimpleStringProperty();
        this.titlePCard6 = new SimpleStringProperty();
        this.titlePCard7 = new SimpleStringProperty();
        this.titleGCard1 = new SimpleStringProperty();
        this.titleGCard2 = new SimpleStringProperty();
        this.titleGCard3 = new SimpleStringProperty();
        this.scoreColLbl = new SimpleStringProperty();
        this.gamesColLbl = new SimpleStringProperty();
        this.wordColLbl = new SimpleStringProperty();
        this.descCard1 = new SimpleStringProperty();
        this.descCard2 = new SimpleStringProperty();
        this.descCard3 = new SimpleStringProperty();
        this.descCard4 = new SimpleStringProperty();
        this.descCard5 = new SimpleStringProperty();
        this.descCard6 = new SimpleStringProperty();
        this.descCard7 = new SimpleStringProperty();
        this.descCard8 = new SimpleStringProperty();
        this.descCard9 = new SimpleStringProperty();
        this.descCard10 = new SimpleStringProperty();
        this.descCard11 = new SimpleStringProperty();
        this.descCard12 = new SimpleStringProperty();
        this.descCard13 = new SimpleStringProperty();
        this.playersCol = new SimpleStringProperty();
        this.maxTurnsCol = new SimpleStringProperty();
        this.minTurnsCol = new SimpleStringProperty();
        this.avgTurnsCol = new SimpleStringProperty();
        this.turnSeries1 = new XYChart.Series<>();
        this.turnSeries2 = new XYChart.Series<>();
        this.turnSeries3 = new XYChart.Series<>();
        this.letters_text = new SimpleStringProperty();
        this.avgOccurr_text = new SimpleStringProperty();
        this.validWordRankingList = FXCollections.observableArrayList();
        this.wordCol = new SimpleStringProperty();
        this.gameCol = new SimpleStringProperty();
        this.occurCol = new SimpleStringProperty();
        this.titleWCard1 = new SimpleStringProperty();
        this.titleWCard2 = new SimpleStringProperty();
        this.reqWordRankingList = FXCollections.observableArrayList();
        this.titleWCard3 = new SimpleStringProperty();
        this.wordGamePointsList = FXCollections.observableArrayList();
        this.observableGlist = FXCollections.observableArrayList();
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
        imgSidelength = (profImage.prefWidthProperty().divide(2)).multiply(Math.sqrt(2));
        initServicesAndTasks();
        initIcons();
        loadImagePreset();
        loadProfileInfo();
        initPopupMenu();
        initRoomTable();
        initLobby();
        initPlayerStatsTables();
        initGameStats();
        initWordStats();
        join_room_btn.setDisable(true);
        lobbyView.setVisible(false);
        loadStatistics();
        activeLobbyService.setExecutor(executorService);
        activeLobbyService.valueProperty().addListener((observable, oldValue, newValue) -> activeLobby.set(newValue));
        lobbiesRefresher = getRoomReferesherService();
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
        notif_connection_loss.set(resBundle.getString("connection_lost_notif"));
        titlePCard1.set(resBundle.getString("stats_p_topGame"));
        titlePCard2.set(resBundle.getString("stats_p_topMatch"));
        titlePCard3.set(resBundle.getString("stats_p_avgGame"));
        titlePCard4.set(resBundle.getString("stats_p_avgMatch"));
        titlePCard5.set(resBundle.getString("stats_p_maxGames"));
        titlePCard6.set(resBundle.getString("stats_p_maxWrong"));
        titlePCard7.set(resBundle.getString("stats_p_maxDupl"));
        titleGCard1.set(resBundle.getString("stats_g_turns"));
        titleGCard2.set(resBundle.getString("stats_g_grid"));
        titleGCard3.set(resBundle.getString("stats_g_requestedw"));
        scoreColLbl.set(resBundle.getString("score_col"));
        gamesColLbl.set(resBundle.getString("games_col"));
        wordColLbl.set(resBundle.getString("n_words_col"));
        descCard1.set(resBundle.getString("desc_top_games"));
        descCard2.set(resBundle.getString("desc_top_p_match"));
        descCard3.set(resBundle.getString("desc_avg_p_game"));
        descCard4.set(resBundle.getString("desc_avg_p_match"));
        descCard5.set(resBundle.getString("desc_top_games"));
        descCard6.set(resBundle.getString("desc_top_wrong"));
        descCard7.set(resBundle.getString("desc_top_dupl"));
        descCard8.set(resBundle.getString("desc_turns"));
        descCard9.set(resBundle.getString("desc_grid"));
        descCard10.set(resBundle.getString("desc_requested"));
        playersCol.set(resBundle.getString("players_col"));
        maxTurnsCol.set(resBundle.getString("max_matches_col"));
        minTurnsCol.set(resBundle.getString("min_matches_col"));
        avgTurnsCol.set(resBundle.getString("avg_matches_col"));
        letters_text.set(resBundle.getString("letters_lbl"));
        avgOccurr_text.set(resBundle.getString("avg_occurr_lbl"));
        gameCol.set(resBundle.getString("game_col"));
        wordCol.set(resBundle.getString("word_col"));
        occurCol.set(resBundle.getString("occurr_col"));
        titleWCard1.set(resBundle.getString("stats_w_rank_valid"));
        descCard11.set(resBundle.getString("desc_rank_valid"));
        titleWCard2.set(resBundle.getString("stats_w_rank_requested"));
        descCard12.set(resBundle.getString("desc_rank_req"));
        titleWCard3.set(resBundle.getString("stats_w_points"));
        descCard13.set(resBundle.getString("desc_word_points"));
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

    @FXML void joinRoom() {
        activeLobbyService.start();
    }

    @FXML void loadStatistics() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ServiceResultInterface serviceResultInterface = Launcher.manager.fetchStatistics();
                if (serviceResultInterface instanceof ServiceResult) {
                    Result<?> errors = serviceResultInterface.getResult("Errors");
                    if (errors != null) {
                        notification(Launcher.contrManager.getBundleValue().getString("stats_error_notification"), Duration.seconds(3));
                        return null;
                    }
                }
                /* Player stats */
                ServiceResultAggregate allStats = (ServiceResultAggregate) serviceResultInterface;
                ServiceResultAggregate playerStats = (ServiceResultAggregate) allStats.getComponentByName("PLAYERS STATS");
                ServiceResult bestPlayerPerGame = (ServiceResult) playerStats.getComponentByName("Highest score per game");
                bestPlayerGame.clear();
                for (Result<?> res : bestPlayerPerGame.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    bestPlayerGame.add(t);
                }
                ServiceResult bestPlayerPerMatch = (ServiceResult) playerStats.getComponentByName("Highest score per match");
                bestPlayerMatch.clear();
                for (Result<?> res : bestPlayerPerMatch.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    bestPlayerMatch.add(t);
                }
                ServiceResult bestAvgPlayerPerGame = (ServiceResult) playerStats.getComponentByName("Highest average score per game");
                bestAvgPlayerGame.clear();
                for (Result<?> res : bestAvgPlayerPerGame.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    bestAvgPlayerGame.add(t);
                }
                ServiceResult bestAvgPlayerPerMatch = (ServiceResult) playerStats.getComponentByName("Highest average score per match");
                bestAvgPlayerMatch.clear();
                for (Result<?> res : bestAvgPlayerPerMatch.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    bestAvgPlayerMatch.add(t);
                }
                ServiceResult maxGamesPlayedRes = (ServiceResult) playerStats.getComponentByName("Games played record");
                maxGamesPlayed.clear();
                for (Result<?> res : maxGamesPlayedRes.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    maxGamesPlayed.add(t);
                }
                ServiceResult maxDupl = (ServiceResult) playerStats.getComponentByName("Record duplicated words");
                maxDuplWords.clear();
                for (Result<?> res : maxDupl.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    maxDuplWords.add(t);
                }
                ServiceResult maxWrong = (ServiceResult) playerStats.getComponentByName("Record wrong words");
                maxWrongWords.clear();
                for (Result<?> res : maxWrong.getResultList()) {
                    PlayerStatTuple t = PlayerStatTuple.asPlayerStatsTuple((PlayerStatResult) res.getValue());
                    maxWrongWords.add(t);
                }
                /* Game stats */
                ServiceResultAggregate gameStats = (ServiceResultAggregate) allStats.getComponentByName("GAME STATS");
                ServiceResult turnsStats = (ServiceResult) gameStats.getComponentByName("TURNS STATS");
                turnStats.clear();
                if (turnSeries1.getData() != null) {
                    turnSeries1.getData().clear();
                }
                if (turnSeries2.getData() != null) {
                    turnSeries2.getData().clear();
                }
                if (turnSeries3.getData() != null) {
                    turnSeries3.getData().clear();
                }
                for (Result<?> res : turnsStats.getResultList()) {
                    TurnResultTuple t = TurnResultTuple.asTurnResultTuple((TurnsResult) res.getValue());
                    turnStats.add(t);
                    XYChart.Data<String, Number> minTurnsBar = new XYChart.Data<>(Integer.toString(t.getCategory()), t.minTurnsProperty().doubleValue());
                    XYChart.Data<String, Number> maxTurnsBar = new XYChart.Data<>(Integer.toString(t.getCategory()), t.maxTurnsProperty().doubleValue());
                    XYChart.Data<String, Number> avgTurnsBar = new XYChart.Data<>(Integer.toString(t.getCategory()), t.avgTurnsProperty().doubleValue());
                    Platform.runLater(() -> {
                        turnSeries1.getData().add(minTurnsBar);
                        turnSeries2.getData().add(maxTurnsBar);
                        turnSeries3.getData().add(avgTurnsBar);
                    });
                }
                Platform.runLater(() -> {
                    turnsGraph.setData(FXCollections.observableArrayList(turnSeries1, turnSeries2, turnSeries3));
                });
                ServiceResult gridStats = (ServiceResult) gameStats.getComponentByName("Average letter occurrences");
                ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();
                for (Result<?> res : gridStats.getResultList()) {
                    Hashtable<String, Double> avgMap = (Hashtable<String, Double>) res.getValue();
                    XYChart.Series<String, Number> lang = new XYChart.Series<>();
                    lang.setName(res.getName());
                    for (Map.Entry<String, Double> entry : avgMap.entrySet()) {
                        XYChart.Data<String, Number> let = new XYChart.Data<>(entry.getKey(), entry.getValue());
                        lang.getData().add(let);
                    }
                    series.add(lang);
                }
                Platform.runLater(() -> gridGraph.setData(series));
                /* Word stats */
                ServiceResultAggregate wordStats = (ServiceResultAggregate) allStats.getComponentByName("WORD STATS");
                ServiceResult validWordsStats = (ServiceResult) wordStats.getComponentByName("Ranking of valid words occurrences");
                validWordRankingList.clear();
                if (validWordsStats.getResultList() != null) {
                    Hashtable<String, Integer> rank = (Hashtable<String, Integer>) validWordsStats.getResult("Occurrences").getValue();
                    for (Map.Entry<String, Integer> entry : rank.entrySet()) {
                        validWordRankingList.add(new WordTuple(entry.getKey(), entry.getValue()));
                    }
                    Platform.runLater(() -> wTable1.sort());
                }
                ServiceResult reqWordsStats = (ServiceResult) wordStats.getComponentByName("Ranking of requested words occurrences");
                reqWordRankingList.clear();
                if (reqWordsStats.getResultList() != null) {
                    Hashtable<String, Integer> rank2 = (Hashtable<String, Integer>) reqWordsStats.getResult("Occurrences").getValue();
                    for (Map.Entry<String, Integer> entry : rank2.entrySet()) {
                        reqWordRankingList.add(new WordTuple(entry.getKey(), entry.getValue()));
                    }
                    Platform.runLater(() -> wTable2.sort());
                }
                ServiceResult wordGameStats = (ServiceResult) wordStats.getComponentByName("Game words with highest points");
                wordGamePointsList.clear();
                if (wordGameStats.getResultList() != null) {
                    for (Result<?> r : wordGameStats.getResultList()) {
                        WordGameStatResult wgsr = (WordGameStatResult) r.getValue();
                        wordGamePointsList.add(WGPTuple.asWGPTuple(wgsr));
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML void requestWordStat() {
        if (wordSearch.getText().equals("")) {
            return;
        }
        Task<ServiceResultInterface> task = new Task<>() {
            @Override
            protected ServiceResultInterface call() {
                try {
                    return Launcher.manager.requestWordStats(wordSearch.getText().toUpperCase());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        task.setOnSucceeded(event -> {
            ServiceResultInterface res = task.getValue();
            observableGlist.clear();
            if (res != null & !res.getResultList().isEmpty()) {
                for (Result<?> r : res.getResultList()) {
                    UUID game = (UUID) r.getValue();
                    observableGlist.add(new Label(game.toString()));
                }
            }
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void setActiveLobby(ObservableLobby lobby) {
        this.activeLobby.set(lobby);
    }

    public void gameStarting(String[] gridF, Integer[] gridN, Instant startingTime) {
        gameLoadingService.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.longValue() == 1) {
                MatchController controller = new MatchController();
                controller.setActiveRoom(activeLobby.get());
                controller.setGridFaces(gridF);
                controller.setGridNumb(gridN);
                Parent parent;
                try {
                    parent = requestParent(ControllerType.MATCH, controller);
                    sceneTransitionAnimation(parent, SlideDirection.TO_BOTTOM).play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        long delay = Instant.now().until(startingTime, ChronoUnit.MILLIS);
        executorService.schedule(gameLoadingService, delay, TimeUnit.MILLISECONDS);
    }

    /*----------- Private methods for initialization and scaling -----------*/
    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleProfile(after);
        rescaleTabPane(after);
        rescaleIcons(after);
        rescaleButton(after);
        rescaleMasonry(after);
    }

    private ScheduledService<Void> getRoomReferesherService() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            Map<UUID, Lobby> map = Launcher.manager.requestRoomUpdate();
                            /* Keys that are present in both maps (either room hasn't changed or only it's status has changed */
                            List<UUID> alreadyPresentKeys = lobbyMap.keySet().stream()
                                    .filter(k -> map.containsKey(k))
                                    .collect(Collectors.toList());
                            /* Keys that are present in the map on the client side but are not present in the updated map
                            * received. It means the keys were removed and the room associated does not exist anymore. */
                            List<UUID> removedKeys = lobbyMap.keySet().stream()
                                    .filter(k -> !map.containsKey(k))
                                    .collect(Collectors.toList());
                            /* Keys that are present in the new received map but not in the map on the client side.
                            * It means the keys were added. */
                            List<UUID> addedKeys = map.keySet().stream()
                                    .filter(k -> !lobbyMap.containsKey(k))
                                    .collect(Collectors.toList());
                            for (UUID k : alreadyPresentKeys) {
                                if (!lobbyMap.get(k).getStatus().equals(map.get(k).getStatus())) {
                                    lobbyMap.get(k).setStatus(map.get(k).getStatus());
                                }
                            }
                            for (UUID k : removedKeys) {
                                lobbyMap.remove(k, lobbyMap.get(k));
                            }
                            for (UUID k : addedKeys) {
                                lobbyMap.put(k, ObservableLobby.toObservableLobby(map.get(k)));
                            }
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
        service.setPeriod(Duration.seconds(5));
        service.setRestartOnFailure(true);
        return service;
    }

    private ScheduledService<Void> getRoomPlayersService() {
        return new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> service = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        ArrayList<String> received = Launcher.manager.requestPlayerList(activeLobby.get().getRoomId());
                        List<Label> labels = new ArrayList<>();
                        received.stream()
                                .forEach(s -> labels.add(new Label(s)));
                        observablePlayerList.set(FXCollections.observableArrayList(labels));
                        return null;
                    }
                };
                return service;
            }
        };
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
        refreshIcon.setFontFamily("FontAwesome");
        refreshIcon.setIcon(FontAwesome.Glyph.REFRESH);
        refreshIcon.setFontSize(ref.getReferences().get("HOME_ICONS_SIZE"));
        infoCard1.setFontFamily("FontAwesome");
        infoCard1.setIcon(FontAwesome.Glyph.INFO);
        infoCard1.setFontSize(currentFontSize.get());
        infoCard2.setFontFamily("FontAwesome");
        infoCard2.setIcon(FontAwesome.Glyph.INFO);
        infoCard2.setFontSize(currentFontSize.get());
        infoCard3.setFontFamily("FontAwesome");
        infoCard3.setIcon(FontAwesome.Glyph.INFO);
        infoCard3.setFontSize(currentFontSize.get());
        infoCard4.setFontFamily("FontAwesome");
        infoCard4.setIcon(FontAwesome.Glyph.INFO);
        infoCard4.setFontSize(currentFontSize.get());
        infoCard5.setFontFamily("FontAwesome");
        infoCard5.setIcon(FontAwesome.Glyph.INFO);
        infoCard5.setFontSize(currentFontSize.get());
        infoCard6.setFontFamily("FontAwesome");
        infoCard6.setIcon(FontAwesome.Glyph.INFO);
        infoCard6.setFontSize(currentFontSize.get());
        infoCard7.setFontFamily("FontAwesome");
        infoCard7.setIcon(FontAwesome.Glyph.INFO);
        infoCard7.setFontSize(currentFontSize.get());
        infoCard8.setFontFamily("FontAwesome");
        infoCard8.setIcon(FontAwesome.Glyph.INFO);
        infoCard8.setFontSize(currentFontSize.get());
        infoCard9.setFontFamily("FontAwesome");
        infoCard9.setIcon(FontAwesome.Glyph.INFO);
        infoCard9.setFontSize(currentFontSize.get());
        infoCard10.setFontFamily("FontAwesome");
        infoCard10.setIcon(FontAwesome.Glyph.INFO);
        infoCard10.setFontSize(currentFontSize.get());
        infoCard11.setFontFamily("FontAwesome");
        infoCard11.setIcon(FontAwesome.Glyph.INFO);
        infoCard11.setFontSize(currentFontSize.get());
        infoCard12.setFontFamily("FontAwesome");
        infoCard12.setIcon(FontAwesome.Glyph.INFO);
        infoCard12.setFontSize(currentFontSize.get());
        infoCard13.setFontFamily("FontAwesome");
        infoCard13.setIcon(FontAwesome.Glyph.INFO);
        infoCard13.setFontSize(currentFontSize.get());
        Glyph searchIcon = new Glyph("FontAwesome", FontAwesome.Glyph.SEARCH);
        searchIcon.setFontSize(currentFontSize.get());
        searchBtn.setGraphic(searchIcon);
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
        refreshIcon.setFontSize(dimAfter);
        infoCard1.setFontSize(currentFontSize.get());
        infoCard2.setFontSize(currentFontSize.get());
        infoCard3.setFontSize(currentFontSize.get());
        infoCard4.setFontSize(currentFontSize.get());
        infoCard5.setFontSize(currentFontSize.get());
        infoCard6.setFontSize(currentFontSize.get());
        infoCard7.setFontSize(currentFontSize.get());
        infoCard8.setFontSize(currentFontSize.get());
        infoCard9.setFontSize(currentFontSize.get());
        infoCard10.setFontSize(currentFontSize.get());
    }

    private void rescaleButton(double after) {
        double h = after*ref.getReferences().get("HOME_ROOM_BTN_H") / ref.getReferences().get("REF_RESOLUTION");
        double w = after*ref.getReferences().get("HOME_ROOM_BTN_W") / ref.getReferences().get("REF_RESOLUTION");
        create_room_btn.setPrefSize(w, h);
        create_room_btn.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        join_room_btn.setPrefSize(w, h);
        join_room_btn.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
    }

    private void rescaleMasonry(double after) {
        //Player stats
        double pMasonryW = after*ref.getReferences().get("HOME_MASONRY_W") / ref.getReferences().get("REF_RESOLUTION");
        double pMasonryH = after*ref.getReferences().get("HOME_MASONRY_H") / ref.getReferences().get("REF_RESOLUTION");
        playerStatsMasonry.setCellHeight(pMasonryH);
        playerStatsMasonry.setCellWidth(pMasonryW);
        double prefWTable = after*ref.getReferences().get("HOME_PSTATSTABLE_W") / ref.getReferences().get("REF_RESOLUTION");
        double prefHTable = after*ref.getReferences().get("HOME_PSTATSTABLE_H") / ref.getReferences().get("REF_RESOLUTION");
        bestPMatchTable.setPrefSize(prefWTable, prefHTable);
        bestPTable.setPrefSize(prefWTable, prefHTable);
        avgBestPTable.setPrefSize(prefWTable, prefHTable);
        avgBestPMatchTable.setPrefSize(prefWTable, prefHTable);
        maxDuplWTable.setPrefSize(prefWTable, prefHTable);
        maxGamesTable.setPrefSize(prefWTable, prefHTable);
        maxWrongWTable.setPrefSize(prefWTable, prefHTable);
        double newFontSize = currentFontSize.get()-5;
        titleCard1p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard2p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard3p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard4p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard5p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard6p.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard7p.setStyle("-fx-font-size: " + newFontSize + ";");
        bestPMatchTable.setStyle("-fx-font-size: " + newFontSize + ";");
        bestPTable.setStyle("-fx-font-size: " + newFontSize + ";");
        avgBestPTable.setStyle("-fx-font-size: " + newFontSize + ";");
        avgBestPMatchTable.setStyle("-fx-font-size: " + newFontSize + ";");
        maxGamesTable.setStyle("-fx-font-size: " + newFontSize + ";");
        maxWrongWTable.setStyle("-fx-font-size: " + newFontSize + ";");
        maxDuplWTable.setStyle("-fx-font-size: " + newFontSize + ";");
        //Game Stats
        double gMasonryW = after*ref.getReferences().get("HOME_MASONRY2_W") / ref.getReferences().get("REF_RESOLUTION");
        double gMasonryH = after*ref.getReferences().get("HOME_MASONRY2_H") / ref.getReferences().get("REF_RESOLUTION");
        gameStatsMasonry.setCellWidth(gMasonryW);
        gameStatsMasonry.setCellHeight(gMasonryH);
        titleCard1g.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard2g.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard3g.setStyle("-fx-font-size: " + newFontSize + ";");
        double turnsW = after*ref.getReferences().get("HOME_TURNSCARDTBL_W") / ref.getReferences().get("REF_RESOLUTION");
        double turnsH = after*ref.getReferences().get("HOME_TURNSCARDTBL_H") / ref.getReferences().get("REF_RESOLUTION");
        turnPlayersTable.setPrefSize(turnsW, turnsH);
        turnPlayersTable.setStyle("-fx-font-size: " + newFontSize + ";");
        turnsGraph.setStyle("-fx-font-size: " + newFontSize + ";");
        double graphW = after*ref.getReferences().get("HOME_GRAPH_W") / ref.getReferences().get("REF_RESOLUTION");
        double graphH = after*ref.getReferences().get("HOME_GRAPH_H") / ref.getReferences().get("REF_RESOLUTION");
        gridGraph.setPrefSize(graphW, graphH);
        gridGraph.setStyle("-fx-font-size: " + newFontSize + ";");
        wordSearch.setStyle("-fx-font-size: " + newFontSize + ";");
        gListTitle.setStyle("-fx-font-size: " + (newFontSize - 2) + ";");
        gList.setStyle("-fx-font-size: " + newFontSize + ";");
        //Word stats
        double wMasonryW = after*ref.getReferences().get("HOME_MASONRY3_W") / ref.getReferences().get("REF_RESOLUTION");
        double wMasonryH = after*ref.getReferences().get("HOME_MASONRY3_H") / ref.getReferences().get("REF_RESOLUTION");
        wordStatsMasonry.setCellWidth(wMasonryW);
        wordStatsMasonry.setCellHeight(wMasonryH);
        titleCard1w.setStyle("-fx-font-size: " + newFontSize + ";");
        wTable1.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard2w.setStyle("-fx-font-size: " + newFontSize + ";");
        wTable2.setStyle("-fx-font-size: " + newFontSize + ";");
        titleCard3w.setStyle("-fx-font-size: " + newFontSize + ";");
        wTable3.setStyle("-fx-font-size: " + newFontSize + ";");
        double wtW = after*ref.getReferences().get("HOME_WPTABLE_W") / ref.getReferences().get("REF_RESOLUTION");
        wTable3.setPrefWidth(wtW);
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
        JFXPopup menu = new JFXPopup(menuContent);
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
                    if (activeLobby.get() != null) {
                        Launcher.manager.leaveRoom(activeLobby.get().getRoomId());
                    }
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
                menu.hide();
                if (activeLobby.get() != null) {
                    try {
                        Launcher.manager.leaveRoom(activeLobby.get().getRoomId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Launcher.manager.logout();
                    RoomCentralManager.stopRoomServer();
                    Parent mainMenu = requestParent(ControllerType.MAIN_MENU);
                    Timeline anim = sceneTransitionAnimation(mainMenu, SlideDirection.TO_RIGHT);
                    anim.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        });
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
        roomList.setItems(lobbiesList);
        lobbyMap.addListener((MapChangeListener<UUID, ObservableLobby>) change -> {
            /* If change is both a removal and an insert it's a modify change */
            if (change.wasAdded() & change.wasRemoved()) {
                lobbiesList.get(lobbiesList.indexOf(change.getValueAdded())).setStatus(change.getValueAdded().getStatus());
            } else if (change.wasRemoved()) {
                lobbiesList.remove(change.getValueRemoved());
            } else {
                lobbiesList.add(change.getValueAdded());
            }
        });
        name_col.setText("");
        name_col.setGraphic(generateTableHeaderTxtField(name_col_txt));
        name_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.3));
        name_col.setCellValueFactory(param -> param.getValue().roomNameProperty());
        players_col.setText("");
        players_col.setGraphic(generateTableHeaderCombo(players_col_txt, "PLAYERS"));
        players_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        players_col.setCellValueFactory(param -> param.getValue().numPlayersProperty().asObject());
        lang_col.setText("");
        lang_col.setGraphic(generateTableHeaderCombo(lang_col_text, "LANGUAGE"));
        lang_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        lang_col.setCellValueFactory(param -> param.getValue().languageProperty());
        ruleset_col.setText("");
        ruleset_col.setGraphic(generateTableHeaderCombo(rule_col_text, "RULESET"));
        ruleset_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        ruleset_col.setCellValueFactory(param -> param.getValue().rulesetProperty());
        status_col.setText("");
        status_col.setGraphic(generateTableHeaderCombo(status_col_txt, "STATUS"));
        status_col.prefWidthProperty().bind(roomList.widthProperty().multiply(0.175));
        status_col.setCellValueFactory(param -> param.getValue().statusProperty());
        roomList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                join_room_btn.setDisable(true);
            } else {
                join_room_btn.setDisable(false);
            }
        });
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

    private VBox generateTableHeaderCombo(StringProperty title_txt, String comboType) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(5);
        Label title = new Label();
        title.textProperty().bind(title_txt);
        title.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        switch(comboType) {
            case "PLAYERS" -> {
                JFXComboBox<Integer> comboBox = new JFXComboBox();
                comboBox.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
                comboBox.setItems(FXCollections.observableArrayList(2,3,4,5,6));
                vBox.getChildren().addAll(title, comboBox);
            }
            case "LANGUAGE" -> {
                JFXComboBox<Language> comboBox = new JFXComboBox<>();
                comboBox.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
                comboBox.setItems(FXCollections.observableArrayList(Language.values()));
                vBox.getChildren().addAll(title, comboBox);
            }
            case "RULESET" -> {
                JFXComboBox<Ruleset> comboBox = new JFXComboBox<>();
                comboBox.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
                comboBox.setItems(FXCollections.observableArrayList(Ruleset.values()));
                vBox.getChildren().addAll(title, comboBox);
            }
            case "STATUS" -> {
                JFXComboBox<Lobby.LobbyStatus> comboBox = new JFXComboBox<>();
                comboBox.setStyle("-fx-font-size: " + (currentFontSize.get() - 7) + ";");
                comboBox.setItems(FXCollections.observableArrayList(Lobby.LobbyStatus.values()));
                vBox.getChildren().addAll(title, comboBox);
            }
        }
        return vBox;
    }

    private void initLobby() {
        leaveIcon.setFontFamily("FontAwesome");
        leaveIcon.setIcon(FontAwesome.Glyph.SIGN_OUT);
        leaveIcon.setFontSize(currentFontSize.get());
        currentFontSize.addListener((observable, oldValue, newValue) -> {
            leaveIcon.setFontSize(newValue.doubleValue());
        });
        observablePlayerList.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> playersList.setItems(newValue));
        });
        activeLobby.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue == null) {
                    /* Means the player is not in a room */
                    lobbyView.setVisible(false);
                    create_room_btn.setDisable(false);
                    join_room_btn.setDisable(false);
                    roomList.setDisable(false);
                    roomPlayersUpdater.cancel();
                    roomPlayersUpdater = null;
                    lobbiesRefresher = getRoomReferesherService();
                    lobbiesRefresher.start();
                    return;
                }
                lobbyView.setText(newValue.getRoomName());
                lobbyPlayer_cont.setText(String.valueOf(newValue.getNumPlayers()));
                lobbyLang_cont.setText(newValue.getLanguage().name());
                lobbyRule_cont.setText(newValue.getRuleset().name());
                create_room_btn.setDisable(true);
                join_room_btn.setDisable(true);
                roomList.setDisable(true);
                lobbyView.setVisible(true);
//                if (lobbiesRefresher != null) {
                    lobbiesRefresher.cancel();
                    lobbiesRefresher = null;
//                }
                roomPlayersUpdater = getRoomPlayersService();
                roomPlayersUpdater.start();
            });
        });
    }

    private void initPlayerStatsTables() {
        bestPTable.setItems(bestPlayerGame);
        bestPMatchTable.setItems(bestPlayerMatch);
        avgBestPTable.setItems(bestAvgPlayerGame);
        avgBestPMatchTable.setItems(bestAvgPlayerMatch);
        maxGamesTable.setItems(maxGamesPlayed);
        maxDuplWTable.setItems(maxDuplWords);
        maxWrongWTable.setItems(maxWrongWords);
        bestPTable.setSelectionModel(null);
        bestPMatchTable.setSelectionModel(null);
        avgBestPMatchTable.setSelectionModel(null);
        avgBestPTable.setSelectionModel(null);
        maxGamesTable.setSelectionModel(null);
        maxDuplWTable.setSelectionModel(null);
        maxWrongWTable.setSelectionModel(null);
        Label desc1 = new Label();
        Label desc2 = new Label();
        Label desc3 = new Label();
        Label desc4 = new Label();
        Label desc5 = new Label();
        Label desc6 = new Label();
        Label desc7 = new Label();
        desc1.textProperty().bind(descCard1);
        desc2.textProperty().bind(descCard2);
        desc3.textProperty().bind(descCard3);
        desc4.textProperty().bind(descCard4);
        desc5.textProperty().bind(descCard5);
        desc6.textProperty().bind(descCard6);
        desc7.textProperty().bind(descCard7);
        desc1.getStyleClass().add("stats-card-desc");
        desc1.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver1 = new PopOver(desc1);
        popOver1.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver1.setDetachable(false);
        infoCard1.setOnMouseClicked(e -> popOver1.show(infoCard1));
        desc2.getStyleClass().add("stats-card-desc");
        desc2.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver2 = new PopOver(desc2);
        popOver2.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver2.setDetachable(false);
        infoCard2.setOnMouseClicked(e -> popOver2.show(infoCard2));
        desc3.getStyleClass().add("stats-card-desc");
        desc3.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver3 = new PopOver(desc3);
        popOver3.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver3.setDetachable(false);
        infoCard3.setOnMouseClicked(e -> popOver3.show(infoCard3));
        desc4.getStyleClass().add("stats-card-desc");
        desc4.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver4 = new PopOver(desc4);
        popOver4.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver4.setDetachable(false);
        infoCard4.setOnMouseClicked(e -> popOver4.show(infoCard4));
        desc5.getStyleClass().add("stats-card-desc");
        desc5.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver5 = new PopOver(desc5);
        popOver5.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver5.setDetachable(false);
        infoCard5.setOnMouseClicked(e -> popOver5.show(infoCard5));
        desc6.getStyleClass().add("stats-card-desc");
        desc6.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver6 = new PopOver(desc6);
        popOver6.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver6.setDetachable(false);
        infoCard6.setOnMouseClicked(e -> popOver6.show(infoCard6));
        desc7.getStyleClass().add("stats-card-desc");
        desc7.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver7 = new PopOver(desc7);
        popOver7.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver7.setDetachable(false);
        infoCard7.setOnMouseClicked(e -> popOver7.show(infoCard7));
        titleCard1p.textProperty().bind(titlePCard1);
        titleCard2p.textProperty().bind(titlePCard2);
        titleCard3p.textProperty().bind(titlePCard3);
        titleCard4p.textProperty().bind(titlePCard4);
        titleCard5p.textProperty().bind(titlePCard5);
        titleCard6p.textProperty().bind(titlePCard6);
        titleCard7p.textProperty().bind(titlePCard7);
        scoreCol1.textProperty().bind(scoreColLbl);
        scoreCol2.textProperty().bind(scoreColLbl);
        scoreCol3.textProperty().bind(scoreColLbl);
        scoreCol4.textProperty().bind(scoreColLbl);
        gamesCol1.textProperty().bind(gamesColLbl);
        wordCol1.textProperty().bind(wordColLbl);
        wordCol2.textProperty().bind(wordColLbl);
        idCol1.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol2.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol3.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol4.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol5.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol6.setCellValueFactory(param -> param.getValue().playerIDProperty());
        idCol7.setCellValueFactory(param -> param.getValue().playerIDProperty());
        scoreCol1.setCellValueFactory(param -> param.getValue().intScoreProperty().asObject());
        scoreCol2.setCellValueFactory(param -> param.getValue().intScoreProperty().asObject());
        scoreCol3.setCellValueFactory(param -> param.getValue().scoreProperty().asObject());
        scoreCol4.setCellValueFactory(param -> param.getValue().scoreProperty().asObject());
        gamesCol1.setCellValueFactory(param -> param.getValue().intScoreProperty().asObject());
        wordCol1.setCellValueFactory(param -> param.getValue().intScoreProperty().asObject());
        wordCol2.setCellValueFactory(param -> param.getValue().intScoreProperty().asObject());
    }

    private void initGameStats() {
        turnPlayersTable.setItems(turnStats);
        titleCard1g.textProperty().bind(titleGCard1);
        titleCard2g.textProperty().bind(titleGCard2);
        titleCard3g.textProperty().bind(titleGCard3);
        turnIdCol.textProperty().bind(playersCol);
        minTurns.textProperty().bind(minTurnsCol);
        maxTurns.textProperty().bind(maxTurnsCol);
        avgTurns.textProperty().bind(avgTurnsCol);
        turnIdCol.setCellValueFactory(param -> param.getValue().categoryProperty().asObject());
        minTurns.setCellValueFactory(param -> param.getValue().minTurnsProperty().asObject());
        maxTurns.setCellValueFactory(param -> param.getValue().maxTurnsProperty().asObject());
        avgTurns.setCellValueFactory(param -> param.getValue().avgTurnsProperty().asObject());
        Label desc8 = new Label();
        desc8.textProperty().bind(descCard8);
        Label desc9 = new Label();
        desc9.textProperty().bind(descCard9);
        Label desc10 = new Label();
        desc10.textProperty().bind(descCard10);
        desc8.getStyleClass().add("stats-card-desc");
        desc8.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        desc9.getStyleClass().add("stats-card-desc");
        desc9.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        desc10.getStyleClass().add("stats-card-desc");
        desc10.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver8 = new PopOver(desc8);
        popOver8.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver8.setDetachable(false);
        infoCard8.setOnMouseClicked(e -> popOver8.show(infoCard8));
        PopOver popOver9 = new PopOver(desc9);
        popOver9.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver9.setDetachable(false);
        infoCard9.setOnMouseClicked(e -> popOver9.show(infoCard9));
        PopOver popOver10 = new PopOver(desc10);
        popOver10.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver10.setDetachable(false);
        infoCard10.setOnMouseClicked(e -> popOver10.show(infoCard10));
        turnSeries1.nameProperty().bind(minTurnsCol);
        turnSeries2.nameProperty().bind(maxTurnsCol);
        turnSeries3.nameProperty().bind(avgTurnsCol);
        turnsGraph.getXAxis().labelProperty().bind(playersCol);
        CategoryAxis categoryAxis = (CategoryAxis) turnsGraph.getXAxis();
        categoryAxis.setCategories(FXCollections.observableArrayList("2", "3", "4", "5", "6"));
        gridGraph.getXAxis().labelProperty().bind(letters_text);
        gridGraph.getYAxis().labelProperty().bind(avgOccurr_text);
        gList.setItems(observableGlist);
    }

    private void initWordStats() {
        titleCard1w.textProperty().bind(titleWCard1);
        wTable1.setItems(validWordRankingList);
        wCol1.textProperty().bind(wordCol);
        occCol1.textProperty().bind(occurCol);
        wCol1.setCellValueFactory(param -> param.getValue().wordProperty());
        occCol1.setCellValueFactory(param -> param.getValue().occurrProperty().asObject());
        occCol1.setSortType(TableColumn.SortType.DESCENDING);
        wCol1.setSortType(TableColumn.SortType.ASCENDING);
        wTable1.getSortOrder().addAll(occCol1, wCol1);
        Label desc11 = new Label();
        desc11.textProperty().bind(descCard11);
        desc11.getStyleClass().add("stats-card-desc");
        desc11.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver11 = new PopOver(desc11);
        popOver11.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver11.setDetachable(false);
        infoCard11.setOnMouseClicked(e -> popOver11.show(infoCard11));
        titleCard2w.textProperty().bind(titleWCard2);
        wTable2.setItems(reqWordRankingList);
        wCol2.textProperty().bind(wordCol);
        occCol2.textProperty().bind(occurCol);
        wCol2.setCellValueFactory(param -> param.getValue().wordProperty());
        occCol2.setCellValueFactory(param -> param.getValue().occurrProperty().asObject());
        occCol2.setSortType(TableColumn.SortType.DESCENDING);
        wCol2.setSortType(TableColumn.SortType.ASCENDING);
        wTable2.getSortOrder().addAll(occCol2, wCol2);
        Label desc12 = new Label();
        desc12.textProperty().bind(descCard12);
        desc12.getStyleClass().add("stats-card-desc");
        desc12.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver12 = new PopOver(desc12);
        popOver12.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver12.setDetachable(false);
        infoCard12.setOnMouseClicked(e -> popOver12.show(infoCard12));
        titleCard3w.textProperty().bind(titleWCard3);
        wTable3.setItems(wordGamePointsList);
        wCol3.textProperty().bind(wordCol);
        scoreColw1.textProperty().bind(scoreColLbl);
        gameCol1.textProperty().bind(gameCol);
        wCol3.setCellValueFactory(param -> param.getValue().wordProperty());
        scoreColw1.setCellValueFactory(param -> param.getValue().pointsProperty().asObject());
        gameCol1.setCellValueFactory(param -> param.getValue().gameProperty());
        Label desc13 = new Label();
        desc13.textProperty().bind(descCard13);
        desc13.getStyleClass().add("stats-card-desc");
        desc13.setStyle("-fx-font-size: " + (currentFontSize.get() - 5) + ";");
        PopOver popOver13 = new PopOver(desc13);
        popOver13.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver13.setDetachable(false);
        infoCard13.setOnMouseClicked(e -> popOver13.show(infoCard13));
    }

    //++ Services initialization ++//
    private void initLoadingAnim() {
        generalLoadingOverlay = new LoadingAnimationOverlay(root, "");
    }

    private void initServicesAndTasks() {
        initLoadingAnim();
        initActiveLobbyService();
        initGameLoadingService();
    }

    private void initActiveLobbyService() {
        activeLobbyService = new Service<>() {
            @Override
            protected Task<ObservableLobby> createTask() {
                Task<ObservableLobby> task  = new Task<>() {
                    private List<String> errors;

                    @Override
                    protected ObservableLobby call() throws Exception {
                        ObservableLobby selectedLobby = roomList.getSelectionModel().getSelectedItem();
                        errors = Launcher.manager.joinRoom(selectedLobby.getRoomId());
                        if (!errors.isEmpty()) {
                            selectedLobby = null;
                        }
                        updateValue(selectedLobby);
                        return selectedLobby;
                    }

                    @Override
                    protected void running() {
                        super.running();
                        Platform.runLater(generalLoadingOverlay::playAnimation);
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(generalLoadingOverlay::stopAnimation);
                        if (!errors.isEmpty()) {
                            List<Text> localized = new ArrayList<>();
                            for (String err : errors) {
                                String localMsg = Launcher.contrManager.getBundleValue().getString(err);
                                Text text = new Text(localMsg + "\n");
                                text.setFill(Color.RED);
                                localized.add(text);
                            }
                            Text[] localizedErrors = new Text[localized.size()];
                            Platform.runLater(() -> {
                                generateDialog((StackPane) root, root.getWidth()/2, "ERROR",
                                        localized.toArray(localizedErrors)).show();
                            });
                        }
                    }

                    @Override
                    protected void cancelled() {
                        super.cancelled();
                        Platform.runLater(generalLoadingOverlay::stopAnimation);
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        Platform.runLater(generalLoadingOverlay::stopAnimation);
                        if (getException() instanceof SocketException) {
                            Platform.runLater(() -> {
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
                                    serverAlert((StackPane) root, root.getWidth()/2).show();
                                });
                            }
                        }
                    }
                };
                return task;
            }
        };
    }

    private void initGameLoadingService() {
        gameLoadingService = new Task<>() {
            private StackPane overlay;
            private Timeline timeline;

            @Override
            protected Void call() throws Exception {
                StackPane over = new StackPane();
                over.setId("bg-loading");
                over.setAlignment(Pos.CENTER);
                VBox vBox = new VBox();
                vBox.setSpacing(60);
                vBox.setAlignment(Pos.CENTER);
                JFXProgressBar progressBar = new JFXProgressBar();
                Label msg = new Label(Launcher.contrManager.getBundleValue().getString("game_loading_lbl"));
                msg.getStyleClass().add("game-loading-msg");
                vBox.getChildren().addAll(progressBar, msg);
                over.getChildren().add(vBox);
                progressBar.progressProperty().setValue(0);
                overlay = over;
                java.time.Duration preGameTimer = activeLobby.get().getRuleset().getTimeToStart();
                Timeline tl = new Timeline(
                        new KeyFrame(Duration.seconds(preGameTimer.getSeconds()),
                                new KeyValue(progressBar.progressProperty(), 1))
                );
                tl.setCycleCount(1);
                tl.setOnFinished(e -> updateProgress(1, 1));
                timeline = tl;
                return null;
            }

            @Override
            protected void scheduled() {
                super.scheduled();
                Platform.runLater(() -> {
                    root.getChildren().add(overlay);
                });
                timeline.play();
            }
        };
    }
}
