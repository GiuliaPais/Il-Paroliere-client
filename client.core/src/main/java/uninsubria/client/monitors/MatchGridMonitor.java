package uninsubria.client.monitors;

/**
 * Monitor for synchronizing on a notification of a new match.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class MatchGridMonitor {
    /*---Fields---*/
    private GridWrapper grid;
    private boolean newMatchAvailable = false;
    private boolean newMatchAcknowledged = false;

    /**
     * A wrapper for the game grid.
     */
    public class GridWrapper {
        private String[] gridFaces;
        private Integer[] gridNumbers;

        /**
         * Instantiates a new Grid wrapper.
         *
         * @param gridFaces   the grid faces
         * @param gridNumbers the grid numbers
         */
        public GridWrapper(String[] gridFaces, Integer[] gridNumbers) {
            this.gridFaces = gridFaces;
            this.gridNumbers = gridNumbers;
        }

        /**
         * Get grid faces.
         *
         * @return the grid faces
         */
        public String[] getGridFaces() {
            return gridFaces;
        }

        /**
         * Get grid numbers
         *
         * @return the numbers
         */
        public Integer[] getGridNumbers() {
            return gridNumbers;
        }
    }

    /*---Constructors---*/
    /**
     * Instantiates a new MatchGridMonitor.
     */
    public MatchGridMonitor() {
    }

    /*---Methods---*/
    /**
     * Waits on the monitor until a new match is available, then consumes the new match by setting newMatchAvailable
     * to false. Also signals other threads that the grid has been received by setting newMatchAcknowledged to true
     * and notifying.
     *
     * @return the grid wrapper
     * @throws InterruptedException the interrupted exception
     */
    public synchronized GridWrapper waitForMatch() throws InterruptedException {
        while (!newMatchAvailable) {
            wait();
        }
        newMatchAcknowledged = true;
        newMatchAvailable = false;
        notify();
        return grid;
    }

    /**
     * Signal match available.
     *
     * @param faces   the faces
     * @param numbers the numbers
     */
    public synchronized void signalMatchAvailable(String[] faces, Integer[] numbers) {
        grid = new GridWrapper(faces, numbers);
        newMatchAvailable = true;
        notify();
    }

    /**
     * Waits for the new match to be acknowledged.
     *
     * @throws InterruptedException the interrupted exception
     */
    public synchronized void awaitAck() throws InterruptedException {
        while (!newMatchAcknowledged) {
            wait();
        }
        newMatchAcknowledged = false;
    }
}
