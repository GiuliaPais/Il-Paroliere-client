package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
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
import uninsubria.client.monitors.*;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.Word;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Controller for the match view.
 *
 * @author Giulia Pais
 * @version 0.9.12
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

    private ObservableLobby activeRoom;
    private List<String> participants;
    private GameGrid matchGrid;
    private ObjectProperty<Duration> matchTimerDuration, timeoutTimerDuration;
    private IntegerProperty minutesPart, secondsPart, playerScore, matchNumber, timeoutMin, timeoutSec;
    private StringProperty currScoreTxt, wordListTxt, leaveGameTxt, insertTxt, clearTxt, instr1, instr2, instr3, instr4,
                            loadingScoresTxt, gameInterrBody, leavingHead, leavingBody;
    private ListProperty<String> wordFoundList;

    private IntegerProperty preCountDownCycle;
    private BooleanProperty gameEnded;
    private BooleanBinding loadingScoresVisible;

    //++ Services and Tasks ++//
    private ScheduledService<Integer> startingCountDownService;
    private ScheduledService<Duration> timerCountDownService, timerTimeout;
    private ScheduledService<MatchGridMonitor.GridWrapper> newMatchListener;
    private ScheduledService<GameScore> scoresListener;
    private ScheduledService<Void> wordRequestListener, timeoutListener;

    //++ Thread Pools ++//
    private ScheduledExecutorService backgroundExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ExecutorService parallelExecutorService = Executors.newCachedThreadPool();

    //++ Internals for keeping scores ++//
    private MapProperty<StringProperty, IntegerProperty> gameScores;
    private HashMap<String, IntegerProperty> lastMatchScores;
    private HashMap<String, ListProperty<Word>> proposedMatchWords;
    private StringProperty winnerName;
    private ListProperty<Map.Entry<StringProperty, IntegerProperty>> scoresListTable;
    private Set<String> requestedWords;
    private ObjectProperty<GameScore> lastGameScore;

    //++ For timeout synch ++//
    private TimeoutMonitor timeoutMonitor;
    private MatchGridMonitor matchGridMonitor;
    private InterruptMonitor interruptMonitor;
    private SendWordsMonitor sendWordsMonitor;
    private GameScoresMonitor gameScoresMonitor;
    private EndGameMonitor endGameMonitor;

    //++ For interrupt ++//
    private BooleanProperty interrupted;
    private BooleanProperty leaving;
    private ObjectProperty<MatchGridMonitor.GridWrapper> newGrid;
    private BooleanProperty isInTimeout;

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
        this.requestedWords = Collections.synchronizedSet(new HashSet<>());
        this.interrupted = new SimpleBooleanProperty(false);
        this.leaving = new SimpleBooleanProperty(false);
        this.sendWordsMonitor = new SendWordsMonitor();
        this.gameScoresMonitor = new GameScoresMonitor();
        this.endGameMonitor = new EndGameMonitor();
        this.newGrid = new SimpleObjectProperty<>();
        this.preCountDownCycle = new SimpleIntegerProperty(4);
        this.lastGameScore = new SimpleObjectProperty<>();
        this.isInTimeout = new SimpleBooleanProperty(false);
        this.gameEnded = new SimpleBooleanProperty(false);
        this.leavingHead = new SimpleStringProperty();
        this.leavingBody = new SimpleStringProperty();
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        timeoutMonitor = new TimeoutMonitor(activeRoom.getRuleset().getTimeToWaitFromMatchToMatch().toMillis());
        Launcher.manager.setMatchMonitors(sendWordsMonitor, timeoutMonitor, gameScoresMonitor, endGameMonitor);
        Task<Boolean> interruptListenerTask = listenForInterruptTask();
        parallelExecutorService.execute(interruptListenerTask);
        try {
            MatchGridMonitor.GridWrapper gw = matchGridMonitor.waitForMatch();
            newGrid.set(gw);
            initGameGrid();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        startingOverlay.setVisible(true);
        winnerOverlay.setVisible(false);
        loadingScoresOverlay.setVisible(false);
        loadingScoresVisible = matchTimerDuration.isEqualTo(Duration.ZERO).and(isInTimeout.not());
        loadingScoresVisible.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                loadingScoresOverlay.setVisible(true);
            } else{
                loadingScoresOverlay.setVisible(false);
            }
        });
        initZeroScores();
        initTable();
        initNewMatchListener();
        initScoresListener();
        initWordRequestListener();
        initTimeoutListener();
        initTimerService();
        initTimerTimeoutService();
        Task<Boolean> endgame = gameEndListener();
        parallelExecutorService.execute(endgame);
        initTimerBindings();
        initHeaderLabels();
        bindLocalizedLabels();
        initWordsList();
        initInstructions();
        initPreCountDownService();
        initLeaveBehaviour();
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
        timeoutMonitor.signalReady();
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
        leavingHead.set(resBundle.getString("leaving_game_head"));
        leavingBody.set(resBundle.getString("leaving_game_body"));
    }

    public void setActiveRoom(ObservableLobby activeRoom) {
        this.activeRoom = activeRoom;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    @Override
    protected void scaleFontSize(double after) {
        super.scaleFontSize(after);
        scoresOverlay.setStyle("-fx-font-size: "+ currentFontSize.get() + "px;");
    }

    @FXML void leaveGame() {
        generateYNDialog((StackPane) root, root.getWidth()/3, leavingHead.get(), leavingBody.get(),
                event -> leaving.set(true), null).show();
    }

    @FXML void requestDefinitions() {

    }

    public void setMatchGridMonitor(MatchGridMonitor matchGridMonitor) {
        this.matchGridMonitor = matchGridMonitor;
    }

    public void setInterruptMonitor(InterruptMonitor interruptMonitor) {
        this.interruptMonitor = interruptMonitor;
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
        matchGrid = new GameGrid(newGrid.get().getGridFaces(), newGrid.get().getGridNumbers());
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
            tile.setSelectedWordsSet(this.requestedWords);
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

    private void initLeaveBehaviour() {
        leaving.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    Launcher.manager.leaveGame(activeRoom.getRoomId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HomeController homeController = new HomeController();
                try {
                    Parent parent = requestParent(ControllerType.HOME_VIEW, homeController);
                    sceneTransitionAnimation(parent, SlideDirection.TO_BOTTOM).play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //++ Initialize services ++//
    //Service for the match timer
    private void initTimerService() {
        ScheduledService<Duration> service = new ScheduledService<>() {
            private Duration duration = activeRoom.getRuleset().getTimeToMatch();

            @Override
            protected Task<Duration> createTask() {
                Task<Duration> task = new Task<>() {
                    @Override
                    protected Duration call() {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            Duration decrement = duration.minusSeconds(1);
                            updateValue(decrement);
                            return decrement;
                        }
                        return null;
                    }
                };
                return task;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getLastValue() != null) {
                    duration = getLastValue();
                }
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (getLastValue() != null) {
                    duration = getLastValue();
                }
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                duration = activeRoom.getRuleset().getTimeToMatch();
                return true;
            }
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(backgroundExecutorService);
        timerCountDownService = service;
        timerCountDownService.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                matchTimerDuration.set(newValue);
            }
        });
    }

    //Service for the timeout timer
    private void initTimerTimeoutService() {
        ScheduledService<Duration> service = new ScheduledService<>() {
            private Duration duration = activeRoom.getRuleset().getTimeToWaitFromMatchToMatch();

            @Override
            protected Task<Duration> createTask() {
                Task<Duration> task = new Task<>() {
                    @Override
                    protected Duration call() {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            Duration decrement = duration.minusSeconds(1);
                            updateValue(decrement);
                            return decrement;
                        }
                        return null;
                    }
                };
                return task;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getLastValue() != null) {
                    duration = getLastValue();
                }
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (getLastValue() != null) {
                    duration = getLastValue();
                }
                if (getLastValue().equals(Duration.ZERO)) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                duration = activeRoom.getRuleset().getTimeToWaitFromMatchToMatch();
                return true;
            }
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(backgroundExecutorService);
        timerTimeout = service;
        timerTimeout.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                timeoutTimerDuration.set(newValue);
        });
    }

    //Service for the initial countdown (before match)
    private void initPreCountDownService() {
        ScheduledService<Integer> service = new ScheduledService<>() {
            private int cycleCount = 5;

            @Override
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    protected Integer call() {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            updateValue(cycleCount);
                            return cycleCount;
                        }
                        return null;
                    }
                };
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                cycleCount--;
                if (cycleCount == -1) {
                    this.cancel();
                }
            }

            @Override
            public boolean cancel() {
                super.cancel();
                cycleCount = 5;
                return true;
            }
        };
        service.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                preCountDownCycle.set(newValue);
            }
        });
        preCountDownCycle.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                startingOverlay.setVisible(false);
                timerCountDownService.restart();
            }
            String txt = switch (newValue.intValue()) {
                case 4 -> "3";
                case 3 -> "2";
                case 2 -> "1";
                case 1, 0 -> "Go!";
                default -> "";
            };
            startingCountDown.setText(txt);
        });
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(1));
        service.setExecutor(backgroundExecutorService);
        startingCountDownService = service;
    }

    private Task<Boolean> listenForInterruptTask() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                    interruptMonitor.isInterrupted();
                    updateValue(true);
                    return true;
                }
                return false;
            }
        };
        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                interrupted.set(newValue);
            }
        });
        interrupted.addListener((observable, oldValue, newValue) -> {
            if (newValue & !leaving.get()) {
                ScheduledService<Duration> load = leavingGameWaitTask();
                load.lastValueProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (newValue1.equals(Duration.ZERO)) {
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
                cancelAllServices();
                load.start();
                notification(gameInterrBody.get(), javafx.util.Duration.seconds(5));
            }
        });
        return task;
    }

    private ScheduledService<Duration> leavingGameWaitTask() {
        ScheduledService<Duration> service5sec = new ScheduledService<>() {
            private Duration notifDuration = Duration.ofSeconds(5);

            @Override
            protected Task<Duration> createTask() {
                return new Task<>() {
                    @Override
                    protected Duration call() {
                        Duration decrement = notifDuration.minusSeconds(1);
                        updateValue(decrement);
                        return decrement;
                    }
                };
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getLastValue() != null) {
                    notifDuration = getLastValue();
                }
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
        service5sec.setExecutor(parallelExecutorService);
        return service5sec;
    }

    private void initNewMatchListener() {
        ScheduledService<MatchGridMonitor.GridWrapper> service = new ScheduledService<>() {
            @Override
            protected Task<MatchGridMonitor.GridWrapper> createTask() {
                return new Task<>() {
                    @Override
                    protected MatchGridMonitor.GridWrapper call() throws InterruptedException {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            MatchGridMonitor.GridWrapper gw = matchGridMonitor.waitForMatch();
                            updateValue(gw);
                            return gw;
                        }
                        return null;
                    }
                };
            }
        };
        service.setRestartOnFailure(false);
        service.setExecutor(parallelExecutorService);
        newMatchListener = service;
        newMatchListener.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newGrid.set(newValue);
            }
        });
        newGrid.addListener((observable, oldValue, newValue) -> {
            matchGrid.resetGrid(newValue.getGridFaces(), newValue.getGridNumbers());
            matchNumber.set(matchNumber.get() + 1);
            wordFoundList.clear();
            matchGrid.clearSelection();
            matchTimerDuration.set(activeRoom.getRuleset().getTimeToMatch());
            isInTimeout.set(false);
        });
        newMatchListener.start();
    }

    private void initScoresListener() {
        ScheduledService<GameScore> service = new ScheduledService<>() {
            @Override
            protected Task<GameScore> createTask() {
                return new Task<>() {
                    @Override
                    protected GameScore call() throws Exception {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            GameScore gs = gameScoresMonitor.awaitGameScore();
                            updateValue(gs);
                            return gs;
                        }
                        return null;
                    }
                };
            }
        };
        service.setExecutor(parallelExecutorService);
        scoresListener = service;
        scoresListener.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastGameScore.set(newValue);
            }
        });
        lastGameScore.addListener((observable, oldValue, newValue) -> {
            newValue.getScores().entrySet().stream()
                    .forEach(entry -> {
                        lastMatchScores.get(entry.getKey()).set(entry.getValue()[0]);
                        gameScores.entrySet().stream()
                                .filter(e -> e.getKey().get().equals(entry.getKey()))
                                .forEach(e -> e.getValue().set(entry.getValue()[1]));
                    });
            Integer score = newValue.getScores().get(Launcher.manager.getProfile().getPlayerID())[1];
            playerScore.set(score);
            newValue.getMatchWords().entrySet().stream()
                    .forEach(entry -> {
                        proposedMatchWords.get(entry.getKey()).clear();
                        proposedMatchWords.get(entry.getKey()).addAll(Arrays.asList(entry.getValue()));
                    });
            winnerName.set(newValue.getWinner());
        });
        scoresListener.start();
    }

    private void initWordRequestListener() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            sendWordsMonitor.wordsRequested();
                            if (!Thread.currentThread().isInterrupted()) {
                                ArrayList<String> words = new ArrayList<>();
                                words.addAll(wordFoundList.get());
                                sendWordsMonitor.offerWords(words);
                            }
                        }
                        return null;
                    }
                };
            }
        };
        service.setExecutor(parallelExecutorService);
        wordRequestListener = service;
        wordRequestListener.start();
    }

    private void initTimeoutListener() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected void succeeded() {
                super.succeeded();
            }

            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                            timeoutMonitor.isInTimeOut();
                            if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                                Platform.runLater(() -> isInTimeout.set(true));
                            }
                        }
                        return null;
                    }
                };


            }
        };
        service.setExecutor(parallelExecutorService);
        timeoutListener = service;
        isInTimeout.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                readyBtn.setDisable(false);
                requestBtn.setDisable(false);
                timeoutTimerDuration.set(activeRoom.getRuleset().getTimeToWaitFromMatchToMatch());
                preCountDownCycle.set(5);
                scoresOverlay.setVisible(true);
                timerTimeout.restart();
            } else {
                preCountDownCycle.set(5);
                scoresOverlay.setVisible(false);
                startingOverlay.setVisible(true);
                if (timerTimeout.isRunning()) {
                    timerTimeout.cancel();
                }
                startingCountDownService.restart();
            }
        });
        timeoutListener.start();
    }

    private Task<Boolean> gameEndListener() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                    endGameMonitor.isEnded();
                    updateValue(true);
                    return true;
                }
                return null;
            }
        };
        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                gameEnded.set(newValue);
            }
        });
        gameEnded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ScheduledService<Duration> t = leavingGameWaitTask();
                t.lastValueProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (newValue1.equals(Duration.ZERO)) {
                        HomeController home = new HomeController();
                        home.setActiveLobby(activeRoom);
                        try {
                            Parent parent = requestParent(ControllerType.HOME_VIEW, home);
                            sceneTransitionAnimation(parent, SlideDirection.TO_BOTTOM).play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                winnerOverlay.setVisible(true);
                t.start();
            }
        });
        return task;
    }

    private void cancelAllServices() {
        timerTimeout.cancel();
        timerCountDownService.cancel();
        startingCountDownService.cancel();
        newMatchListener.cancel();
        scoresListener.cancel();
        wordRequestListener.cancel();
        timeoutListener.cancel();
    }
}
