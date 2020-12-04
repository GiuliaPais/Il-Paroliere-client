package uninsubria.client.guicontrollers;

/**
 * Enum class for the different types of scenes to display on GUI. Each has the reference to the appropriate fxml file.
 * @author Giulia Pais
 * @version 0.9.2
 */
public enum ControllerType {
	/*---Enum constants---*/
	MAIN_MENU("MainMenu.fxml"),
	OPTIONS_MAIN("Options.fxml"),
	OPTIONS_LANGUAGES("Options_languages.fxml"),
	OPTIONS_GRAPHICS("Options_graphics.fxml"),
	OPTIONS_ASPECT("Options_aspect.fxml"),
	OPTIONS_SERVER("Options_server.fxml"),
	LOGIN("Login.fxml"),
	REGISTER("Register.fxml"),
	ACTIVATION_CODE("ActivationCodeAlert.fxml"),
	HOME_VIEW("HomeView.fxml");

	/*---Fields---*/
	/**
	 * The file name of the associated fxml file
	 */
	private String filename;
	
	/*---Constructor---*/
	/**
	 * Builds an object of type ControllerType
	 * @param file The fxml file name
	 */
	ControllerType(String file) {
		this.filename = file;
	}

	/*---Methods---*/
	/**
	 * 
	 * @return The value of filename
	 */
	public String getFile() {
		return this.filename;
	}
}
