package uninsubria.client.monitors;

/**
 * Monitor for synchronizing on the game interruption by the server.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class InterruptMonitor {
    /*---Fields---*/
    private boolean interrupted = false;

    /*---Constructors---*/
    /**
     * Instantiates a new Interrupt monitor.
     */
    public InterruptMonitor() {
    }

    /*---Methods---*/
    /**
     * Waits for a notification of interruption by the server (and eventually resets it).
     *
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    public synchronized boolean isInterrupted() throws InterruptedException {
        while (!interrupted) {
            wait();
        }
        interrupted = false;
        return true;
    }

    /**
     * Signals the game has been interrupted and notifies other threads.
     */
    public synchronized void setInterrupted() {
        interrupted = true;
        notify();
    }
}
