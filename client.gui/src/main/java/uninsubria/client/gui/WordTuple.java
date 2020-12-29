package uninsubria.client.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Observable object representing a word statistics table row.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class WordTuple {
    /*---Fields---*/
    private StringProperty word;
    private IntegerProperty occurr;

    /*---Constructors---*/
    public WordTuple(String word, Integer occurr) {
        this.word = new SimpleStringProperty(word);
        this.occurr = new SimpleIntegerProperty(occurr);
    }

    /*---Methods---*/
    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public int getOccurr() {
        return occurr.get();
    }

    public IntegerProperty occurrProperty() {
        return occurr;
    }

    public void setOccurr(int occurr) {
        this.occurr.set(occurr);
    }
}
