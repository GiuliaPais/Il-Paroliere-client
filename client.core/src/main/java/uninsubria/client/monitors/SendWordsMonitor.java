package uninsubria.client.monitors;

import java.util.ArrayList;

/**
 * Monitor for synchronizing on word request at the end of the match.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class SendWordsMonitor {
    /*---Fields---*/
    private boolean wordRequest = false;
    private boolean wordsAvailable = false;
    private ArrayList<String> words;

    /*---Constructors---*/
    public SendWordsMonitor() {
    }

    /*---Methods---*/
    public synchronized boolean wordsRequested() throws InterruptedException {
        while (!wordRequest) {
            wait();
        }
        wordRequest = false;
        return true;
    }

    public synchronized ArrayList<String> getProposedWords() throws InterruptedException {
        while (!wordsAvailable) {
            wait();
        }
        ArrayList<String> wordList = words;
        words = null;
        wordsAvailable = false;
        return wordList;
    }

    public synchronized void offerWords(ArrayList<String> words) {
        this.words = words;
        wordsAvailable = true;
        notify();
    }

    public synchronized void sendRequest() {
        this.wordRequest = true;
        notify();
    }
}
