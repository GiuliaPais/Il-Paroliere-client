/**
 * 
 */
package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import uninsubria.client.gui.AspectRatio;
import uninsubria.client.gui.Controller;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.Resolution;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Abstract implementation of Controller interface.
 * @author Giulia Pais
 * @version 0.9.1
 *
 */
abstract class AbstractController implements Controller {
	/*---Fields---*/
	/**
	 * AspectRatio object used for references upon rescaling of GUI components.
	 */
	protected AspectRatio ref;
	protected DoubleProperty currentFontSize;

	/*---Constructors---*/
	/**
	 * Builds an object of type AbstractController
	 */
	public AbstractController() {
		this.ref = AspectRatio.getByRatio(Launcher.contrManager.getCurrentresolution().getAspectRatio());
		this.currentFontSize = new SimpleDoubleProperty();
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

	protected JFXDialog serverAlert(StackPane stackPane, double maxWidth) {
		String heading_content = Launcher.contrManager.getBundleValue().getString("alert_heading");
		String body_content = Launcher.contrManager.getBundleValue().getString("alert_body");
		Label heading = new Label(heading_content);
		double heading_font_size = currentFontSize.get() + 4;
		heading.setStyle("-fx-font-size: "+ heading_font_size +";");
		Label body = new Label(body_content);
		body.setStyle("-fx-font-size: "+currentFontSize.get()+";");
		JFXDialogLayout content = new JFXDialogLayout();
		content.getStylesheets().add(Launcher.contrManager.getAppTheme());
		content.setMaxWidth(maxWidth);
		content.setHeading(heading);
		content.setBody(body);
		JFXButton ok_button = new JFXButton("OK");
		ok_button.setStyle("-fx-font-size: "+currentFontSize.get()+";");
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
		ok_button.setOnAction((actionEvent -> {dialog.close();}));
		content.setActions(ok_button);
		return dialog;
	}

	protected JFXDialog serverConnectedAlert(StackPane stackPane, double maxWidth, String address) {
		String heading_content = Launcher.contrManager.getBundleValue().getString("alert_connected_heading");
		String body_content = Launcher.contrManager.getBundleValue().getString("alert_connected_body");
		Label heading = new Label(heading_content);
		double heading_font_size = currentFontSize.get() + 4;
		heading.setStyle("-fx-font-size: "+ heading_font_size +";");
		Label body = new Label(body_content + " " + address);
		body.setStyle("-fx-font-size: "+currentFontSize.get()+";");
		JFXDialogLayout content = new JFXDialogLayout();
		content.getStylesheets().add(Launcher.contrManager.getAppTheme());
		content.setMaxWidth(maxWidth);
		content.setHeading(heading);
		content.setBody(body);
		JFXButton ok_button = new JFXButton("OK");
		ok_button.setStyle("-fx-font-size: "+currentFontSize.get()+";");
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
		ok_button.setOnAction((actionEvent -> {dialog.close();}));
		content.setActions(ok_button);
		return dialog;
	}
	
}
