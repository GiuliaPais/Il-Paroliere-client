package uninsubria.client.roomserver;

import uninsubria.client.monitors.*;
import uninsubria.utils.business.GameScore;
import uninsubria.utils.business.Word;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

/**
 * A thread that serves as Skeleton for the lobby.
 *
 * @author Giulia Pais
 * @version 0.9.12
 */
public class RoomSkeleton extends Thread implements ProxySkeletonInterface {
    /*---Fields---*/
    private Socket roomClient;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Instant timerStartingTime;

    //++ Monitors ++//
    private GameStartMonitor gameStartMonitor;
    private MatchGridMonitor matchGridMonitor;
    private TimeoutMonitor timeoutMonitor;
    private InterruptMonitor interruptMonitor;
    private SendWordsMonitor sendWordsMonitor;
    private GameScoresMonitor gameScoresMonitor;
    private EndGameMonitor endGameMonitor;

    /*---Constructors---*/
    public RoomSkeleton(Socket roomClient, GameStartMonitor startMonitor, MatchGridMonitor matchGridMonitor, InterruptMonitor interruptMonitor) {
        this.roomClient = roomClient;
        this.gameStartMonitor = startMonitor;
        this.matchGridMonitor = matchGridMonitor;
        this.interruptMonitor = interruptMonitor;
        this.setDaemon(true);
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
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
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
    public void readCommand(String command) throws IOException, ClassNotFoundException, InterruptedException {
        if (in == null) {
            this.in = new ObjectInputStream(new BufferedInputStream(roomClient.getInputStream()));
        }
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (Objects.requireNonNull(com)) {
            case GAME_STARTING -> {
                writeCommand(CommProtocolCommands.GAME_STARTING);
            }
            case SET_SYNC -> {
                timerStartingTime = (Instant) in.readObject();
                long delay = Instant.now().until(timerStartingTime, ChronoUnit.MILLIS);
                gameStartMonitor.signalStart(delay);
            }
            case INTERRUPT_GAME -> {
                interruptMonitor.setInterrupted();
            }
            case NEW_MATCH -> {
                String[] gameF = (String[]) in.readObject();
                Integer[] gameN = (Integer[]) in.readObject();
                matchGridMonitor.signalMatchAvailable(gameF, gameN);
                matchGridMonitor.awaitAck();
                writeCommand(CommProtocolCommands.NEW_MATCH);
            }
            case SEND_WORDS -> {
                sendWordsMonitor.sendRequest();
                ArrayList<String> words = sendWordsMonitor.getProposedWords();
                writeCommand(CommProtocolCommands.SEND_WORDS, words);
            }
            case SEND_SCORE -> {
                Hashtable<String, Word[]> mWords = (Hashtable<String, Word[]>) in.readObject();
                Hashtable<String, Integer[]> score = (Hashtable<String, Integer[]>) in.readObject();
                String w = in.readUTF();
                GameScore scores = new GameScore();
                scores.setScores(score);
                scores.setMatchWords(mWords);
                scores.setWinner(w);
                gameScoresMonitor.publishScores(scores);
            }
            case TIMEOUT_MATCH -> {
                timeoutMonitor.setTimeOut();
                try {
                    timeoutMonitor.isReady();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writeCommand(CommProtocolCommands.TIMEOUT_MATCH);
            }
            case END_GAME -> endGameMonitor.setGameEnded();
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

    public void setTimeoutMonitor(TimeoutMonitor timeoutMonitor) {
        this.timeoutMonitor = timeoutMonitor;
    }

    public void setSendWordsMonitor(SendWordsMonitor sendWordsMonitor) {
        this.sendWordsMonitor = sendWordsMonitor;
    }

    public void setGameScoresMonitor(GameScoresMonitor gameScoresMonitor) {
        this.gameScoresMonitor = gameScoresMonitor;
    }

    public void setEndGameMonitor(EndGameMonitor endGameMonitor) {
        this.endGameMonitor = endGameMonitor;
    }
}
