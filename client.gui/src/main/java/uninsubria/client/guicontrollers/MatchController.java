package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.client.customcontrols.GameGrid;
import uninsubria.client.gui.ObservableLobby;

import java.time.Duration;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Controller for the match view.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class MatchController extends AbstractMainController {
    /*---Fields---*/
    @FXML StackPane gameGrid, startingOverlay;
    @FXML AnchorPane header;
    @FXML Label timerSeconds, timerMinutes, matchNumLbl, currScoreLbl, currScoreValueLbl, foundWord, wordListLbl, startingCountDown;
    @FXML VBox instructionSidePanel, wordsSidePanel;
    @FXML JFXButton leaveGameBtn, insertBtn, clearBtn;
    @FXML JFXListView<String> wordsListView;
    @FXML TextFlow instructions;

    private ObservableLobby activeRoom;
    private String[] gridFaces;
    private Integer[] gridNumb;
    private GameGrid matchGrid;
    private ObjectProperty<Duration> timerDuration;
    private IntegerProperty minutesPart, secondsPart, playerScore, matchNumber;
    private ScheduledService<Duration> timerCountDownService;
    private StringProperty currScoreTxt, wordListTxt, leaveGameTxt, insertTxt, clearTxt, instr1, instr2, instr3, instr4,
                            loadingScoresTxt;
    private ListProperty<String> wordFoundList;
    private ScheduledService<String> startingCountDownService;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private LoadingAnimationOverlay loadingScores;

    /*---Constructors---*/
    public MatchController() {
        this.timerDuration = new SimpleObjectProperty<>();
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
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        startingOverlay.setVisible(true);
        loadingScores = new LoadingAnimationOverlay(root, loadingScoresTxt.get());
        initGameGrid();
        initTimerService();
        initTimerBindings();
        initHeaderLabels();
        bindLocalizedLabels();
        initWordsList();
        initInstructions();
        initPreCountDownService();
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

    public void setGridFaces(String[] gridFaces) {
        this.gridFaces = gridFaces;
    }

    public void setGridNumb(Integer[] gridNumb) {
        this.gridNumb = gridNumb;
    }

    /*----------- Private methods for initialization and scaling -----------*/
    private void initTimerBindings() {
        timerDuration.set(activeRoom.getRuleset().getTimeToMatch());
        minutesPart.set(timerDuration.getValue().toMinutesPart());
        secondsPart.set(timerDuration.getValue().toSecondsPart());
        timerMinutes.setText(minutesPart.asString("%02d").getValue());
        timerSeconds.setText(secondsPart.asString("%02d").getValue());
        timerCountDownService.lastValueProperty().addListener((observable, oldValue, newValue) -> {
            timerDuration.set(newValue);
        });
        timerDuration.addListener((observable, oldValue, newValue) -> {
            secondsPart.set(newValue.toSecondsPart());
            minutesPart.set(newValue.toMinutesPart());
        });
        timerSeconds.textProperty().bind(secondsPart.asString("%02d"));
        timerMinutes.textProperty().bind(minutesPart.asString("%02d"));
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

    @Override
    protected void rescaleAll(double after) {
        super.rescaleAll(after);
        rescaleHeader(after);
        rescaleGrid(after);
        rescaleSidePanels(after);
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
                        Duration decrement = timerDuration.get().minusSeconds(1);
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
        service.setPeriod(javafx.util.Duration.seconds(1));
        service.setDelay(javafx.util.Duration.seconds(0));
        service.setExecutor(scheduledExecutorService);
        timerCountDownService = service;
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
