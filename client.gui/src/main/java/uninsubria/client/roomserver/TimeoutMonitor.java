package uninsubria.client.roomserver;

import java.time.Duration;

/**
 * Simple monitor for signaling the player ready state during timeout.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class TimeoutMonitor {
    /*---Fields---*/
    private boolean ready = false;

    /*---Constructors---*/
    public TimeoutMonitor() {
    }

    /*---Methods---*/
    public synchronized boolean isReady(Duration timeToWait) throws InterruptedException {
        if (!ready) {
            wait(timeToWait.toMillis());
        }
        return ready;
    }

    public synchronized void signalReady() {
        ready = true;
        notify();
    }

    public synchronized void reset() {
        ready = false;
    }
}
