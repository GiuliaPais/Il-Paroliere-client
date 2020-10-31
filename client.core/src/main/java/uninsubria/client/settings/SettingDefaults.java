/**
 * 
 */
package uninsubria.client.settings;

import java.util.prefs.Preferences;

/**
 * Object representing setting defaults.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class SettingDefaults extends AppSettings {
	/*---Fields---*/

	/*---Constructors---*/
	/**
	 * Builds an object of type SettingDefaults
	 * @param prefs The preferences node
	 */
	public SettingDefaults(Preferences prefs) {
		super(prefs);
		setWidth(1920.0);
		setHeight(1080.0);
		setAspectRatio(16.0/9.0);
		setLanguage("ITALIAN");
		setTheme("NIGHT_SKY");
		setFullscreen(true);
	}

	/*---Methods---*/
	
}
