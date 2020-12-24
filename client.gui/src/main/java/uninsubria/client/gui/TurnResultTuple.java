package uninsubria.client.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uninsubria.utils.business.TurnsResult;

/**
 * Observable object representing a turn statistics table row.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class TurnResultTuple {
    /*---Fields---*/
    private IntegerProperty category;
    private IntegerProperty minTurns;
    private IntegerProperty maxTurns;
    private DoubleProperty avgTurns;

    /*---Constructors---*/
    private TurnResultTuple() {
        this.category = new SimpleIntegerProperty();
        this.minTurns = new SimpleIntegerProperty();
        this.maxTurns = new SimpleIntegerProperty();
        this.avgTurns = new SimpleDoubleProperty();
    }
    /*---Methods---*/
    /**
     * Converts a general TurnsResult object into an observable implementation.
     *
     * @param turnsResult the turns result
     * @return the turn result tuple
     */
    public static TurnResultTuple asTurnResultTuple(TurnsResult turnsResult) {
        TurnResultTuple tuple = new TurnResultTuple();
        tuple.category.setValue(turnsResult.getCategory());
        tuple.minTurns.setValue(turnsResult.getMinTurns());
        tuple.maxTurns.setValue(turnsResult.getMaxTurns());
        tuple.avgTurns.setValue(turnsResult.getAvgTurns());
        return tuple;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * Category property integer property.
     *
     * @return the integer property
     */
    public IntegerProperty categoryProperty() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Gets min turns.
     *
     * @return the min turns
     */
    public int getMinTurns() {
        return minTurns.get();
    }

    /**
     * Min turns property integer property.
     *
     * @return the integer property
     */
    public IntegerProperty minTurnsProperty() {
        return minTurns;
    }

    /**
     * Sets min turns.
     *
     * @param minTurns the min turns
     */
    public void setMinTurns(int minTurns) {
        this.minTurns.set(minTurns);
    }

    /**
     * Gets max turns.
     *
     * @return the max turns
     */
    public int getMaxTurns() {
        return maxTurns.get();
    }

    /**
     * Max turns property integer property.
     *
     * @return the integer property
     */
    public IntegerProperty maxTurnsProperty() {
        return maxTurns;
    }

    /**
     * Sets max turns.
     *
     * @param maxTurns the max turns
     */
    public void setMaxTurns(int maxTurns) {
        this.maxTurns.set(maxTurns);
    }

    /**
     * Gets avg turns.
     *
     * @return the avg turns
     */
    public double getAvgTurns() {
        return avgTurns.get();
    }

    /**
     * Avg turns property double property.
     *
     * @return the double property
     */
    public DoubleProperty avgTurnsProperty() {
        return avgTurns;
    }

    /**
     * Sets avg turns.
     *
     * @param avgTurns the avg turns
     */
    public void setAvgTurns(double avgTurns) {
        this.avgTurns.set(avgTurns);
    }
}
