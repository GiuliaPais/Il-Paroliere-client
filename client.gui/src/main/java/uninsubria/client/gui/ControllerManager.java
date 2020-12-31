package uninsubria.client.gui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import uninsubria.client.guicontrollers.ControllerType;
import uninsubria.client.guicontrollers.Theme;
import uninsubria.client.settings.AppSettings;
import uninsubria.utils.languages.Language;
import uninsubria.utils.languages.LanguageManager;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Central class for coordinating GUI controllers.
 * @author Giulia Pais
 * @version 0.9.4
 *
 */
public class ControllerManager {
	/*---Fields---*/
	/**
	 * The internal relative path to the folder containing fxml files.
	 */
	private final String FXML_PATH = "/fxml/";
	
	/**
	 * Object to keep track of current settings in the application.
	 */
	private AppSettings settings;
	
	/**
	 * A LanguageManager object for dinamically obtaining the appropriate ResourceBundle object when language changes.
	 */
	private LanguageManager langManager;
	/**
	 * The ResourceBundle associated with the chosen language wrapped in an ObjectProperty.
	 */
	private ObjectProperty<ResourceBundle> bundle;
	/**
	 * The current Resolution in use by the application.
	 */
	private ObjectBinding<Resolution> current_resolution;
	/**
	 * The Resolution chosen by the user (via GUI interaction).
	 */
	private ObjectProperty<Resolution> chosen_resolution;
	/**
	 * The Resolution associated to the fullscreen mode.
	 */
	private Resolution FULLSCREEN_RES;
	/**
	 * Listener for changes on the preferred language.
	 */
	private ChangeListener<String> language_listener;
	/**
	 * Listener for changes on the current resolution.
	 */
	private ChangeListener<Resolution> resolution_listener;
	/**
	 * Listener for changes on fullscreen mode.
	 */
	private ChangeListener<Boolean> fullscreen_listener;

	/*---Constructors---*/
	/**
	 * Returns an object of type ControllerManager.
	 */
	public ControllerManager() {
		Preferences pref = Launcher.manager.getPreferences();
		this.settings = new AppSettings(pref, pref.getBoolean("FULLSCREEN", true),
				pref.getDouble("ASPECT_RATIO", 16.0 / 9.0),
				pref.getDouble("WIDTH", 1280.0),
				pref.getDouble("HEIGHT", 720.0), pref.get("THEME", "NIGHT_SKY"),
				pref.get("LANGUAGE", "ITALIAN"),
				pref.getBoolean("REMEMBER_ME", true),
				pref.get("USERNAME", ""),
				Launcher.manager.getAddressList());
		this.langManager = new LanguageManager(Language.valueOf(settings.getLanguage()));
		this.bundle = new SimpleObjectProperty<>(langManager.getResourcesBundle());
		this.chosen_resolution = new SimpleObjectProperty<>(new Resolution(settings.getAspectRatio(), settings.getWidth()));
		this.language_listener = (observable, oldValue, newValue) -> {
			LanguageManager lm = langManager;
			lm.setLang(Language.valueOf(newValue));
			langManager = lm;
			bundle.set(lm.getResourcesBundle());
		};
		this.resolution_listener = (observable, oldValue, newValue) -> {
			Double[] dims = newValue.getWidthHeight();
			Launcher.mainStage.setHeight(dims[1]);
			Launcher.mainStage.setWidth(dims[0]);
			Launcher.mainStage.centerOnScreen();
		};
		this.fullscreen_listener = (observable, oldValue, newValue) -> {
			Launcher.mainStage.setFullScreen(newValue);
			if (!newValue) {
				Launcher.mainStage.setHeight(current_resolution.get().getWidthHeight()[1]);
				Launcher.mainStage.setWidth(current_resolution.get().getWidthHeight()[0]);
				Launcher.mainStage.centerOnScreen();
			}
		};
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		FULLSCREEN_RES = new Resolution(new Double[] {screenBounds.getWidth(), screenBounds.getHeight()});
		this.current_resolution = new When(settings.fullscreenProperty()).then(FULLSCREEN_RES).otherwise(chosen_resolution);
		setUpPropertyListeners();
	} 

	/*---Methods---*/
	/**
	 * Loads the fxml file for the main menu screen.
	 * @return A Parent object (can be set as root for a Scene)
	 * @throws IOException If loading of the fxml file fails 
	 */
	public Parent loadMainMenu() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + ControllerType.MAIN_MENU.getFile()), bundle.get());
		Parent root = loader.load();
		String theme = getAppTheme();
		root.getStylesheets().clear();
		root.getStylesheets().add(theme);
		return root;
	}
	
	/**
	 * Loads the fxml file for the chosen view. This method is meant to be called by controllers.
	 * @param path The path to the file to load
	 * @return A Parent object (can be set as root for a Scene)
	 * @throws IOException If loading of the fxml file fails 
	 */
	public Parent loadParent(String path) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + path), bundle.get());
		Parent root = loader.load();
		String theme = getAppTheme();
		root.getStylesheets().clear();
		root.getStylesheets().add(theme);
		return root;
	}
	
	/**
	 * Loads the requested fxml file and sets the specified controller. Useful for controllers
	 * that have parameters that needs to be set before being loaded.
	 * @param path The path to the file to load
	 * @param controller The controller
	 * @return A Parent object (can be set as root for a Scene)
	 * @throws IOException If loading of the fxml file fails 
	 */
	public Parent loadParentWithController(String path, Controller controller) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + path), bundle.get());
		loader.setController(controller);
		Parent root = loader.load();
		String theme = getAppTheme();
		root.getStylesheets().clear();
		root.getStylesheets().add(theme);
		return root;
	}

	/**
	 * Loads a player tile fxml with the given controller.
	 *
	 * @param fxmlPath   the fxml path
	 * @param controller the controller
	 * @return the stack pane
	 * @throws IOException the io exception
	 */
	public StackPane loadPlayerScoreTile(String fxmlPath, Controller controller) throws IOException {
		StackPane stackPane = new StackPane();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxmlPath), bundle.get());
		loader.setController(controller);
		loader.setRoot(stackPane);
		StackPane root = loader.load();
		String theme = getAppTheme();
		root.getStylesheets().clear();
		root.getStylesheets().add(theme);
		return root;
	}

	/**
	 * Writes preferences in store.
	 * @throws BackingStoreException
	 */
	public void writePrefs() throws BackingStoreException {
		this.settings.writePrefs();
	}
	
	/**
	 * Initializes property listeners.
	 */
	private void setUpPropertyListeners() {
		this.current_resolution.addListener(resolution_listener);
		this.chosen_resolution.addListener((observable, oldValue, newValue) -> {
			Double[] dims = newValue.getWidthHeight();
			settings.setAspectRatio(newValue.getAspectRatio());
			settings.setWidth(dims[0]);
			settings.setHeight(dims[1]);
		});
		registerSettingListeners();
	}
	
	/**
	 * Register listeners on settings properties.
	 */
	private void registerSettingListeners() {
		settings.languageProperty().addListener(language_listener);
		settings.fullscreenProperty().addListener(fullscreen_listener);
	}
	
	/**
	 * 
	 * @return The bundle object property
	 */
	public ObjectProperty<ResourceBundle> bundleProperty() {
		return bundle;
	}
	
	/**
	 * 
	 * @return The value of the bundle property.
	 */
	public ResourceBundle getBundleValue() {
		return bundle.get();
	}

	/**
	 * 
	 * @return The value of settings
	 */
	public AppSettings getSettings() {
		return settings;
	}
	
	/**
	 * Sets the value of settings
	 * @param other Another AppSettings object
	 */
	public void setSettings(AppSettings other) {
		this.settings.setFullscreen(other.isFullscreen());
		this.chosen_resolution.set(new Resolution(other.getAspectRatio(), other.getWidth()));
		this.settings.setLanguage(other.getLanguage());
		this.settings.setTheme(other.getTheme());
		this.settings.setPrefs(other.getPrefs());
	}
	
	/**
	 * 
	 * @return The current resolution binding
	 */
	public final ObjectBinding<Resolution> currentresolutionBinding() {
		return this.current_resolution;
	}
	
	/**
	 * 
	 * @return The current resolution value
	 */
	public final Resolution getCurrentresolution() {
		return this.currentresolutionBinding().get();
	}
	
	/**
	 * 
	 * @return The chosen resolution property
	 */
	public ObjectProperty<Resolution> chosenResolutionProperty() {
		return this.chosen_resolution;
	}
	
	/**
	 * 
	 * @return The chosen resolution value
	 */
	public Resolution getChosenResolution() {
		return this.chosenResolutionProperty().get();
	}
	
	/**
	 * Sets the value of the chosen resolution
	 * @param chosen_resolution A Resolution object
	 */
	public final void setChosenResolution(Resolution chosen_resolution) {
		this.chosenResolutionProperty().set(chosen_resolution);
	}

	/**
	 * Gets app theme.
	 *
	 * @return the app theme
	 */
	public String getAppTheme() {
		Theme theme = Theme.valueOf(settings.getTheme());
		return getClass().getResource(theme.getFilePath()).toExternalForm();
	}
	
}
