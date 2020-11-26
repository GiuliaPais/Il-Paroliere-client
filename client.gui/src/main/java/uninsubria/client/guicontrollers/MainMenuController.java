package uninsubria.client.guicontrollers;

import java.io.IOException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import com.jfoenix.controls.JFXDialog;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import uninsubria.client.gui.Launcher;

/**
 * Controller class for the main menu screen.
 * @author Giulia Pais
 * @version 0.9.1
 *
 */
public class MainMenuController extends AbstractMainController{
	/*---Fields---*/
	@FXML private StackPane root;
	@FXML private StackPane menu_container;
	@FXML private Rectangle menu_bg;
	@FXML private ImageView logo;
	@FXML private VBox btn_vbox;
	@FXML private JFXButton login_btn, register_btn, opt_btn, exit_btn;
	
	private StringProperty login_btn_label;
	private StringProperty reg_btn_label;
	private StringProperty opt_btn_label;
	private StringProperty exit_btn_label;


	/*---Constructors---*/
	/**
	 * Returns an object of type MainMenuController.
	 */
	public MainMenuController() {
		super();
		this.login_btn_label = new SimpleStringProperty();
		this.reg_btn_label = new SimpleStringProperty();
		this.opt_btn_label = new SimpleStringProperty();
		this.exit_btn_label = new SimpleStringProperty();
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		login_btn.textProperty().bind(login_btn_label);
		register_btn.textProperty().bind(reg_btn_label);
		opt_btn.textProperty().bind(opt_btn_label);
		exit_btn.textProperty().bind(exit_btn_label);
		bindButtons();
		if (!Launcher.manager.isConnected()) {
			JFXDialog servAlert = serverAlert(root, menu_bg.getWidth());
			servAlert.show();
		}
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		login_btn_label.set(resBundle.getString("login_btn"));
		reg_btn_label.set(resBundle.getString("register_btn"));
		opt_btn_label.set(resBundle.getString("opt_btn"));
		exit_btn_label.set(resBundle.getString("exit_btn"));
	}
	
	/**
	 * Action performed when "Exit" button is pressed. Exits the application.
	 */
	@FXML
	public void exit() {
		Platform.exit();
		System.exit(0);
	}
	
	/**
	 *  Action performed when "Options" button is pressed. Displays the options menu.
	 * @throws IOException
	 */
	@FXML
	public void options() throws IOException {
		Parent p = requestParent(ControllerType.OPTIONS_MAIN);
		Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_TOP);
		anim.play();
	}
	
	/**
	 *  Action performed when "Login" button is pressed. Displays the login menu.
	 * @throws IOException
	 */
	@FXML
	public void login() throws IOException {
		Parent p = requestParent(ControllerType.LOGIN);
		Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_LEFT);
		anim.play();
	}
	
	/**
	 *  Action performed when "New Player" button is pressed. Displays the register menu.
	 * @throws IOException
	 */
	@FXML
	public void register() throws IOException {
		Parent p = requestParent(ControllerType.REGISTER);
		Timeline anim = sceneTransitionAnimation(p, SlideDirection.TO_LEFT);
		anim.play();
	}
	
	/*----------- Private methods for initialization and scaling -----------*/
	private void setMargins(double after) {
		double newTop = (after*ref.getReferences().get("MENU_MARGIN_T")) / ref.getReferences().get("REF_RESOLUTION");
		double newLeft = (after*ref.getReferences().get("MENU_MARGIN_L")) / ref.getReferences().get("REF_RESOLUTION");
		StackPane.setMargin(menu_container, new Insets(newTop, newLeft, newTop, newLeft));
		double vBoxMargin = after*ref.getReferences().get("VBOX_MARGIN_B") / ref.getReferences().get("REF_RESOLUTION");
		StackPane.setMargin(btn_vbox, new Insets(0, 0, vBoxMargin, 0));
	}
	
	private void scaleLogo(double after) {
		double newDim = (after*ref.getReferences().get("LOGO_DIM")) / ref.getReferences().get("REF_RESOLUTION");
		logo.setFitWidth(newDim);
		logo.setFitHeight(newDim);
	}
	
	private void scaleMenuBg(double after) {
		menu_bg.setWidth((after*ref.getReferences().get("RECT_WIDTH")) / ref.getReferences().get("REF_RESOLUTION")); 
		menu_bg.setHeight((after*ref.getReferences().get("RECT_HEIGHT")) / ref.getReferences().get("REF_RESOLUTION"));
	}
	
	private void scaleVbox(double after) {
		btn_vbox.setPrefSize(after*ref.getReferences().get("VBOX_W") / 1280, 
				after*ref.getReferences().get("VBOX_H") / ref.getReferences().get("REF_RESOLUTION"));
		btn_vbox.setSpacing(after*ref.getReferences().get("VBOX_SPACING") / ref.getReferences().get("REF_RESOLUTION"));
	}
	
	@Override
	protected void rescaleAll(double after) {
		super.rescaleAll(after);
		scaleLogo(after);
		scaleMenuBg(after);
		scaleVbox(after);
		setMargins(after);
	}
	
	private void bindButtons() {
		DoubleBinding spaces = btn_vbox.spacingProperty().multiply(3);
		login_btn.prefWidthProperty().bind(btn_vbox.prefWidthProperty());
		login_btn.prefHeightProperty().bind((btn_vbox.prefHeightProperty().subtract(spaces).divide(4)));
		register_btn.prefWidthProperty().bind(btn_vbox.prefWidthProperty());
		register_btn.prefHeightProperty().bind((btn_vbox.prefHeightProperty().subtract(spaces).divide(4)));
		opt_btn.prefWidthProperty().bind(btn_vbox.prefWidthProperty());
		opt_btn.prefHeightProperty().bind((btn_vbox.prefHeightProperty().subtract(spaces).divide(4)));
		exit_btn.prefWidthProperty().bind(btn_vbox.prefWidthProperty());
		exit_btn.prefHeightProperty().bind((btn_vbox.prefHeightProperty().subtract(spaces).divide(4)));
	}
	
	
}
