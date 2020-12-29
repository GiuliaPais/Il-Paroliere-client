package uninsubria.client.gui;

import javafx.beans.property.*;
import uninsubria.utils.business.WordGameStatResult;

import java.util.UUID;

/**
 * Observable object representing a word game increment statistics table row.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class WGPTuple {
    /*---Fields---*/
    private StringProperty word;
    private IntegerProperty points;
    private ObjectProperty<UUID> game;

    /*---Constructors---*/
    private WGPTuple() {
        this.word = new SimpleStringProperty();
        this.points = new SimpleIntegerProperty();
        this.game = new SimpleObjectProperty<>();
    }

    /*---Methods---*/
    public static WGPTuple asWGPTuple(WordGameStatResult wgp) {
        WGPTuple tuple = new WGPTuple();
        tuple.setGame(wgp.getGame());
        tuple.setWord(wgp.getWord());
        tuple.setPoints(wgp.getPoints());
        return tuple;
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public UUID getGame() {
        return game.get();
    }

    public ObjectProperty<UUID> gameProperty() {
        return game;
    }

    public void setGame(UUID game) {
        this.game.set(game);
    }
}
