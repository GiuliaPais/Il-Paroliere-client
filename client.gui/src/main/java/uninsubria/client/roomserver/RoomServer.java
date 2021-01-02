package uninsubria.client.roomserver;

import uninsubria.client.guicontrollers.HomeController;
import uninsubria.client.guicontrollers.MatchController;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Client-side server responsible for accepting connection requests
 * from rooms.
 *
 * @author Giulia Pais
 * @version 0.9.4
 */
public class RoomServer extends Thread {
    /*---Fields---*/
    private ServerSocket serverSocket;
    private RoomSkeleton activeRoom;
    private HomeController controller;

    /*---Constructors---*/
    /**
     * Instantiates a new Room server.
     *
     * @param controller the controller
     * @throws IOException the io exception
     */
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
        closeResources();
    }

    /**
     * Stops room.
     */
    public void stopRoom() {
        activeRoom = null;
    }

    private void closeResources() {
        if (activeRoom != null) {
            activeRoom.terminate();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMatchController(MatchController controller) {
        if (activeRoom != null) {
            activeRoom.setMatchController(controller);
        }
    }

    public void setHomeController(HomeController controller) {
        this.controller = controller;
    }
}
