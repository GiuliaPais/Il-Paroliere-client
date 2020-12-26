package uninsubria.client.roomserver;

import javafx.application.Platform;
import uninsubria.client.guicontrollers.HomeController;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.Objects;

/**
 * A thread that serves as Skeleton for the lobby.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class RoomSkeleton extends Thread implements ProxySkeletonInterface {
    /*---Fields---*/
    private Socket roomClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private HomeController homeController;
    private String[] currentGridFaces;
    private Integer[] currentGridNumbers;
    private Instant timerStartingTime;

    /*---Constructors---*/
    public RoomSkeleton(Socket roomClient, HomeController homeController) {
        this.roomClient = roomClient;
        this.homeController = homeController;
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
            terminate();
            RoomCentralManager.stopRoom();
        }
    }

    @Override
    public void writeCommand(CommProtocolCommands command, Object... params) throws IOException {
        out.writeUTF(command.getCommand());
        for (Object p : params) {
            if (p instanceof String) {
                String s = (String) p;
                out.writeUTF(s);
            } else {
                out.writeObject(p);
            }
        }
        out.flush();
    }

    @Override
    public void readCommand(String command) throws IOException, ClassNotFoundException {
        if (in == null) {
            this.in = new ObjectInputStream(new BufferedInputStream(roomClient.getInputStream()));
        }
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (Objects.requireNonNull(com)) {
            case GAME_STARTING -> {
                currentGridFaces = (String[]) in.readObject();
                currentGridNumbers = (Integer[]) in.readObject();
                writeCommand(CommProtocolCommands.GAME_STARTING);
            }
            case SET_SYNC -> {
                timerStartingTime = (Instant) in.readObject();
                Platform.runLater(() -> homeController.gameStarting(currentGridFaces, currentGridNumbers, timerStartingTime));
            }
            case INTERRUPT_GAME -> {
                //interr
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
            if (out != null)
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
