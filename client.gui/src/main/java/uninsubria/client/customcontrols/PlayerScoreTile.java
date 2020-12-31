package uninsubria.client.customcontrols;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.CheckListView;
import uninsubria.client.gui.Launcher;
import uninsubria.client.guicontrollers.ControllerType;
import uninsubria.utils.business.Word;
import uninsubria.utils.languages.Language;
import uninsubria.utils.languages.LanguageManager;

import java.io.IOException;
import java.net.URL;

/**
 * Custom object that represents a tile for displaying the score of a player.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class PlayerScoreTile extends StackPane {
    /*---Fields---*/
    private final URL fxmlFile = getClass().getResource("/fxml/" + ControllerType.SCORE_TILE.getFile());

    @FXML Label playerID, matchScoreTitle, gameScoreTitle, matchScoreValue, gameScoreValue;
    @FXML CheckListView<Word> wordListView;

    private ListProperty<Word> wordList;
    private IntegerProperty matchScore, gameScore;
    private StringProperty player;
    private DoubleProperty fontSize;

    /*---Constructors---*/
    public PlayerScoreTile() {
        wordList = new SimpleListProperty<>(FXCollections.observableArrayList());
        player = new SimpleStringProperty("player");
        matchScore = new SimpleIntegerProperty(0);
        gameScore = new SimpleIntegerProperty(0);
        fontSize = new SimpleDoubleProperty(16.0);
    }

    /*---Methods---*/
    public void initialize() {
        loadFxml();
        playerID.setText(player.get());
        playerID.textProperty().bind(player);
        matchScoreValue.textProperty().bind(matchScore.asString());
        gameScoreValue.textProperty().bind(gameScore.asString());
        fontSize.addListener((observable, oldValue, newValue) -> {
            playerID.setStyle("-fx-font-size: " + (newValue.doubleValue() + 3) + ";");
            matchScoreValue.setStyle("-fx-font-size: " + newValue.doubleValue()+ ";");
            gameScoreValue.setStyle("-fx-font-size: " + newValue.doubleValue()+ ";");
            matchScoreTitle.setStyle("-fx-font-size: " + newValue.doubleValue()+ ";");
            gameScoreTitle.setStyle("-fx-font-size: " + newValue.doubleValue()+ ";");
            wordListView.setStyle("-fx-font-size: " + newValue.doubleValue()+ ";");
        });
        wordListView.itemsProperty().bind(wordList);
    }

    public ObservableList<Word> getWordList() {
        return wordList.get();
    }

    public ListProperty<Word> wordListProperty() {
        return wordList;
    }

    public void setWordList(ObservableList<Word> wordList) {
        this.wordList.set(wordList);
    }

    public int getMatchScore() {
        return matchScore.get();
    }

    public IntegerProperty matchScoreProperty() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore.set(matchScore);
    }

    public int getGameScore() {
        return gameScore.get();
    }

    public IntegerProperty gameScoreProperty() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore.set(gameScore);
    }

    public String getPlayer() {
        return player.get();
    }

    public StringProperty playerProperty() {
        return player;
    }

    public void setPlayer(String player) {
        this.player.set(player);
    }

    public double getFontSize() {
        return fontSize.get();
    }

    public DoubleProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize.set(fontSize);
    }

    public CheckListView<Word> getWordListView() {
        return wordListView;
    }

    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(fxmlFile, Launcher.contrManager.getBundleValue());
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
