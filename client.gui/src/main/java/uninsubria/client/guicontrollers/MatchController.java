package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.client.customcontrols.GameGrid;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.ObservableLobby;
import uninsubria.client.roomserver.RoomCentralManager;
import uninsubria.client.roomserver.TimeoutMonitor;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.Word;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Controller for the match view.
 *
 * @author Giulia Pais
 * @version 0.9.9
 */
public class MatchController extends AbstractMainController {
    /*---Fields---*/
    @FXML StackPane gameGrid, startingOverlay, scoresOverlay, winnerOverlay, loadingScoresOverlay;
    @FXML AnchorPane header;
    @FXML Label timerSeconds, timerMinutes, matchNumLbl, currScoreLbl, currScoreValueLbl, foundWord, wordListLbl, startingCountDown,
            scoresSecLbl, scoresMinLbl, scoresTitle, winnerLbl, winnerTitle;
    @FXML VBox instructionSidePanel, wordsSidePanel;
    @FXML JFXButton leaveGameBtn, insertBtn, clearBtn, readyBtn, requestBtn;
    @FXML JFXListView<String> wordsListView;
    @FXML TextFlow instructions;
    @FXML TilePane tilePane;
    @FXML TableView<Map.Entry<StringProperty, IntegerProperty>> playersTable;
    @FXML TableColumn<Map.Entry<StringProperty, IntegerProperty>, String> playeridCol;
    @FXML TableColumn<Map.Entry<StringProperty, IntegerProperty>, Integer> scoreCol;

    private HomeController homeReference;
    private ObservableLobby activeRoom;
    private List<String> participants;
    private String[] gridFaces;
    private Integer[] gridNumb;
    private GameGrid matchGrid;
    private Boolean newMatchAvailable = false;
    private ObjectProperty<Duration> matchTimerDuration, timeoutTimerDuration;
    private IntegerProperty minutesPart, secondsPart, playerScore, matchNumber, timeoutMin, timeoutSec;
    private ScheduledService<Duration> timerCountDownService, timerTimeout;
    private StringProperty currScoreTxt, wordListTxt, leaveGameTxt, insertTxt, clearTxt, instr1, instr2, instr3, instr4,
                            loadingScoresTxt, gameInterrBody;
    private ListProperty<String> wordFoundList;
    private ScheduledService<String> startingCountDownService;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    //++ Internals for keeping scores ++//
    private MapProperty<StringProperty, IntegerProperty> gameScores;
    private HashMap<String, IntegerProperty> lastMatchScores;
    private HashMap<String, ListProperty<Word>> proposedMatchWords;
    private StringProperty winnerName;
    private ListProperty<Map.Entry<StringProperty, IntegerProperty>> scoresListTable;

    //++ For timeout synch ++//
    private TimeoutMonitor monitor;

    //++ For interrupt ++//
    private boolean interrupted = false;
    private boolean leaving = false;

    /*---Constructors---*/
    public MatchController() {
        this.matchTimerDuration = new SimpleObjectProperty<>();
        this.timeoutTimerDuration = new SimpleObjectProperty<>();
        this.minutesPart = new SimpleIntegerProperty();
        this.secondsPart = new SimpleIntegerProperty();
        this.playerScore = new SimpleIntegerProperty(0);
        this.matchNumber = new SimpleIntegerProperty(1);
        this.currScoreTxt = new SimpleStringProperty();
        this.wordListTxt = new SimpleStringProperty();
        this.leaveGameTxt = new SimpleStringProperty();
        this.insertTxt = new SimpleStringProperty();
        this.clearTxt = new SimpleStringProperty();
        this.instr1 = new SimpleStringProperty();
        this.instr2 = new SimpleStringProperty();
        this.instr3 = new SimpleStringProperty();
        this.instr4 = new SimpleStringProperty();
        this.wordFoundList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.loadingScoresTxt = new SimpleStringProperty();
        this.winnerName = new SimpleStringProperty();
        this.timeoutMin = new SimpleIntegerProperty();
        this.timeoutSec = new SimpleIntegerProperty();
        this.gameScores = new SimpleMapProperty<>();
        this.scoresListTable = new SimpleListProperty<>();
        this.gameInterrBody = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        startingOverlay.setVisible(true);
        winnerOverlay.setVisible(false);
        loadingScoresOverlay.setVisible(false);
        initZeroScores();
        initTable();
        initGameGrid();
        initTimerService();
        initTimerTimeoutService();
        initTimerBindings();
        initHeaderLabels();
        bindLocalizedLabels();
        initWordsList();
        initInstructions();
        initPreCountDownService();
        try {
            initScoresOverlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startingCountDownService.start();
    }

    @FXML void insertWord() {
        String word = foundWord.getText();
        if (word.length() >= 3) {
            if (!wordFoundList.contains(word)) {
                wordFoundList.add(word);
            }
            matchGrid.clearSelection();
        }
    }

    @FXML void clearGrid() {
        matchGrid.clearSelection();
    }

    @FXML void setReady() {
        monitor.signalReady();
        readyBtn.setDisable(true);
        requestBtn.setDisable(true);
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
        currScoreTxt.set(resBundle.getString("curr_score"));
        wordListTxt.set(resBundle.getString("word_list"));
        leaveGameTxt.set(resBundle.getString("leave_game_btn"));
        insertTxt.set(resBundle.getString("insert"));
        clearTxt.set(resBundle.getString("clear"));
        instr1.set("\u2727 " + resBundle.getString("instructions_select") + "\n\n");
        instr2.set("\u2727 " + resBundle.getString("instructions_insert") + "\n\n");
        instr3.set("\u2727 " + resBundle.getString("instructions_clear") + "\n\n");
        instr4.set("\u2727 " + resBundle.getString("instructions_delete") + "\n\n");
        loadingScoresTxt.set(resBundle.getString("loading_scores"));
        gameInterrBody.set(resBundle.getString("game_interr_body"));
    }

    public void setActiveRoom(ObservableLobby activeRoom) {
        this.activeRoom = activeRoom;
    }

    public void setMonitor(TimeoutMonitor monitor) {
        this.monitor = monitor;
    }

    public void setMatchGrid(String[] gridFaces, Integer[] gridNumb) {
        matchGrid.resetGrid(gridFaces, gridNumb);
        newMatchAvailable = true;
        matchNumber.set(matchNumber.get() + 1);
        wordFoundList.clear();
        matchGrid.clearSelection();
        timerTimeout.cancel();
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void setFirstMatchGrid(String[] gridFaces, Integer[] gridNumb) {
        this.gridFaces = gridFaces;
        this.gridNumb = gridNumb;
    }

    public ArrayList<String> getFoundWords() {
        ArrayList<String> words = new ArrayList<>();
        words.addAll(wordFoundList.get());
        return words;
    }

    public void setMatchScores(GameScore gameScore) {
        gameScore.getScores().entrySet().stream()
                .forEach(entry -> {
                    lastMatchScores.get(entry.getKey()).set(entry.getValue()[0]);
                    gameScores.entrySet().stream()
                            .filter(e -> e.getKey().get().equals(entry.getKey()))
                            .forEach(e -> e.getValue().set(entry.getValue()[1]));
                });
        Integer score = gameScore.getScores().get(Launcher.manager.getProfile().getPlayerID())[1];
        playerScore.set(score);
        gameScore.getMatchWords().entrySet().stream()
                .forEach(entry -> {
                    proposedMatchWords.get(entry.getKey()).clear();
                    proposedMatchWords.get(entry.getKey()).addAll(Arrays.asList(entry.getValue()));
                });
        winnerName.set(gameScore.getWinner());
    }

    public void setTimerMatchTimeout() {
        loadingScoresOverlay.setVisible(false);
        readyBtn.setDisable(false);
        requestBtn.setDisable(false);
        scoresOverlay.setVisible(true);
        switch (timerTimeout.getState()) {
            case FAILED, CANCELLED, SUCCEEDED -> {
                initTimerTimeoutService();
                timerTimeout.start();
            }
            case READY, SCHEDULED -> timerTimeout.start();
        }
    }

    public void setEndGame() {
        timerTimeout.cancel();
    }

    public Duration getTimeoutDuration() {
        return activeRoom.getRuleset().getTimeToWaitFromMatchToMatch();
    }

    @Override
    protected void scaleFontSize(double after) {
        super.scaleFontSize(after);
        scoresOverlay.setStyle("-fx-font-size: "+ currentFontSize.get() + "px;");
    }

    public void setHomeReference(HomeController homeReference) {
        this.homeReference = homeReference;
    }

    @FXML void leaveGame() {
        leaving = true;
        RoomCentralManager.stopRoom();
        RoomCentralManager.stopRoomServer();
        timerCountDownService.cancel();
    }

    public void interruptGame() {
        if (!leaving) {
            interrupted = true;
            ScheduledService<Duration> service5sec = new ScheduledService<>() {
                private Duration notifDuration = Duration.ofSeconds(5);

                @Override
                protected Task<Duration> createTask() {
                    return new Task<>() {
                        @Override
                        protected Duration call() {
                            Duration decrement = notifDuration.minusSeconds(1);
                            notifDuration = decrement;
                            updateValue(decrement);
                            return decrement;
                        }
                    };
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    if (getLastValue().equals(Duration.ZERO)) {
                        this.cancel();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (getLastValue().equals(Duration.ZERO)) {
                        this.cancel();
                    }
                }
            };
            service5sec.setDelay(javafx.util.Duration.seconds(1));
            service5sec.setExecutor(scheduledExecutorService);
            service5sec.lastValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(Duration.ZERO)) {
                    Parent p;
                    try {
                        HomeController homeView = new HomeController();
                        homeView.setActiveLobby(activeRoom);
                        p = requestParent(ControllerType.HOME_VIEW, homeView);
                        sceneTransitionAnimation(p, SlideDirection.TO_BOTTOM).play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            timerCountDownService.cancel();
            timerTimeout.cancel();
            notification(gameInterrBody.get(), javafx.util.Duration.seconds(5));
            service5sec.start();
        }
    }

    /*----------- Private methods for initialization and scaling -----------*/
    private void initTimerBindings() {
        matchTimerDuration.set(activeRoom.getRuleset().getTimeToMatch());
        minutesPart.set(matchTimerDuration.getValue().toMinutesPart());
        secondsPart.set(matchTimerDuration.getValue().toSecondsPart());
        timerMinutes.setText(minutesPart.asString("%02d").getValue());
        timerSeconds.setText(secondsPart.asString("%02d").getValue());
        matchTimerDuration.addListener((observable, oldValue, newValue) -> {
            secondsPart.set(newValue.toSecondsPart());
            minutesPart.set(newValue.toMinutesPart());
        });
        timerSeconds.textProperty().bind(secondsPart.asString("%02d"));
        timerMinutes.textProperty().bind(minutesPart.asString("%02d"));
        timeoutTimerDuration.addListener((observable, oldValue, newValue) -> {
            timeoutSec.set(newValue.toSecondsPart());
            timeoutMin.set(newValue.toMinutesPart());
        });
        scoresSecLbl.textProperty().bind(timeoutSec.asString("%02d"));
        scoresMinLbl.textProperty().bind(timeoutMin.asString("%02d"));
    }

    private void initGameGrid() {
        matchGrid = new GameGrid(gridFaces, gridNumb);
        matchGrid.setFontSize(currentFontSize.get());
        matchGrid.fontSizeProperty().bind(currentFontSize);
        gameGrid.getChildren().add(matchGrid);
        gameGrid.setOnKeyPressed(k -> {
            if (k.getCode().equals(KeyCode.INSERT)) {
                insertWord();
            }
            if (k.getCode().equals(KeyCode.ESCAPE)) {
                clearGrid();
            }
        });
    }

    private void initWordsList() {
        wordsListView.setCellFactory(param -> {
            WordListCell cell = new WordListCell();
            cell.setFontSize(currentFontSize.get());
            cell.fontSizeProperty().bind(currentFontSize);
            return cell;
        });
        wordsListView.itemsProperty().bindBidirectional(wordFoundList);
    }

    private void initHeaderLabels() {
        matchNumLbl.setText(Integer.toString(matchNumber.get()));
        matchNumLbl.textProperty().bind(matchNumber.asString());
        currScoreValueLbl.setText(Integer.toString(playerScore.get()));
        currScoreValueLbl.textProperty().bind(playerScore.asString());
        foundWord.textProperty().bind(matchGrid.formedWordProperty());
    }

    private void initInstructions() {
        instructions.prefWidthProperty().bind(instructionSidePanel.prefWidthProperty());
        instructions.setStyle("-fx-font-size: " + (currentFontSize.get() - 2) + ";");
        currentFontSize.addListener((observable, oldValue, newValue) -> {
            instructions.setStyle("-fx-font-size: " + (newValue.doubleValue() - 2) + ";");
        });
        Text i1 = new Text();
        i1.textProperty().bind(instr1);
        i1.getStyleClass().add("instruction");
        Text i2 = new Text();
        i2.textProperty().bind(instr2);
        i2.getStyleClass().add("instruction");
        Text i3 = new Text();
        i3.textProperty().bind(instr3);
        i3.getStyleClass().add("instruction");
        Text i4 = new Text();
        i4.textProperty().bind(instr4);
        i4.getStyleClass().add("instruction");
        instructions.getChildren().addAll(i1, i2, i3, i4);

    }

    private void initZeroScores() {
        HashMap<StringProperty, IntegerProperty> zeroGameScores = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroGameScores.put(new SimpleStringProperty(p), new SimpleIntegerProperty(0)));
        gameScores.set(FXCollections.observableMap(zeroGameScores));
        HashMap<String, IntegerProperty> zeroMatchScores = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroMatchScores.put(p, new SimpleIntegerProperty(0)));
        lastMatchScores = zeroMatchScores;
        HashMap<String, ListProperty<Word>> zeroWords = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroWords.put(p, new SimpleListProperty<>(FXCollections.observableArrayList())));
        proposedMatchWords = zeroWords;
    }

    private void initScoresOverlay() throws IOException {
        for (String player :  participants) {
            PlayerScoreTile tile = new PlayerScoreTile();
            tile.setFontSize(currentFontSize.get());
            currentFontSize.addListener((observable, oldValue, newValue) -> {
                tile.setFontSize(newValue.doubleValue());
            });
            tile.setPlayer(player);
            gameScores.entrySet().stream()
                    .filter(entry -> entry.getKey().get().equals(player))
                    .forEach(entry -> entry.getValue().addListener((observable, oldValue, newValue) -> tile.setGameScore(newValue.intValue())));
            proposedMatchWords.get(player).addListener((observable, oldValue, newValue) -> {
                tile.setWordList(newValue);
            });
            lastMatchScores.get(player).addListener((observable, oldValue, newValue) -> {
                tile.setMatchScore(newValue.intValue());
            });
            StackPane tileRoot = newPlayerScoreTile(tile);
            tilePane.prefTileHeightProperty().addListener((observable, oldValue, newValue) -> {
                tileRoot.setPrefHeight(newValue.doubleValue());
            });
            tilePane.prefTileWidthProperty().addListener((observable, oldValue, newValue) -> {
                tileRoot.setPrefWidth(newValue.doubleValue());
            });
            tilePane.getChildren().add(tileRoot);
        }
        winnerName.addListener((observable, oldValue, newValue) -> winnerLbl.setText(newValue));
    }

    private void initTable() {
        List<Map.Entry<StringProperty, IntegerProperty>> entries = new ArrayList<>(gameScores.entrySet());
        scoresListTable.set(FXCollections.observableArrayList(entries));
        gameScores.addListener((MapChangeListener<StringProperty, IntegerProperty>) change -> {
            List<Map.Entry<StringProperty, IntegerProperty>> ent = new ArrayList<>(gameScores.entrySet());
            scoresListTable.set(FXCollections.observableArrayList(ent));
        });
        playeridCol.setCellValueFactory(param -> param.getValue().getKey());
        scoreCol.setCellValueFactory(param -> param.getValue().getValue().asObject());
        playersTable.itemsProperty().bind(scoresListTable);
    }

    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleHeader(after);
        rescaleGrid(after);
        rescaleSidePanels(after);
        rescaleScoresOverlay(after);
    }

    private void rescaleHeader(double after) {
        double headerH = (after*ref.getReferences().get("MATCH_HEADER_H")) / ref.getReferences().get("REF_RESOLUTION");
        header.setPrefHeight(headerH);
        header.setStyle("-fx-font-size: " + (currentFontSize.get() + 5) + ";");
        double foundW = (after*ref.getReferences().get("MATCH_FOUNDW_W")) / ref.getReferences().get("REF_RESOLUTION");
        foundWord.setPrefWidth(foundW);
        double btnW = (after*ref.getReferences().get("MATCH_BTN_DIM")) / ref.getReferences().get("REF_RESOLUTION");
        clearBtn.setPrefWidth(btnW);
        insertBtn.setPrefWidth(btnW);
    }

    private void rescaleGrid(double after) {
        double sizeAf = (after*ref.getReferences().get("MATCH_GRID_DIM")) / ref.getReferences().get("REF_RESOLUTION");
        gameGrid.setPrefSize(sizeAf, sizeAf);
    }

    private void rescaleSidePanels(double after) {
        double sizeAf = (after*ref.getReferences().get("MATCH_SIDEPANEL_W")) / ref.getReferences().get("REF_RESOLUTION");
        instructionSidePanel.setPrefWidth(sizeAf);
        wordsSidePanel.setPrefWidth(sizeAf);
    }

    private void rescaleScoresOverlay(double after) {
        double titleFontSize = (after*ref.getReferences().get("SCORES_TITLE_SIZE")) / ref.getReferences().get("REF_RESOLUTION");
        scoresTitle.setStyle("-fx-font-size: " + titleFontSize + ";");
        double prefTileH = (after*ref.getReferences().get("SCORES_TILE_H")) / ref.getReferences().get("REF_RESOLUTION");
        double prefTileW = (after*ref.getReferences().get("SCORES_TILE_W")) / ref.getReferences().get("REF_RESOLUTION");
        tilePane.setPrefTileHeight(prefTileH);
        tilePane.setPrefTileWidth(prefTileW);
        double btnW = (after*ref.getReferences().get("SCORES_BTN_DIM")) / ref.getReferences().get("REF_RESOLUTION");
        readyBtn.setPrefWidth(btnW);
        requestBtn.setPrefWidth(btnW);
        winnerLbl.setStyle("-fx-font-size: " + (currentFontSize.get() + 20) + ";");
        winnerTitle.setStyle("-fx-font-size: " + (currentFontSize.get() + 20) + ";");
    }

    private void bindLocalizedLabels() {
        currScoreLbl.textProperty().bind(currScoreTxt);
        wordListLbl.textProperty().bind(wordListTxt);
        leaveGameBtn.textProperty().bind(leaveGameTxt);
        insertBtn.textProperty().bind(insertTxt);
        clearBtn.textProperty().bind(clearTxt);
    }

    private StackPane newPlayerScoreTile(PlayerScoreTile tileController) throws IOException {
        return Launcher.contrManager.loadPlayerScoreTile(ControllerType.SCORE_TILE.getFile(), tileController);
    }

    //++ Initialize services ++//
    //Service for the match timer
    private void initTimerService() {
        matchTimerDuration.set(activeRoom.getRuleset().getTimeToMatch());
        ScheduledService<Duration> service = new ScheduledService<>() {
            @Override
            protected Task<Duration> createTask() {
                Task<Duration> task = new Task<>() {
                    @Override
                    protected Duration call() {
                        Duration decrement = matchTimerDuration.get().minusSeconds(1);
                        updateValue(decrement);
                        return decrement;
                    }
                };
                return task;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                if (!interrupted & !leaving) {
                    loadingScoresOverlay.setVisible(true);
                } else if (leaving) {
                    try {
                        Launcher.manager.leaveGame(activeRoom.getRoomId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        HomeController home = new HomeController();
                        Parent parent = null;
                        try {
                            parent = requestParent(ControllerType.HOME_VIEW, home);
                            sceneTransitionAnimation(parent, SlideDirection.TO_BOTTOM).play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                return true;
            }
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(scheduledExecutorService);
        timerCountDownService = service;
        timerCountDownService.lastValueProperty().addListener((observable, oldValue, newValue) -> matchTimerDuration.set(newValue));
    }

    //Service for the timeout timer
    private void initTimerTimeoutService() {
        timeoutTimerDuration.set(activeRoom.getRuleset().getTimeToWaitFromMatchToMatch());
        ScheduledService<Duration> service = new ScheduledService<>() {
            @Override
            protected Task<Duration> createTask() {
                Task<Duration> task = new Task<>() {
                    @Override
                    protected Duration call() {
                        Duration decrement = timeoutTimerDuration.get().minusSeconds(1);
                        updateValue(decrement);
                        return decrement;
                    }
                };
                return task;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                if (!interrupted & !leaving) {
                    if (newMatchAvailable) {
                        scoresOverlay.setVisible(false);
                        startingOverlay.setVisible(true);
                        switch (startingCountDownService.getState()) {
                            case FAILED, CANCELLED, SUCCEEDED -> {
                                initPreCountDownService();
                                startingCountDownService.start();
                            }
                            case READY, SCHEDULED -> startingCountDownService.start();
                        }
                    } else {
                        winnerOverlay.setVisible(true);
                        try {
                            Thread.sleep(Duration.ofSeconds(10).toMillis());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            Parent parent = requestParent(ControllerType.HOME_VIEW, homeReference);
                            sceneTransitionAnimation(parent, SlideDirection.TO_RIGHT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (leaving) {
                    Platform.runLater(() -> {
                        RoomCentralManager.stopRoom();
                        RoomCentralManager.stopRoomServer();
                        HomeController home = new HomeController();
                        Parent parent = null;
                        try {
                            parent = requestParent(ControllerType.HOME_VIEW, home);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sceneTransitionAnimation(parent, SlideDirection.TO_BOTTOM).play();
                    });
                }
                return true;
            }
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(scheduledExecutorService);
        timerTimeout = service;
        timerTimeout.lastValueProperty().addListener((observable, oldValue, newValue) -> timeoutTimerDuration.set(newValue));
    }

    private void initPreCountDownService() {
        ScheduledService<String> service = new ScheduledService<>() {
            private int cycle = 1;

            @Override
            protected Task<String> createTask() {
                return new Task<>() {
                    @Override
                    protected String call() {
                        String val = switch (cycle) {
                            case 1 -> "3";
                            case 2 -> "2";
                            case 3 -> "1";
                            case 4 -> "Go!";
                            default -> null;
                        };
                        updateValue(val);
                        return val;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        cycle++;
                    }
                };
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (cycle == 5) {
                    this.cancel();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (cycle == 5) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                if (!interrupted) {
                    startingOverlay.setVisible(false);
                    newMatchAvailable = false;
                    switch (timerCountDownService.getState()) {
                        case FAILED, CANCELLED, SUCCEEDED -> {
                            initTimerService();
                            timerCountDownService.start();
                        }
                        case READY, SCHEDULED -> timerCountDownService.start();
                    }
                }
                return true;
            }
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(1));
        service.setExecutor(scheduledExecutorService);
        service.lastValueProperty().addListener((observable, oldValue, newValue) -> startingCountDown.setText(newValue));
        startingCountDownService = service;
    }
}
