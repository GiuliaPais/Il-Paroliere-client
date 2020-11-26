/**
 * 
 */
package uninsubria.client.comm;

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

/**
 * Represents a proxy object in the Proxy-Skeleton communication pattern.
 * The class is mainly responsible for writing on or reading from the socket.
 *
 * @author Giulia Pais
 * @version 0.9.1
 *
 */
public class ProxyServer implements PlayerManagerInterface, ProxySkeletonInterface {
    /*---Fields---*/
    private Socket socket;
    private String address;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private List<ServiceResult> serviceResultList;


	/*---Constructors---*/
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
    public Player login(String user, String pw) {
        return null;
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
        switch (com) {
            case ACTIVATION_CODE -> {
                serviceResultList.add((ServiceResult) in.readObject());
            }
        }
    }


}
