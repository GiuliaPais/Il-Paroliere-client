package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import uninsubria.client.gui.AspectRatio;
import uninsubria.client.gui.Controller;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.Resolution;

import java.io.IOException;

/**
 * Abstract implementation of Controller interface.
 *
 * @author Giulia Pais
 * @version 0.9.3
 */
abstract class AbstractController implements Controller {
	/*---Fields---*/
	/**
	 * AspectRatio object used for references upon rescaling of GUI components.
	 */
	protected AspectRatio ref;
	/**
	 * The Current font size.
	 */
	protected final DoubleProperty currentFontSize;

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
		Launcher.contrManager.bundleProperty().addListener((observable, oldValue, newValue) -> setTextResources(newValue));
	}

	/**
	 * Request the loading of the fxml file for the desired controller
	 *
	 * @param ctype the requested controller type
	 * @return a Parent object (can be set as root for a scene)
	 * @throws IOException if fxml can't be loaded
	 */
	Parent requestParent(ControllerType ctype) throws IOException {
		return Launcher.contrManager.loadParent(ctype.getFile());
	}

	/**
	 * Request the loading of the fxml file for the desired controller, intended for the loading of option menu panes
	 * and other parents where controller fields need to be set in advance
	 *
	 * @param ctype      the requested controller type
	 * @param controller The parent option controller
	 * @return a Parent object (can be set as root for a scene)
	 * @throws IOException if fxml can't be loaded
	 */
	Parent requestParent(ControllerType ctype, Controller controller) throws IOException {
		return Launcher.contrManager.loadParentWithController(ctype.getFile(), controller);
	}

	/**
	 * Rescales all GUI components based on the width in input and the reference.
	 *
	 * @param after The width to use for rescaling
	 */
	abstract protected void rescaleAll(double after);
	
	private ChangeListener<Resolution> getResizeListener() {
		return (observable, oldValue, newValue) -> {
			ref = AspectRatio.getByRatio(newValue.getAspectRatio());
			rescaleAll(newValue.getWidthHeight()[0]);
		};
	}

	/**
	 * Scales the font size of the text elements based on the width in input and the reference.
	 *
	 * @param after The width to use for rescaling
	 */
	abstract protected void scaleFontSize(double after);

	/**
	 * Produces a dialog that notifies a server malfunction.
	 *
	 * @param stackPane the stack pane where the dialog should be shown
	 * @param maxWidth  the max width
	 * @return A JFXdialog
	 */
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
		ok_button.setOnAction((actionEvent -> dialog.close()));
		content.setActions(ok_button);
		return dialog;
	}

	/**
	 * Produces a dialog that notifies the server is connected.
	 *
	 * @param stackPane the stack pane where the dialog should be shown
	 * @param maxWidth  the max width
	 * @param address   the ip address of the server
	 * @return A JFXdialog
	 */
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
		ok_button.setOnAction((actionEvent -> dialog.close()));
		content.setActions(ok_button);
		return dialog;
	}

	/**
	 * Generate a custom general JFXDialog with a simple text body and an "OK" button.
	 *
	 * @param stackPane the stack pane where the dialog should be shown
	 * @param maxWidth  the max width
	 * @param heading   the heading of the dialog
	 * @param body      the body of the dialog
	 * @return the jfx dialog
	 */
	public JFXDialog generateDialog(StackPane stackPane, double maxWidth, String heading, String body) {
		Label head = new Label(heading);
		Label bodyContent = new Label(body);
		JFXDialogLayout content = new JFXDialogLayout();
		content.getStylesheets().add(Launcher.contrManager.getAppTheme());
		content.setMaxWidth(maxWidth);
		content.setHeading(head);
		content.setBody(bodyContent);
		JFXButton ok_button = new JFXButton("OK");
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
		ok_button.setOnAction((actionEvent -> dialog.close()));
		content.setActions(ok_button);
		return dialog;
	}

	/**
	 * Generate a custom general JFXDialog with a custom series of nodes body and an "OK" button.
	 *
	 * @param stackPane the stack pane where the dialog should be shown
	 * @param maxWidth  the max width
	 * @param heading   the heading of the dialog
	 * @param bodyNodes the body of the dialog
	 * @return the jfx dialog
	 */
	public JFXDialog generateDialog(StackPane stackPane, double maxWidth, String heading, Node... bodyNodes) {
		Label head = new Label(heading);
		JFXDialogLayout content = new JFXDialogLayout();
		content.getStylesheets().add(Launcher.contrManager.getAppTheme());
		content.setMaxWidth(maxWidth);
		content.setHeading(head);
		content.setBody(bodyNodes);
		JFXButton ok_button = new JFXButton("OK");
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
		ok_button.setOnAction((actionEvent -> dialog.close()));
		content.setActions(ok_button);
		return dialog;
	}

	/**
	 * Generate a custom general JFXDialog with a simple text body and "YES"/"NO" buttons.
	 *
	 * @param stackPane  the stack pane where the dialog should be shown
	 * @param maxWidth   the max width
	 * @param heading    the heading of the dialog
	 * @param body       the body of the dialog
	 * @param yesHandler the yes handler
	 * @param noHandler  the no handler
	 * @return the jfx dialog
	 */
	public JFXDialog generateYNDialog(StackPane stackPane, double maxWidth, String heading, String body,
									  EventHandler<ActionEvent> yesHandler, EventHandler<ActionEvent> noHandler) {
		JFXDialog dialog = new JFXDialog();
		JFXDialogLayout content = new JFXDialogLayout();
		content.getStylesheets().add(Launcher.contrManager.getAppTheme());
		content.setMaxWidth(maxWidth);
		if (heading != null) {
			Label head = new Label(heading);
			content.setHeading(head);
		}
		Label bodyContent = new Label(body);
		content.setBody(bodyContent);
		JFXButton yes = new JFXButton(Launcher.contrManager.getBundleValue().getString("yes").toUpperCase());
		JFXButton no = new JFXButton(Launcher.contrManager.getBundleValue().getString("no").toUpperCase());
		if (noHandler != null) {
			no.setOnAction(noHandler);
		} else {
			no.setOnAction(event -> dialog.close());
		}
		yes.setOnAction(event -> {
			dialog.close();
			yesHandler.handle(event);});
		content.setActions(yes, no);
		dialog.setDialogContainer(stackPane);
		dialog.setContent(content);
		dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		return dialog;
	}
}
