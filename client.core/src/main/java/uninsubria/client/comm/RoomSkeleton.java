package uninsubria.client.comm;

import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.managersAPI.RoomProxyInterface;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class RoomSkeleton extends Thread implements ProxySkeletonInterface {
    /*---Fields---*/
    private Socket roomClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /*---Constructors---*/
    public RoomSkeleton(Socket roomClient) {
        this.roomClient = roomClient;
        start();
    }

    /*---Methods---*/
    @Override
    public void run() {
        try {
            this.out = new ObjectOutputStream(new BufferedOutputStream(roomClient.getOutputStream()));
            this.in = new ObjectInputStream(new BufferedInputStream(roomClient.getInputStream()));
            String command = in.readUTF();
            while(!command.equals(CommProtocolCommands.QUIT.getCommand())) {
                readCommand(command);
                command = in.readUTF();
            }
            terminate();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeCommand(CommProtocolCommands command, Object... params) throws IOException {

    }

    @Override
    public void readCommand(String command) throws IOException, ClassNotFoundException {
        if (in == null) {
            this.in = new ObjectInputStream(new BufferedInputStream(roomClient.getInputStream()));
        }
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (Objects.requireNonNull(com)) {
            case SET_SYNC -> {
                RoomProxyInterface.TimerType timerType = (RoomProxyInterface.TimerType) in.readObject();
                writeCommand(CommProtocolCommands.SET_SYNC);
                Instant future = (Instant) in.readObject();
                //do other stuff TODO
            }
        }
    }

    public void terminate() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
        try {
            out.close();
        } catch (IOException ignored) {
        }
        try {
            roomClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
