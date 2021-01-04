package uninsubria.client.monitors;

/**
 * Monitor for signaling the game is ending.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class EndGameMonitor {
    /*---Fields---*/
    private boolean gameHasEnded = false;

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
        return true;
    }
}
