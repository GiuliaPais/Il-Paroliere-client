package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXListView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import uninsubria.client.gui.Controller;
import uninsubria.utils.business.Word;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * Custom object that represents a tile for displaying the score of a player.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public class PlayerScoreTile implements Controller {
    /*---Fields---*/
    @FXML Label playerID, matchScoreTitle, gameScoreTitle, matchScoreValue, gameScoreValue;
    @FXML JFXListView<Word> wordListView;

    private ListProperty<Word> wordList;
    private IntegerProperty matchScore, gameScore;
    private StringProperty player;
    private DoubleProperty fontSize;
    private Set<String> selectedWordsSet;

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
        playerID.setText(player.get());
        playerID.setStyle("-fx-font-size: " + (fontSize.doubleValue() + 5) + ";");
        playerID.textProperty().bind(player);
        matchScoreValue.textProperty().bind(matchScore.asString());
        gameScoreValue.textProperty().bind(gameScore.asString());
        fontSize.addListener((observable, oldValue, newValue) -> {
            playerID.setStyle("-fx-font-size: " + (newValue.doubleValue() + 5) + ";");
        });
        wordListView.itemsProperty().bind(wordList);
        wordListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        wordListView.setCellFactory(param -> {
            ScoreWordsListCell cell = new ScoreWordsListCell();
            cell.fontSizeProperty().bind(fontSize);
            return cell;
        });
        wordListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Word>) c -> {
            while (c.next()) {

            }
        });
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

    public JFXListView<Word> getWordListView() {
        return wordListView;
    }

    public void setSelectedWordsSet(Set<String> selectedWordsSet) {
        this.selectedWordsSet = selectedWordsSet;
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {

    }
}
