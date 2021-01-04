package uninsubria.client.monitors;

import uninsubria.utils.business.GameScore;

/**
 * Monitor for synchronizing on the reception of game scores from the server.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class GameScoresMonitor {
    /*---Fields---*/
    private GameScore gameScore;

    /*---Constructors---*/
    /**
     * Instantiates a new GameScoresMonitor.
     */
    public GameScoresMonitor() {
    }

    /*---Methods---*/
    /**
     * Publishes the scores and notifies the threads waiting for them.
     *
     * @param gameScore the game score
     */
    public synchronized void publishScores(GameScore gameScore) {
        this.gameScore = gameScore;
        notify();
    }

    /**
     * Waits until game scores are published, then consumes them by setting them again to null.
     *
     * @return the game score
     * @throws InterruptedException the interrupted exception
     */
    public synchronized GameScore awaitGameScore() throws InterruptedException {
        while (gameScore == null) {
            wait();
        }
        GameScore gs = gameScore;
        gameScore = null;
        return gs;
    }
}
