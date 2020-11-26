/**
 * 
 */
package uninsubria.client.centralmanagement;

import uninsubria.client.comm.ProxyServer;
import uninsubria.client.settings.ConnectionPrefs;
import uninsubria.client.settings.SettingDefaults;
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
 * @version 0.9.1
 */
public class CentralManager {
	/*---Fields---*/
	private Preferences preferences;
	private ProxyServer proxy;
	private ConnectionPrefs addressList;

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
		this.proxy = null;
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
		if (this.proxy == null) {
			this.proxy = getProxyFromList(this.addressList.getServer_addresses());
		}
		if (this.proxy != null) {
			return proxy.getSocket().getInetAddress().getHostAddress();
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
		if (this.proxy == null) {
			return false;
		} else {
			return true;
		}
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
	public List<String> requestActivationCode(String name, String lastname, String userID, String email, String password) {
		try {
			String hashedPw = PasswordEncryptor.hashPassword(password);
			ServiceResultInterface sent = proxy.requestActivationCode(name, lastname, userID, email, hashedPw);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
