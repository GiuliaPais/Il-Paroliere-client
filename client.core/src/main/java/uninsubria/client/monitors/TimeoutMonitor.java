package uninsubria.client.monitors;

/**
 * Simple monitor for signaling the player ready state during timeout.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class TimeoutMonitor {
    /*---Fields---*/
    private boolean inTimeOut = false;
    private boolean ready = false;
    private long timeToWait;

    /*---Constructors---*/
    public TimeoutMonitor(long time) {
        timeToWait = time;
    }

    /*---Methods---*/
    public synchronized boolean isInTimeOut() throws InterruptedException {
        while (!inTimeOut) {
            wait();
        }
        return true;
    }

    public synchronized boolean isReady() throws InterruptedException {
        if (!ready) {
            wait(timeToWait);
        }
        return ready;
    }

    public synchronized void signalReady() {
        ready = true;
        notify();
        ready = false;
    }

    public synchronized void setTimeOut() {
        inTimeOut = true;
        notify();
    }
}
