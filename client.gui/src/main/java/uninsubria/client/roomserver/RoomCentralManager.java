package uninsubria.client.roomserver;

import uninsubria.client.guicontrollers.HomeController;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.Socket;

/**
 * Manager for communication with the room.
 *
 * @author Giulia Pais
 * @version 0.9.0
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

    public static void startRoomServer(HomeController controller) throws IOException {
        if (getInstance().roomServer == null) {
            getInstance().roomServer = new RoomServer(controller);
        }
    }

    public static void stopRoomServer() {
        if (getInstance().roomServer != null) {
            getInstance().roomServer.interrupt();
            try {
                new Socket("localhost", CommHolder.ROOM_PORT);
                getInstance().roomServer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopRoom() {
        getInstance().roomServer.stopRoom();
    }
}
