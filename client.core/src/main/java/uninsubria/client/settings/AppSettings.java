/**
 * 
 */
package uninsubria.client.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a predefined set of user preferences for the application.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class AppSettings {
	/*---Fields---*/
	private Preferences prefs;
	private BooleanProperty fullscreen;
	private DoubleProperty aspectRatio;
	private DoubleProperty width;
	private DoubleProperty height;
	private StringProperty theme;
	private StringProperty language;

	/*---Constructors---*/
	/**
	 * Builds an object of type AppSettings
	 * @param prefs The preferences node to associate
	 * @param fullscreen boolean value to set fullscreen mode
	 * @param aspectRatio aspect ratio of the screen
	 * @param width width of the screen
	 * @param height height of the screen
	 * @param theme the name of the application graphical theme 
	 * @param language the preferred language
	 */
	public AppSettings(Preferences prefs, boolean fullscreen, double aspectRatio, double width, double height, String theme, String language) {
		this.prefs = prefs;
		this.fullscreen = new SimpleBooleanProperty(fullscreen);
		this.aspectRatio = new SimpleDoubleProperty(aspectRatio);
		this.width = new SimpleDoubleProperty(width);
		this.height = new SimpleDoubleProperty(height);
		this.theme = new SimpleStringProperty(theme);
		this.language = new SimpleStringProperty(language);
	}
	
	/**
	 * Builds an object of type AppSettings
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
	}
	
	/**
	 * Builds an object of type AppSettings as a copy of another AppSettings object.
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
	}
	
	/*---Methods---*/
	public BooleanProperty fullscreenProperty() {
		return this.fullscreen;
	}
	
	public boolean isFullscreen() {
		return fullscreen.get();
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen.set(fullscreen);
	}
	
	public DoubleProperty aspectRatioProperty() {
		return this.aspectRatio;
	}

	public double getAspectRatio() {
		return aspectRatio.get();
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio.set(aspectRatio);
	}

	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	public double getWidth() {
		return width.get();
	}

	public void setWidth(double width) {
		this.width.set(width);
	}
	
	public DoubleProperty heightProperty() {
		return this.height;
	}

	public double getHeight() {
		return height.get();
	}

	public void setHeight(double height) {
		this.height.set(height);;
	}

	public StringProperty themeProperty() {
		return this.theme;
	}
	
	public String getTheme() {
		return theme.get();
	}

	public void setTheme(String theme) {
		this.theme.set(theme);;
	}
	
	public StringProperty languageProperty() {
		return this.language;
	}

	public String getLanguage() {
		return language.get();
	}

	public void setLanguage(String language) {
		this.language.set(language);
	}

	public Preferences getPrefs() {
		return prefs;
	}

	public void setPrefs(Preferences prefs) {
		this.prefs = prefs;
	}
	
	/**
	 * Writes the preferences to the storage
	 * @throws BackingStoreException
	 */
	public void writePrefs() throws BackingStoreException {
		prefs.putBoolean("FULLSCREEN", this.fullscreen.get());
		prefs.putDouble("ASPECT_RATIO", this.aspectRatio.get());
		prefs.putDouble("WIDTH", this.width.get());
		prefs.putDouble("HEIGHT", this.height.get());
		prefs.put("THEME", this.theme.get());
		prefs.put("LANGUAGE", this.language.get());
		prefs.flush();
	}

}
