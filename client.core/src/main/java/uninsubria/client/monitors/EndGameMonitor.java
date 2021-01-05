package uninsubria.client.monitors;

import uninsubria.utils.business.WordRequest;

import java.util.HashSet;

/**
 * Monitor for signaling the game is ending.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class EndGameMonitor {
    /*---Fields---*/
    private boolean gameHasEnded = false;
    private HashSet<WordRequest> wordRequested;

    /*---Constructors---*/

    /**
     * Instantiates a new EndGameMonitor.
     */
    public EndGameMonitor() {
    }

    /*---Methods---*/

    /**
     * Sets the game to ended and notifies other threads.
     */
    public synchronized void setGameEnded() {
        gameHasEnded = true;
        notify();
    }

    /**
     * Waits until game has ended.
     *
     * @return true when the thread has awaken
     * @throws InterruptedException the interrupted exception
     */
    public synchronized boolean isEnded() throws InterruptedException {
        while (!gameHasEnded) {
            wait();
        }
        gameHasEnded = false;
        return true;
    }

    /**
     * Waits for the set of requested words
     *
     * @return the requested words set
     * @throws InterruptedException the interrupted exception
     */
    public synchronized HashSet<WordRequest> waitRequests() throws InterruptedException {
       while (wordRequested == null) {
           wait();
       }
       HashSet<WordRequest> r = wordRequested;
       wordRequested = null;
       return r;
    }

    /**
     * Notifies the threads waiting for words reqeusts.
     *
     * @param wordRequested the word requested
     */
    public synchronized void offerRequests(HashSet<WordRequest> wordRequested) {
        this.wordRequested = wordRequested;
        notify();
    }
}
