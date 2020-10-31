/**
 * 
 */
package uninsubria.client.centralmangment;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import uninsubria.client.comm.ProxyServer;
import uninsubria.client.settings.SettingDefaults;

/**
 * Class responsible for central communication between client and server. Also manages preferences at start up.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class CentralManager {
	/*---Fields---*/
	private Preferences preferences;
	private ProxyServer proxy;

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
		//INIT PROXY
	}

	/*---Methods---*/
	/**
	 * 
	 * @return The value of preferences
	 */
	public Preferences getPreferences() {
		return this.preferences;
	}
}
