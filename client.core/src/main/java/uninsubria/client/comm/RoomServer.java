package uninsubria.client.comm;

import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class RoomServer extends Thread {
    /*---Fields---*/
    private ServerSocket serverSocket;
    private RoomSkeleton activeRoom;

    /*---Constructors---*/
    public RoomServer() throws IOException {
        serverSocket = new ServerSocket(CommHolder.ROOM_PORT);
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
                activeRoom = new RoomSkeleton(room);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
