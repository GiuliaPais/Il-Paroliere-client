/**
 * 
 */
package uninsubria.client.gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uninsubria.client.centralmanagement.CentralManager;

/**
 * The entry point of the application. Initializes the GUI.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class Launcher extends Application{
	
	/*---Fields---*/
	/**
	 * The CentralManager for this application.
	 */
	public static CentralManager manager;
	/**
	 * The controller manager for this application.
	 */
	public static ControllerManager contrManager;
	/**
	 * The main stage on which scenes are rendered.
	 */
	public static Stage mainStage;

	/*---Methods---*/
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		manager = new CentralManager();
		contrManager = new ControllerManager();
		Parent root = contrManager.loadMainMenu();
		boolean fullscreen = manager.getPreferences().getBoolean("FULLSCREEN", true);
		if (fullscreen) {
			primaryStage.setFullScreen(true);
			primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			primaryStage.initStyle(StageStyle.UNDECORATED);
		} else {
			primaryStage.initStyle(StageStyle.UNDECORATED);
		}
		primaryStage.centerOnScreen();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch();
	}
}
