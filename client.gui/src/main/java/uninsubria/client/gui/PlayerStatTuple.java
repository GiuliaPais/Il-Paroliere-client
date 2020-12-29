package uninsubria.client.gui;

import javafx.beans.property.*;
import uninsubria.utils.business.PlayerStatResult;

/**
 * Observable object representing a player statistics table row.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class PlayerStatTuple {
    /*---Fields---*/
    private StringProperty playerID;
    private DoubleProperty score;
    private IntegerProperty intScore;

    /*---Constructors---*/
    private PlayerStatTuple() {
        this.playerID = new SimpleStringProperty();
        this.score = new SimpleDoubleProperty();
        this.intScore = new SimpleIntegerProperty();
        this.intScore.bind(scoreProperty());
    }

    /*---Methods---*/
    /**
     * Converts a general PlayerStatResult object into an observable implementation.
     *
     * @param result the result
     * @return the player stat tuple
     */
    public static PlayerStatTuple asPlayerStatsTuple(PlayerStatResult result) {
        PlayerStatTuple tuple = new PlayerStatTuple();
        tuple.setPlayerID(result.getPlayerID());
        tuple.setScore(result.getScore());
        return tuple;
    }

    public String getPlayerID() {
        return playerID.get();
    }

    public StringProperty playerIDProperty() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID.set(playerID);
    }

    public double getScore() {
        return score.get();
    }

    public DoubleProperty scoreProperty() {
        return score;
    }

    public void setScore(double score) {
        this.score.set(score);
    }

    public int getIntScore() {
        return intScore.get();
    }

    public IntegerProperty intScoreProperty() {
        return intScore;
    }

    public void setIntScore(int intScore) {
        this.intScore.set(intScore);
    }
}
