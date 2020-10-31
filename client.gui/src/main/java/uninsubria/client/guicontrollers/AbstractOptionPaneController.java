/**
 * 
 */
package uninsubria.client.guicontrollers;

import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import uninsubria.client.gui.Launcher;

/**
 * Implementation of AbstractController that represent an internal pane of the options menu.
 * Has a reference to the main OptionsController.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public abstract class AbstractOptionPaneController extends AbstractController {
	/*---Fields---*/
	@FXML GridPane grid;
	@FXML ColumnConstraints val_col, labels_col;
	
	private OptionsController mainOptionsController;

	/*---Constructors---*/
	/**
	 * Builds an object of type AbstractOptionPaneController
	 */
	public AbstractOptionPaneController() {}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		rescaleAll(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]);
		grid.prefHeightProperty().bind(mainOptionsController.central_view.prefHeightProperty());
		grid.prefWidthProperty().bind(mainOptionsController.central_view.prefWidthProperty());
	}
	
	/**
	 * 
	 * @return The value of mainOptionsController
	 */
	public OptionsController getMainOptionsController() {
		return mainOptionsController;
	}

	/**
	 * Sets the value of the mainOptionsController
	 * @param mainOptionsController The controller
	 */
	public void setMainOptionsController(OptionsController mainOptionsController) {
		this.mainOptionsController = mainOptionsController;
	}
	
	@Override
	protected void scaleFontSize(double after) {
		double newFontSize = (after*ref.getReferences().get("FONT_SIZE")) / ref.getReferences().get("REF_RESOLUTION");
		grid.setStyle("-fx-font-size: "+ newFontSize + "px;");
	}
	
	@Override
	protected void rescaleAll(double after) {
		scaleFontSize(after);
	};
}
