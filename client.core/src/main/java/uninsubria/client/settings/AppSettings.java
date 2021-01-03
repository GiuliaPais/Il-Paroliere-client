package uninsubria.client.settings;

import javafx.beans.property.*;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Represents a predefined set of user preferences for the application.
 *
 * @author Giulia Pais
 * @version 0.9.3
 */
public class AppSettings {
	/*---Fields---*/
	private Preferences prefs;
	private BooleanProperty fullscreen, rememberMe;
	private DoubleProperty aspectRatio, width, height;
	private StringProperty theme, language, userName;
	private ConnectionPrefs connectionPrefs;

	/*---Constructors---*/

	/**
	 * Builds an object of type AppSettings
	 *
	 * @param prefs            The preferences node to associate
	 * @param fullscreen       boolean value to set fullscreen mode
	 * @param aspectRatio      aspect ratio of the screen
	 * @param width            width of the screen
	 * @param height           height of the screen
	 * @param theme            the name of the application graphical theme
	 * @param language         the preferred language
	 * @param remember         should the user credentials be remembered?
	 * @param userName         the user name
	 * @param server_addresses a ConnectionPrefs object
	 */
	public AppSettings(Preferences prefs, boolean fullscreen, double aspectRatio, double width, double height,
					   String theme, String language, boolean remember, String userName,
					   ConnectionPrefs server_addresses) {
		this.prefs = prefs;
		this.fullscreen = new SimpleBooleanProperty(fullscreen);
		this.aspectRatio = new SimpleDoubleProperty(aspectRatio);
		this.width = new SimpleDoubleProperty(width);
		this.height = new SimpleDoubleProperty(height);
		this.theme = new SimpleStringProperty(theme);
		this.language = new SimpleStringProperty(language);
		this.rememberMe = new SimpleBooleanProperty(remember);
		this.userName = new SimpleStringProperty(userName);
		this.connectionPrefs = server_addresses;
	}

	/**
	 * Builds an object of type AppSettings
	 *
	 * @param prefs The preferences node to associate
	 */
	public AppSettings(Preferences prefs) {
		this.prefs = prefs;
		this.fullscreen = new SimpleBooleanProperty();
		this.aspectRatio = new SimpleDoubleProperty();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();
		this.theme = new SimpleStringProperty();
		this.language = new SimpleStringProperty();
		this.rememberMe = new SimpleBooleanProperty();
		this.userName = new SimpleStringProperty();
		this.connectionPrefs = new ConnectionPrefs("localhost");
	}

	/**
	 * Builds an object of type AppSettings as a copy of another AppSettings object.
	 *
	 * @param settings The object to replicate
	 */
	public AppSettings(AppSettings settings) {
		this.prefs = settings.getPrefs();
		this.fullscreen = new SimpleBooleanProperty(settings.isFullscreen());
		this.aspectRatio = new SimpleDoubleProperty(settings.getAspectRatio());
		this.width = new SimpleDoubleProperty(settings.getWidth());
		this.height = new SimpleDoubleProperty(settings.getHeight());
		this.theme = new SimpleStringProperty(settings.getTheme());
		this.language = new SimpleStringProperty(settings.getLanguage());
		this.rememberMe = new SimpleBooleanProperty(settings.isRememberMe());
		this.userName = new SimpleStringProperty(settings.getUserName());
		this.connectionPrefs = settings.getConnectionPrefs();
	}

	/**
	 * Fullscreen property boolean property.
	 *
	 * @return the boolean property
	 */
	/*---Methods---*/
	public BooleanProperty fullscreenProperty() {
		return this.fullscreen;
	}

	/**
	 * Is fullscreen boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFullscreen() {
		return fullscreen.get();
	}

	/**
	 * Sets fullscreen.
	 *
	 * @param fullscreen the fullscreen
	 */
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen.set(fullscreen);
	}

	/**
	 * Aspect ratio property double property.
	 *
	 * @return the double property
	 */
	public DoubleProperty aspectRatioProperty() {
		return this.aspectRatio;
	}

	/**
	 * Gets aspect ratio.
	 *
	 * @return the aspect ratio
	 */
	public double getAspectRatio() {
		return aspectRatio.get();
	}

	/**
	 * Sets aspect ratio.
	 *
	 * @param aspectRatio the aspect ratio
	 */
	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio.set(aspectRatio);
	}

	/**
	 * Width property double property.
	 *
	 * @return the double property
	 */
	public DoubleProperty widthProperty() {
		return this.width;
	}

	/**
	 * Gets width.
	 *
	 * @return the width
	 */
	public double getWidth() {
		return width.get();
	}

	/**
	 * Sets width.
	 *
	 * @param width the width
	 */
	public void setWidth(double width) {
		this.width.set(width);
	}

	/**
	 * Height property double property.
	 *
	 * @return the double property
	 */
	public DoubleProperty heightProperty() {
		return this.height;
	}

	/**
	 * Gets height.
	 *
	 * @return the height
	 */
	public double getHeight() {
		return height.get();
	}

	/**
	 * Sets height.
	 *
	 * @param height the height
	 */
	public void setHeight(double height) {
		this.height.set(height);
	}

	/**
	 * Theme property string property.
	 *
	 * @return the string property
	 */
	public StringProperty themeProperty() {
		return this.theme;
	}

	/**
	 * Gets theme.
	 *
	 * @return the theme
	 */
	public String getTheme() {
		return theme.get();
	}

	/**
	 * Sets theme.
	 *
	 * @param theme the theme
	 */
	public void setTheme(String theme) {
		this.theme.set(theme);
	}

	/**
	 * Language property string property.
	 *
	 * @return the string property
	 */
	public StringProperty languageProperty() {
		return this.language;
	}

	/**
	 * Gets language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language.get();
	}

	/**
	 * Sets language.
	 *
	 * @param language the language
	 */
	public void setLanguage(String language) {
		this.language.set(language);
	}

	/**
	 * Gets prefs.
	 *
	 * @return the prefs
	 */
	public Preferences getPrefs() {
		return prefs;
	}

	/**
	 * Sets prefs.
	 *
	 * @param prefs the prefs
	 */
	public void setPrefs(Preferences prefs) {
		this.prefs = prefs;
	}

	/**
	 * Gets connection prefs.
	 *
	 * @return the connection prefs
	 */
	public ConnectionPrefs getConnectionPrefs() {
		return connectionPrefs;
	}

	/**
	 * Sets connection prefs.
	 *
	 * @param connectionPrefs the connection prefs
	 */
	public void setConnectionPrefs(ConnectionPrefs connectionPrefs) {
		this.connectionPrefs = connectionPrefs;
	}

	/**
	 * Gets the value of rememberMe
	 *
	 * @return the value of rememberMe
	 */
	public boolean isRememberMe() {
		return rememberMe.get();
	}

	/**
	 * Remember me property boolean property.
	 *
	 * @return the boolean property
	 */
	public BooleanProperty rememberMeProperty() {
		return rememberMe;
	}

	/**
	 * Sets remember me.
	 *
	 * @param rememberMe the remember me
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe.set(rememberMe);
	}

	/**
	 * Gets user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName.get();
	}

	/**
	 * User name property string property.
	 *
	 * @return the string property
	 */
	public StringProperty userNameProperty() {
		return userName;
	}

	/**
	 * Sets user name.
	 *
	 * @param userName the user name
	 */
	public void setUserName(String userName) {
		this.userName.set(userName);
	}

	/**
	 * Writes the preferences to the storage
	 *
	 * @throws BackingStoreException the backing store exception
	 */
	public void writePrefs() throws BackingStoreException {
		prefs.putBoolean("FULLSCREEN", isFullscreen());
		prefs.putDouble("ASPECT_RATIO", getAspectRatio());
		prefs.putDouble("WIDTH", getWidth());
		prefs.putDouble("HEIGHT", getHeight());
		prefs.put("THEME", getTheme());
		prefs.put("LANGUAGE", getLanguage());
		prefs.putBoolean("REMEMBER_ME", isRememberMe());
		if (getUserName() != null) {
			prefs.put("USERNAME", getUserName());
		}
		prefs.put("SERVER_ADDRESSES", this.connectionPrefs.addressesAsString());
		prefs.flush();
	}

}
