package uninsubria.client.roomserver;

import uninsubria.client.guicontrollers.HomeController;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Client-side server responsible for accepting connection requests
 * from rooms.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class RoomServer extends Thread {
    /*---Fields---*/
    private ServerSocket serverSocket;
    private RoomSkeleton activeRoom;
    private HomeController controller;

    /*---Constructors---*/
    public RoomServer(HomeController controller) throws IOException {
        serverSocket = new ServerSocket(CommHolder.ROOM_PORT);
        this.controller = controller;
        start();
    }

    /*---Methods---*/
    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                Socket room = serverSocket.accept();
                if (activeRoom != null) {
                    activeRoom.terminate();
                }
                activeRoom = new RoomSkeleton(room, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInRoom() {
        if (activeRoom == null) {
            return false;
        }
        return true;
    }

    public void stopRoom() {
        activeRoom = null;
    }
}
