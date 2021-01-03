package uninsubria.client.monitors;

/**
 * Monitor object used for signaling the GUI a new game is starting.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class GameStartMonitor {
    /*---Fields---*/
    private long delay;
    private boolean starting = false;

    /*---Constructors---*/
    /**
     * Instantiates a new GameStartMonitor.
     */
    public GameStartMonitor() {
    }

    /*---Methods---*/
    /**
     * Waits until the game is starting, returning the delay (millis) for the scheduling of the game.
     *
     * @return the delay
     * @throws InterruptedException the interrupted exception
     */
    public synchronized long isStarting() throws InterruptedException {
        while (!starting) {
            wait();
        }
        return delay;
    }

    /**
     * Signals a new game is starting by passing the delay and notifies other threads.
     *
     * @param delay the delay
     */
    public synchronized void signalStart(long delay) {
        this.delay = delay;
        this.starting = true;
        notify();
    }
}
