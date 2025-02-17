package uninsubria.client.centralmanagement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uninsubria.client.comm.ProxyServer;
import uninsubria.client.monitors.*;
import uninsubria.client.roomserver.RoomCentralManager;
import uninsubria.client.settings.ConnectionPrefs;
import uninsubria.client.settings.SettingDefaults;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.Player;
import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.languages.Language;
import uninsubria.utils.security.PasswordEncryptor;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class responsible for central communication between client and server. Also manages preferences at start up.
 *
 * @author Giulia Pais
 * @version 0.9.12
 */
public class CentralManager {
	/*---Fields---*/
	private final Preferences preferences;
	private final ObjectProperty<ProxyServer> proxy;
	private ConnectionPrefs addressList;
	private ObjectProperty<Player> profile;
	private DatagramSocket datagramSocket;

	/*---Constructors---*/

	/**
	 * Builds an object of type CentralManager
	 *
	 * @throws BackingStoreException the backing store exception
	 */
	public CentralManager() throws BackingStoreException {
		if (!Preferences.userRoot().nodeExists("IlParoliere")) {
			this.preferences = Preferences.userRoot().node("IlParoliere");
			new SettingDefaults(this.preferences).writePrefs();
		} else {
			this.preferences = Preferences.userRoot().node("IlParoliere");
		}
		this.addressList = new ConnectionPrefs(preferences.get(
				"SERVER_ADDRESSES", "localhost"));
		this.proxy = new SimpleObjectProperty<>();
		this.profile = new SimpleObjectProperty<>();
	}

	/*---Methods---*/

	/**
	 * Gets preferences.
	 *
	 * @return the preferences
	 */
	public Preferences getPreferences() {
		return this.preferences;
	}

	/**
	 * Tries to build a ProxyServer object from the list of preferred ip addresses. The method returns
	 * when the first instance of ProxyServer can be instantiated (does not build multiple objects).
	 * @param addressList The list of ip adresses (as a list of strings)
	 * @return A ProxyServer object if a running server was found and the connection was accepted, null otherwise
	 */
	private ProxyServer getProxyFromList(List<String> addressList) {
		ProxyServer proxy = null;
		for (String address : addressList) {
			try {
				proxy = new ProxyServer(address);
				break;
			} catch (IOException ignored) {
			}
		}
		return proxy;
	}

	/**
	 * Gets address list.
	 *
	 * @return the address list
	 */
	public ConnectionPrefs getAddressList() {
		return addressList;
	}

	/**
	 * Sets address list.
	 *
	 * @param addressList the address list
	 */
	public void setAddressList(ConnectionPrefs addressList) {
		this.addressList = addressList;
	}

	/**
	 * Trys to connect to the server, if it succeeds it returns the ip address of the server.
	 *
	 * @return The ip address of the server
	 */
	public String tryConnectServer() {
		if (this.proxy.getValue() == null) {
			ProxyServer proxyServer = getProxyFromList(this.addressList.getServer_addresses());
			this.proxy.setValue(proxyServer);
		}
		if (this.proxy.get() != null) {
			return proxy.getValue().getSocket().getInetAddress().getHostAddress();
		} else {
			return null;
		}
	}

	/**
	 * Is the manager connected to the server?
	 *
	 * @return true or false
	 */
	public boolean isConnected() {
		return this.proxy.getValue() != null;
	}

	/**
	 * Sets disconnected.
	 */
	public void setDisconnected() {
		this.proxy.set(null);
	}

	/**
	 * Returns the proxyProperty.
	 *
	 * @return the object property
	 */
	public ObjectProperty<ProxyServer> proxyProperty() {
		return this.proxy;
	}

	/**
	 * Initiates the procedure for the request of the activation code when a user wants to register.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param name     the name
	 * @param lastname the lastname
	 * @param userID   the user id
	 * @param email    the email
	 * @param password the password
	 * @return A list of errors
	 * @throws IOException the io exception
	 */
	public List<String> requestActivationCode(String name, String lastname, String userID, String email, String password) throws IOException {
		try {
			String hashedPw = PasswordEncryptor.hashPassword(password);
			ServiceResultInterface sent = proxy.get().requestActivationCode(name, lastname, userID, email, hashedPw);
			Result<?> errors = sent.getResult("Errors");
			List<String> errMsgs = new ArrayList<>();
			if (errors != null) {
				ErrorMsgType[] ers = (ErrorMsgType[]) sent.getResult("Errors").getValue();
				for (ErrorMsgType e : ers) {
					errMsgs.add(e.name());
				}
			}
			return errMsgs;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Completes the procedure for the user registration by confirming the code the user recieved via email.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param email the email
	 * @param code  the code
	 * @return a list of errors
	 * @throws IOException the io exception
	 */
	public List<String> confirmActivationCode(String email, String code) throws IOException {
		ServiceResultInterface ack = proxy.get().confirmActivationCode(email, code);
		Result<Player> playerResult = (Result<Player>) ack.getResult("Profile");
		List<String> errMsgs = new ArrayList<>();
		if (playerResult.getValue() != null) {
			if (this.profile == null) {
				this.profile = new SimpleObjectProperty<>(playerResult.getValue());
			} else {
				this.profile.set(playerResult.getValue());
			}
			return errMsgs;
		}
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Resends a code via email in case the user didn't receive it. If the code is expired,
	 * it silently generates a new code and sends it to the email address provided.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param email       the email
	 * @param requestType the request type
	 * @return a list of errors
	 * @throws IOException the io exception
	 */
	public List<String> resendCode(String email, String requestType) throws IOException {
		ServiceResultInterface ack = proxy.get().resendCode(email, requestType);
		Result<?> errors = ack.getResult("Errors");
		List<String> errMsgs = new ArrayList<>();
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Initiates the login procedure for a user.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param id the id
	 * @param pw the pw
	 * @return a list of errors
	 * @throws IOException              the io exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public List<String> login(String id, String pw) throws IOException, NoSuchAlgorithmException {
		String hashedPw = PasswordEncryptor.hashPassword(pw);
		ServiceResultInterface ack = proxy.get().login(id, hashedPw);
		Result<Player> playerResult = (Result<Player>) ack.getResult("Profile");
		List<String> errMsgs = new ArrayList<>();
		if (playerResult.getValue() != null) {
			if (this.profile == null) {
				this.profile = new SimpleObjectProperty<>(playerResult.getValue());
			} else {
				this.profile.set(playerResult.getValue());
			}
			return errMsgs;
		}
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Gets the user profile.
	 *
	 * @return the profile
	 */
	public Player getProfile() {
		return profile.get();
	}

	/**
	 * Gets the profileProperty.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Player> profileProperty() {
		return profile;
	}

	/**
	 * Sets the user profile.
	 *
	 * @param profile the profile
	 */
	public void setProfile(Player profile) {
		this.profile.set(profile);
	}

	/**
	 * Commit profile changes.
	 */
	public void commitProfileChanges() {
		if (isConnected()) {
			try {
				proxy.get().updatePlayerInfo(getProfile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Request userID change.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param oldID the old id
	 * @param newID the new id
	 * @return the list
	 * @throws IOException the io exception
	 */
	public List<String> changeUserID(String oldID, String newID) throws IOException {
		ServiceResultInterface ack = proxy.get().changeUserId(oldID, newID);
		Result<Player> playerResult = (Result<Player>) ack.getResult("Profile");
		List<String> errMsgs = new ArrayList<>();
		if (playerResult.getValue() != null) {
			if (this.profile == null) {
				this.profile = new SimpleObjectProperty<>(playerResult.getValue());
			} else {
				this.profile.set(playerResult.getValue());
			}
			return errMsgs;
		}
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Request password change.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param email the email
	 * @param oldPW the old pw
	 * @param newPW the new pw
	 * @return a list of errors
	 * @throws IOException              the io exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public List<String> changePassword(String email, String oldPW, String newPW) throws IOException, NoSuchAlgorithmException {
		String hashedOldPw = PasswordEncryptor.hashPassword(oldPW);
		String hashedNewPw = PasswordEncryptor.hashPassword(newPW);
		ServiceResultInterface ack = proxy.get().changePassword(email, hashedOldPw, hashedNewPw);
		Boolean changed = (Boolean) ack.getResult("Changed").getValue();
		List<String> errMsgs = new ArrayList<>();
		if (changed) {
			profile.get().setPassword(hashedNewPw);
			return errMsgs;
		}
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Request password reset after clicking on "Password forgotten" prompt.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param email the email
	 * @return a list of errors
	 * @throws IOException the io exception
	 */
	public List<String> resetPassword(String email) throws IOException {
		ServiceResultInterface ack = proxy.get().resetPassword(email);
		Boolean changed = (Boolean) ack.getResult("Success").getValue();
		List<String> errMsgs = new ArrayList<>();
		if (changed) {
			return errMsgs;
		}
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Request the deletion of the user profile.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param id       the id
	 * @param password the password
	 * @return a list of errors
	 * @throws IOException              the io exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public List<String> deleteProfile(String id, String password) throws IOException, NoSuchAlgorithmException {
		String hashedPw = PasswordEncryptor.hashPassword(password);
		ServiceResultInterface ack = proxy.get().deleteProfile(id, hashedPw);
		List<String> errMsgs = new ArrayList<>();
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Signals server that the client application has been closed.
	 *
	 * @throws IOException the io exception
	 */
	public void quit() throws IOException {
		if (proxy.get() != null) {
			proxy.get().quit();
		}
	}

	/**
	 * Requests an update of the room list via datagram socket.
	 *
	 * @return the map of lobbies
	 * @throws IOException            the io exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public Map<UUID, Lobby> requestRoomUpdate() throws IOException, ClassNotFoundException {
		byte[] buf;
		if (datagramSocket == null) {
			datagramSocket = new DatagramSocket();
		}
		if (this.proxy == null | this.proxy.get() == null) {
			return null;
		}
		buf = CommProtocolCommands.SEND_ROOM_LIST.getCommand().getBytes();
		DatagramPacket packet
				= new DatagramPacket(buf, buf.length, InetAddress.getByName(proxy.get().getAddress()), CommHolder.SERVER_PORT);
		datagramSocket.send(packet);
		buf = new byte[5000];
		packet = new DatagramPacket(buf, buf.length);
		datagramSocket.receive(packet);
		ByteArrayInputStream byteStream = new
				ByteArrayInputStream(buf);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
		Map<UUID, Lobby> received = (ConcurrentHashMap<UUID, Lobby>) is.readObject();
		return received;
	}

	/**
	 * Request an update of the player list of the active room via datagram socket.
	 *
	 * @param roomID the room id
	 * @return the list of players currently in the room
	 * @throws IOException            the io exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public ArrayList<String> requestPlayerList(UUID roomID) throws IOException, ClassNotFoundException {
		byte[] buf;
		if (datagramSocket == null) {
			datagramSocket = new DatagramSocket();
		}
		if (this.proxy == null | this.proxy.get() == null) {
			return null;
		}
		buf = (CommProtocolCommands.SEND_PLIST.getCommand() + "|" + roomID.toString()).getBytes();
		DatagramPacket packet
				= new DatagramPacket(buf, buf.length, InetAddress.getByName(proxy.get().getAddress()), CommHolder.SERVER_PORT);
		datagramSocket.send(packet);
		buf = new byte[5000];
		packet = new DatagramPacket(buf, buf.length);
		datagramSocket.receive(packet);
		ByteArrayInputStream byteStream = new
				ByteArrayInputStream(buf);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
		ArrayList<String> received = (ArrayList<String>) is.readObject();
		return received;
	}

	/**
	 * Creates a new room on the room list.
	 *
	 * @param lobby the lobby
	 * @return the boolean
	 * @throws IOException the io exception
	 */
	public boolean createRoom(Lobby lobby) throws IOException {
		if (proxy.get() != null) {
			return proxy.get().createRoom(lobby);
		}
		return false;
	}

	/**
	 * Leaves a room.
	 *
	 * @param roomID the room id
	 * @throws IOException the io exception
	 */
	public void leaveRoom(UUID roomID) throws IOException {
		if (proxy.get() != null) {
			proxy.get().leaveRoom(roomID);
		}
	}

	/**
	 * Leave game.
	 *
	 * @param roomID the room id
	 * @throws IOException the io exception
	 */
	public void leaveGame(UUID roomID) throws IOException {
		if (proxy.get() != null) {
			proxy.get().leaveGame(roomID);
		}
		RoomCentralManager.stopRoom();
	}

	/**
	 * Joins a selected room.
	 * If the request succeeds the method returns an empty list, otherwise it returns a list of error names
	 * to be localized in the selected language.
	 *
	 * @param roomID the room id
	 * @return a list of errors
	 * @throws IOException the io exception
	 */
	public List<String> joinRoom(UUID roomID) throws IOException {
		ServiceResultInterface ack = proxy.get().joinRoom(roomID);
		List<String> errMsgs = new ArrayList<>();
		Result<?> errors = ack.getResult("Errors");
		if (errors != null) {
			ErrorMsgType[] ers = (ErrorMsgType[]) ack.getResult("Errors").getValue();
			for (ErrorMsgType e : ers) {
				errMsgs.add(e.name());
			}
		}
		return errMsgs;
	}

	/**
	 * Fetch statistics from server.
	 *
	 * @return the service result
	 * @throws IOException the io exception
	 */
	public ServiceResultInterface fetchStatistics() throws IOException {
		ServiceResultInterface stats = proxy.get().fetchStatistics();
		return stats;
	}

	/**
	 * Request word stats to the server.
	 *
	 * @param word the word
	 * @return the service result
	 * @throws IOException the io exception
	 */
	public ServiceResultInterface requestWordStats(String word) throws IOException {
		ServiceResultInterface stats = proxy.get().requestWordStats(word);
		return stats;
	}

	/**
	 * Logs out the player.
	 *
	 * @throws IOException the io exception
	 */
	public void logout() throws IOException {
		if (proxy.get() != null) {
			proxy.get().logout(profile.get().getPlayerID());
		}
	}

	/**
	 * Starts the room server if one is not already active.
	 *
	 * @param monitor          monitor for synchronization on game start
	 * @param matchGridMonitor monitor for synchronization on setting a new grid (match)
	 * @param interruptMonitor monitor for synchronization on game interruption
	 */
	public void setupRoomServer(GameStartMonitor monitor, MatchGridMonitor matchGridMonitor, InterruptMonitor interruptMonitor) {
		try {
			RoomCentralManager.startRoomServer(monitor, matchGridMonitor, interruptMonitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Signals the server and player manager this player was kicked
	 * from the game and the room due to connection issues.
	 */
	public void iWasKicked() {
		try {
			if (proxy.get() != null) {
				proxy.get().signalWasKicked();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets all other monitors on the room skeleton for the match.
	 *
	 * @param sendWordsMonitor  the send words monitor
	 * @param timeoutMonitor    the timeout monitor
	 * @param gameScoresMonitor the game scores monitor
	 * @param endGameMonitor    the end game monitor
	 */
	public void setMatchMonitors(SendWordsMonitor sendWordsMonitor, TimeoutMonitor timeoutMonitor, GameScoresMonitor gameScoresMonitor, EndGameMonitor endGameMonitor) {
		RoomCentralManager.setSendWordsMonitor(sendWordsMonitor);
		RoomCentralManager.setTimeoutMonitor(timeoutMonitor);
		RoomCentralManager.setGameScoresMonitor(gameScoresMonitor);
		RoomCentralManager.setEndGameMonitor(endGameMonitor);
	}

	/**
	 * Request definitions of the words passed as parameter.
	 *
	 * @param words    the words
	 * @param language the language
	 * @return the service result interface
	 * @throws IOException the io exception
	 */
	public ServiceResultInterface requestDefinitions(String[] words, Language language) throws IOException {
		if (proxy.get() != null) {
			return proxy.get().requestWordsDefinitions(words, language);
		}
		return null;
	}
}
