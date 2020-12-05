package uninsubria.client.centralmanagement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uninsubria.client.comm.ProxyServer;
import uninsubria.client.settings.ConnectionPrefs;
import uninsubria.client.settings.SettingDefaults;
import uninsubria.utils.business.Player;
import uninsubria.utils.security.PasswordEncryptor;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class responsible for central communication between client and server. Also manages preferences at start up.
 * @author Giulia Pais
 * @version 0.9.2
 */
public class CentralManager {
	/*---Fields---*/
	private Preferences preferences;
	private ObjectProperty<ProxyServer> proxy;
	private ConnectionPrefs addressList;
	private Player profile;

	/*---Constructors---*/
	/**
	 * Builds an object of type CentralManager
	 * @throws BackingStoreException
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
	}

	/*---Methods---*/
	/**
	 * 
	 * @return The value of preferences
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
			} catch (IOException e) {
			}
		}
		return proxy;
	}

	public ConnectionPrefs getAddressList() {
		return addressList;
	}

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
		if (this.proxy.getValue() == null) {
			return false;
		} else {
			return true;
		}
	}

	public void setDisconnected() {
		this.proxy.set(null);
	}

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
	 * @throws IOException
	 */
	public List<String> confirmActivationCode(String email, String code) throws IOException {
		ServiceResultInterface ack = proxy.get().confirmActivationCode(email, code);
		Result<Player> playerResult = (Result<Player>) ack.getResult("Profile");
		List<String> errMsgs = new ArrayList<>();
		if (playerResult.getValue() != null) {
			this.profile = playerResult.getValue();
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
	 * @throws IOException
	 */
	public List<String> resendCode(String email, String requestType) throws IOException {
		ServiceResultInterface ack = proxy.get().resendConde(email, requestType);
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
	 * @throws IOException
	 */
	public List<String> login(String id, String pw) throws IOException, NoSuchAlgorithmException {
		String hashedPw = PasswordEncryptor.hashPassword(pw);
		ServiceResultInterface ack = proxy.get().login(id, hashedPw);
		Result<Player> playerResult = (Result<Player>) ack.getResult("Profile");
		List<String> errMsgs = new ArrayList<>();
		if (playerResult.getValue() != null) {
			this.profile = playerResult.getValue();
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

	public Player getProfile() {
		return profile;
	}

	public void setProfile(Player profile) {
		this.profile = profile;
	}
}
