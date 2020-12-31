package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.client.customcontrols.GameGrid;
import uninsubria.client.customcontrols.PlayerScoreTile;
import uninsubria.client.gui.ObservableLobby;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.Word;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Controller for the match view.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public class MatchController extends AbstractMainController {
    /*---Fields---*/
    @FXML StackPane gameGrid, startingOverlay, scoresOverlay;
    @FXML AnchorPane header;
    @FXML Label timerSeconds, timerMinutes, matchNumLbl, currScoreLbl, currScoreValueLbl, foundWord, wordListLbl, startingCountDown,
            scoresSecLbl, scoresMinLbl, scoresTitle;
    @FXML VBox instructionSidePanel, wordsSidePanel;
    @FXML JFXButton leaveGameBtn, insertBtn, clearBtn, readyBtn, requestBtn;
    @FXML JFXListView<String> wordsListView;
    @FXML TextFlow instructions;
    @FXML TilePane tilePane;
    @FXML TableView<?> playersTable;

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
                            loadingScoresTxt;
    private ListProperty<String> wordFoundList;
    private ScheduledService<String> startingCountDownService;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private LoadingAnimationOverlay loadingScores;

    //++ Internals for keeping scores ++//
    private HashMap<String, IntegerProperty> gameScores;
    private HashMap<String, IntegerProperty> lastMatchScores;
    private HashMap<String, ListProperty<Word>> proposedMatchWords;
    private StringProperty winnerName;

    //++ For timeout synch ++//
    private Boolean monitor;

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
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        startingOverlay.setVisible(true);
        loadingScores = new LoadingAnimationOverlay(root, loadingScoresTxt.get());
        initZeroScores();
        initGameGrid();
        initTimerService();
        initTimerBindings();
        initHeaderLabels();
        bindLocalizedLabels();
        initWordsList();
        initInstructions();
        initPreCountDownService();
        initScoresOverlay();
        initTimerTimeoutService();
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
        synchronized (monitor) {
            notify();
        }
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
    }

    public void setActiveRoom(ObservableLobby activeRoom) {
        this.activeRoom = activeRoom;
    }

    public void setMatchGrid(String[] gridFaces, Integer[] gridNumb) {
        matchGrid.resetGrid(gridFaces, gridNumb);
        newMatchAvailable = true;
        matchTimerDuration.set(activeRoom.getRuleset().getTimeToMatch());
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
                    gameScores.get(entry.getKey()).set(entry.getValue()[1]);
                });
        gameScore.getMatchWords().entrySet().stream()
                .forEach(entry -> {
                    proposedMatchWords.get(entry.getKey()).clear();
                    proposedMatchWords.get(entry.getKey()).addAll(Arrays.asList(entry.getValue()));
                });
        winnerName.set(gameScore.getWinner());
    }

    public void setTimerMatch(Boolean monitor) {
        this.monitor = monitor;
        timerTimeout.start();
    }

    public Duration getTimeoutDuration() {
        return activeRoom.getRuleset().getTimeToWaitFromMatchToMatch();
    }

    /*----------- Private methods for initialization and scaling -----------*/
    private void initTimerBindings() {
        matchTimerDuration.set(activeRoom.getRuleset().getTimeToMatch());
        minutesPart.set(matchTimerDuration.getValue().toMinutesPart());
        secondsPart.set(matchTimerDuration.getValue().toSecondsPart());
        timerMinutes.setText(minutesPart.asString("%02d").getValue());
        timerSeconds.setText(secondsPart.asString("%02d").getValue());
        timerCountDownService.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            matchTimerDuration.set(newValue);
        });
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
        HashMap<String, IntegerProperty> zeroGameScores = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroGameScores.put(p, new SimpleIntegerProperty(0)));
        gameScores = zeroGameScores;
        HashMap<String, IntegerProperty> zeroMatchScores = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroMatchScores.put(p, new SimpleIntegerProperty(0)));
        lastMatchScores = zeroMatchScores;
        HashMap<String, ListProperty<Word>> zeroWords = new HashMap<>();
        participants.stream()
                .forEach(p -> zeroWords.put(p, new SimpleListProperty<>(FXCollections.observableArrayList())));
        proposedMatchWords = zeroWords;
    }

    private void initScoresOverlay() {
        for (String player :  participants) {
            PlayerScoreTile tile = new PlayerScoreTile();
            tile.setFontSize(currentFontSize.get());
            tile.setPlayer(player);
            tile.gameScoreProperty().bind(gameScores.get(player));
            tile.wordListProperty().bind(proposedMatchWords.get(player));
            tile.matchScoreProperty().bind(lastMatchScores.get(player));
            tilePane.getChildren().add(tile);
        }
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
    }

    private void bindLocalizedLabels() {
        currScoreLbl.textProperty().bind(currScoreTxt);
        wordListLbl.textProperty().bind(wordListTxt);
        leaveGameBtn.textProperty().bind(leaveGameTxt);
        insertBtn.textProperty().bind(insertTxt);
        clearBtn.textProperty().bind(clearTxt);
    }

    //++ Initialize services ++//
    private void initTimerService() {
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
        };
        service.setOnCancelled(event -> loadingScores.playAnimation());
        service.setOnScheduled(event -> {
            newMatchAvailable = false;
        });
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(scheduledExecutorService);
        timerCountDownService = service;
    }

    private void initTimerTimeoutService() {
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
        };
        service.setOnCancelled(event -> {
            if (newMatchAvailable == true) {
                scoresOverlay.setVisible(false);
                startingOverlay.setVisible(true);
                startingCountDownService.start();
            } else {
                System.out.println("Match not set yet");
            }
        });
        service.setOnScheduled(event -> {
            timeoutTimerDuration.set(activeRoom.getRuleset().getTimeToWaitFromMatchToMatch());
            loadingScores.stopAnimation();
            readyBtn.setDisable(false);
            requestBtn.setDisable(false);
            scoresOverlay.setVisible(true);
        });
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(scheduledExecutorService);
        timerTimeout = service;
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
        };
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(1));
        service.setOnCancelled(event -> {
            startingOverlay.setVisible(false);
            timerCountDownService.start();
        });
        service.setExecutor(scheduledExecutorService);
        service.lastValueProperty().addListener((observable, oldValue, newValue) -> startingCountDown.setText(newValue));
        startingCountDownService = service;
    }
}
