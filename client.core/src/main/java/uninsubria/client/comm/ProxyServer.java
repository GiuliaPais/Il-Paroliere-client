package uninsubria.client.comm;

import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.Player;
import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a proxy object in the Proxy-Skeleton communication pattern.
 * The class is mainly responsible for writing on or reading from the socket.
 *
 * @author Giulia Pais
 * @version 0.9.8
 *
 */
public class ProxyServer implements PlayerManagerInterface, ProxySkeletonInterface {
    /*---Fields---*/
    private final Socket socket;
    private final String address;
    private final ObjectOutputStream out;
    private ObjectInputStream in;
    private final List<ServiceResultInterface> serviceResultList;


	/*---Constructors---*/
    /**
     * Instantiates a new Proxy server.
     *
     * @param address the address
     * @throws IOException the io exception
     */
    public ProxyServer(String address) throws IOException {
        this.address = address;
        this.socket = new Socket(address, CommHolder.SERVER_PORT);
        this.socket.setKeepAlive(true);
        this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.serviceResultList = new ArrayList<>();
    }

	/*---Methods---*/

    public String getAddress() {
        return address;
    }

    public Socket getSocket() {
        return socket;
    }


    @Override
    public ServiceResultInterface requestActivationCode(String name, String lastname, String userID, String email, String password) throws IOException {
        writeCommand(CommProtocolCommands.ACTIVATION_CODE, name, lastname, userID, email, password);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface confirmActivationCode(String email, String code) throws IOException {
        writeCommand(CommProtocolCommands.CONFIRM_ACTIVATION_CODE, email, code);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface resendCode(String email, String requestType) throws IOException {
        writeCommand(CommProtocolCommands.RESEND_CODE, email, requestType);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface login(String user, String pw) throws IOException {
        writeCommand(CommProtocolCommands.LOGIN, user, pw);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public void logout(String userid) throws IOException {
        writeCommand(CommProtocolCommands.LOGOUT, userid);
    }

    @Override
    public void updatePlayerInfo(Player player) throws IOException {
        writeCommand(CommProtocolCommands.UPDATE_PLAYER_INFO, player);
    }

    @Override
    public ServiceResultInterface changeUserId(String oldUserID, String newUserID) throws IOException {
        writeCommand(CommProtocolCommands.CHANGE_USER_ID, oldUserID, newUserID);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface changePassword(String email, String oldPassword, String newPassword) throws IOException {
        writeCommand(CommProtocolCommands.CHANGE_PW, email, oldPassword, newPassword);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface deleteProfile(String id, String password) throws IOException {
        writeCommand(CommProtocolCommands.DELETE_PROFILE, id, password);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public void quit() throws IOException {
        writeCommand(CommProtocolCommands.QUIT);
        closeResources();
    }

    @Override
    public ServiceResultInterface resetPassword(String email) throws IOException {
        writeCommand(CommProtocolCommands.RESET_PW, email);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public void leaveRoom(UUID roomID) throws IOException {
        writeCommand(CommProtocolCommands.LEAVE_ROOM, roomID);
    }

    @Override
    public boolean createRoom(Lobby lobby) throws IOException {
        writeCommand(CommProtocolCommands.CREATE_ROOM, lobby);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ServiceResultInterface joinRoom(UUID roomID) throws IOException {
        writeCommand(CommProtocolCommands.JOIN_ROOM, roomID);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface fetchStatistics() throws IOException {
        writeCommand(CommProtocolCommands.FETCH_STATS);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
    }

    @Override
    public ServiceResultInterface requestWordStats(String word) throws IOException {
        writeCommand(CommProtocolCommands.WORD_STATS , word);
        try {
            if (in == null) {
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            readCommand(in.readUTF());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ServiceResultInterface res = serviceResultList.get(serviceResultList.size()-1);
        serviceResultList.remove(res);
        return res;
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
            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        }
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (Objects.requireNonNull(com)) {
            case ACTIVATION_CODE, CONFIRM_ACTIVATION_CODE, RESEND_CODE, LOGIN,
                    CHANGE_USER_ID, CHANGE_PW, RESET_PW,
                    DELETE_PROFILE, JOIN_ROOM, FETCH_STATS,
                    WORD_STATS -> serviceResultList.add((ServiceResultInterface) in.readObject());
            case CREATE_ROOM -> {}
        }
    }

    private void closeResources() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
