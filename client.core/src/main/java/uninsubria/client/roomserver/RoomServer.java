package uninsubria.client.roomserver;

import uninsubria.client.monitors.*;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Client-side server responsible for accepting connection requests
 * from rooms.
 *
 * @author Giulia Pais
 * @version 0.9.5
 */
public class RoomServer extends Thread {
    /*---Fields---*/
    private ServerSocket serverSocket;
    private RoomSkeleton activeGame;
    private GameStartMonitor gameStartMonitor;
    private MatchGridMonitor matchGridMonitor;
    private InterruptMonitor interruptMonitor;


    /*---Constructors---*/
    /**
     * Instantiates a new Room server.
     *
     * @throws IOException the io exception
     */
    public RoomServer(GameStartMonitor gm, MatchGridMonitor mm, InterruptMonitor im) throws IOException {
        serverSocket = new ServerSocket(CommHolder.ROOM_PORT);
        gameStartMonitor = gm;
        matchGridMonitor = mm;
        interruptMonitor = im;
        start();
    }

    /*---Methods---*/
    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                Socket room = serverSocket.accept();
                if (activeGame != null) {
                    activeGame.terminate();
                }
                activeGame = new RoomSkeleton(room, gameStartMonitor, matchGridMonitor, interruptMonitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeResources();
    }

    /**
     * Stops room.
     */
    public void stopGame() {
        activeGame = null;
    }

    private void closeResources() {
        if (activeGame != null) {
            activeGame.terminate();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameStartMonitor(GameStartMonitor gameStartMonitor) {
        this.gameStartMonitor = gameStartMonitor;
    }

    public void setMatchGridMonitor(MatchGridMonitor matchGridMonitor) {
        this.matchGridMonitor = matchGridMonitor;
    }

    public void setInterruptMonitor(InterruptMonitor interruptMonitor) {
        this.interruptMonitor = interruptMonitor;
    }

    public void setSendWordsMonitor(SendWordsMonitor sendWordsMonitor) {
        if (activeGame != null) {
            activeGame.setSendWordsMonitor(sendWordsMonitor);
        }
    }

    public void setTimeoutMonitor(TimeoutMonitor timeoutMonitor) {
        if (activeGame != null) {
            activeGame.setTimeoutMonitor(timeoutMonitor);
        }
    }

    public void setGameScoresMonitor(GameScoresMonitor gameScoresMonitor) {
        if (activeGame != null) {
            activeGame.setGameScoresMonitor(gameScoresMonitor);
        }
    }

    public void setEndGameMonitor(EndGameMonitor endGameMonitor) {
        if (activeGame != null) {
            activeGame.setEndGameMonitor(endGameMonitor);
        }
    }
}
