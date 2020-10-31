/**
 * 
 */
package uninsubria.client.guicontrollers;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import uninsubria.client.gui.AspectRatio;
import uninsubria.client.gui.Controller;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.Resolution;

/**
 * Abstract implementation of Controller interface.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
abstract class AbstractController implements Controller {
	/*---Fields---*/
	/**
	 * AspectRatio object used for references upon rescaling of GUI components.
	 */
	protected AspectRatio ref;

	/*---Constructors---*/
	/**
	 * Builds an object of type AbstractController
	 */
	public AbstractController() {
		this.ref = AspectRatio.getByRatio(Launcher.contrManager.getCurrentresolution().getAspectRatio());
	}

	/*---Methods---*/
	/**
	 * Initializes this controller
	 */
	public void initialize() {
		setTextResources(Launcher.contrManager.getBundleValue());
		Launcher.contrManager.currentresolutionBinding().addListener(getResizeListener());
		Launcher.contrManager.bundleProperty().addListener(new ChangeListener<ResourceBundle>() {

			@Override
			public void changed(ObservableValue<? extends ResourceBundle> observable, ResourceBundle oldValue,
					ResourceBundle newValue) {
				setTextResources(newValue);				
			}
			
		});
	}
	
	/**
	 * Request the loading of the fxml file for the desired controller
	 * @param ctype the requested controller type
	 * @return a Parent object (can be set as root for a scene)
	 * @throws IOException if fxml can't be loaded
	 */
	Parent requestParent(ControllerType ctype) throws IOException {
		return Launcher.contrManager.loadParent(ctype.getFile());
	}
	
	/**
	 * Request the loading of the fxml file for the desired controller, intended for the loading of option menu panes
	 * @param ctype the requested controller type
	 * @param controller The parent option controller
	 * @return a Parent object (can be set as root for a scene)
	 * @throws IOException if fxml can't be loaded
	 */
	Parent requestParent(ControllerType ctype, Controller controller) throws IOException {
		return Launcher.contrManager.loadParentOptionPane(ctype.getFile(), controller);
	}
	
	/**
	 * Rescales all GUI components based on the width in input and the reference.
	 * @param after The width to use for rescaling
	 */
	abstract protected void rescaleAll(double after);
	
	private ChangeListener<Resolution> getResizeListener() {
		return new ChangeListener<Resolution>() {

			@Override
			public void changed(ObservableValue<? extends Resolution> observable, Resolution oldValue, Resolution newValue) {
				ref = AspectRatio.getByRatio(newValue.getAspectRatio());
				rescaleAll(newValue.getWidthHeight()[0]);
			}
			
		};
	}
	
	/**
	 * Scales the font size of the text elements based on the width in input and the reference.
	 * @param after The width to use for rescaling
	 */
	abstract protected void scaleFontSize(double after);
	
}
