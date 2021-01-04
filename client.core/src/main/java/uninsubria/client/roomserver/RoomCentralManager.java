package uninsubria.client.roomserver;

import uninsubria.client.monitors.*;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.Socket;

/**
 * Manager for communication with the room.
 *
 * @author Giulia Pais
 * @version 0.9.5
 */
public class RoomCentralManager {
    /*---Fields---*/
    private static RoomCentralManager instance;
    private RoomServer roomServer;

    /*---Constructors---*/
    private RoomCentralManager() {
    }

    /*---Methods---*/
    public static RoomCentralManager getInstance() {
        if (instance == null) {
            instance = new RoomCentralManager();
        }
        return instance;
    }

    public static void startRoomServer(GameStartMonitor gm, MatchGridMonitor mm, InterruptMonitor im) throws IOException {
        if (getInstance().roomServer == null) {
            getInstance().roomServer = new RoomServer(gm, mm, im);
        } else {
            getInstance().roomServer.setGameStartMonitor(gm);
            getInstance().roomServer.setMatchGridMonitor(mm);
            getInstance().roomServer.setInterruptMonitor(im);
        }
    }

    public static void stopRoomServer() {
        if (getInstance().roomServer != null) {
            getInstance().roomServer.interrupt();
            try {
                new Socket("localhost", CommHolder.ROOM_PORT);
                stopRoom();
                getInstance().roomServer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopRoom() {
        if (getInstance().roomServer != null)
            getInstance().roomServer.stopGame();
    }

    public static void setSendWordsMonitor(SendWordsMonitor sendWordsMonitor) {
        if (getInstance().roomServer != null)
            getInstance().roomServer.setSendWordsMonitor(sendWordsMonitor);
    }

    public static void setTimeoutMonitor(TimeoutMonitor timeoutMonitor) {
        if (getInstance().roomServer != null)
            getInstance().roomServer.setTimeoutMonitor(timeoutMonitor);
    }

    public static void setGameScoresMonitor(GameScoresMonitor gameScoresMonitor) {
        if (getInstance().roomServer != null)
            getInstance().roomServer.setGameScoresMonitor(gameScoresMonitor);
    }

    public static void setEndGameMonitor(EndGameMonitor endGameMonitor) {
        if (getInstance().roomServer != null)
            getInstance().roomServer.setEndGameMonitor(endGameMonitor);
    }
}
