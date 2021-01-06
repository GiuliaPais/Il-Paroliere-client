package uninsubria.client.guicontrollers;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;

import com.jfoenix.controls.JFXDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import uninsubria.client.gui.Launcher;
import uninsubria.client.settings.AppSettings;
import uninsubria.client.settings.SettingDefaults;

/**
 * Controller class for the options menu screen.
 * @author Giulia Pais
 * @version 0.9.3
 *
 */
public class OptionsController extends AbstractMainController {
	/*---Fields---*/
	@FXML Glyph icon;
	@FXML AnchorPane central_view;
	@FXML ToolBar toolbar;
	@FXML ToggleButton lang_toggle, graphics_toggle, server_toggle;
	@FXML ButtonBar btn_bar;
	@FXML Button save_btn, default_btn, undo_btn;
	@FXML Label title_label;
	@FXML HBox title_bar;
	@FXML StackPane stackPane;

	private AbstractMainController requestOrigin;

	private ToggleGroup tgroup;
	private StringProperty opt_title_lbl, lt_lbl, gt_lbl, def_btn_lbl, undo_btn_lbl, save_btn_lbl;
	
	private Parent languages, graphics, server;
	
	private DoubleProperty titleSize;
	
	private AppSettings before;
	private ObservableList<String> addresses_before;
	
	/*---Constructors---*/
	/**
	 * Builds an object of type OptionsController
	 */
	public OptionsController() {
		super();
		this.tgroup = new ToggleGroup();
		opt_title_lbl = new SimpleStringProperty();
		lt_lbl = new SimpleStringProperty();
		gt_lbl = new SimpleStringProperty();
		def_btn_lbl = new SimpleStringProperty();
		undo_btn_lbl = new SimpleStringProperty();
		save_btn_lbl = new SimpleStringProperty();
		before = new AppSettings(Launcher.contrManager.getSettings());
		titleSize = new SimpleDoubleProperty(ref.getReferences().get("FONT_SIZE_TITLE"));
		addresses_before = FXCollections.observableArrayList(before.getConnectionPrefs().getServer_addresses());
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		icon.setFontFamily("FontAwesome");
		icon.setIcon(FontAwesome.Glyph.GEARS);
		icon.setFontSize(titleSize.get()+10);
		titleSize.addListener((observable, oldValue, newValue) -> icon.setFontSize(newValue.doubleValue()+10));
		tgroup.getToggles().add(lang_toggle);
		tgroup.getToggles().add(graphics_toggle);
		tgroup.getToggles().add(server_toggle);
		tgroup.selectToggle(lang_toggle);
		bindLbls();
		try {
			OptionLanguagePaneController langController = new OptionLanguagePaneController();
			langController.setMainOptionsController(this);
			languages = requestParent(ControllerType.OPTIONS_LANGUAGES, langController);
			loadLanguagePanel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bindToggleWidth();
		bindComponentsDim();
	}
	
	/**
	 * Action performed when "Language" toggle is selected. Loads the language options view in the central area.
	 */
	@FXML
	void loadLanguagePanel() {
		central_view.getChildren().clear();
		central_view.getChildren().add(languages);
		setCentralAnchors(languages);
	}
	
	/**
	 * Action performed when "Graphics" toggle is selected. Loads the graphic options view in the central area.
	 */
	@FXML
	void loadGraphicsPanel() {
		if (graphics == null) {
			try {
				OptionGraphicsController graphContr = new OptionGraphicsController();
				graphContr.setMainOptionsController(this);
				graphics = requestParent(ControllerType.OPTIONS_GRAPHICS, graphContr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		central_view.getChildren().clear();
		central_view.getChildren().add(graphics);
		setCentralAnchors(graphics);
	}


	@FXML
	void loadServerPanel() {
		if (server == null) {
			try {
				OptionServerController serverContr = new OptionServerController();
				serverContr.setMainOptionsController(this);
				server = requestParent(ControllerType.OPTIONS_SERVER, serverContr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		central_view.getChildren().clear();
		central_view.getChildren().add(server);
		setCentralAnchors(server);
	}
	
	/**
	 * Action performed when the "Save" button is pressed. Saves the current preferences (options saved 
	 * are remembered the next time the application is executed). Finally loads the main menu screen.
	 * @throws BackingStoreException If problems occur in saving preferences
	 * @throws IOException 
	 */
	@FXML
	void saveOptions() throws BackingStoreException, IOException {
		Launcher.contrManager.writePrefs();
		Parent applicant;
		if (requestOrigin instanceof MainMenuController) {
			applicant = requestParent(ControllerType.MAIN_MENU);
			Timeline anim = sceneTransitionAnimation(applicant, SlideDirection.TO_BOTTOM);
			anim.play();
			return;
		}
		if (requestOrigin instanceof HomeController) {
			applicant = requestParent(ControllerType.HOME_VIEW, requestOrigin);
			Timeline anim = sceneTransitionAnimation(applicant, SlideDirection.TO_BOTTOM);
			anim.play();
			return;
		}
	}
	
	/**
	 * Action performed when the "Undo" button is pressed. Reverts all changes and loads main menu screen.
	 * @throws IOException
	 */
	@FXML
	void undo() throws IOException {
		before.getConnectionPrefs().setServer_addresses(addresses_before);
		Launcher.contrManager.setSettings(before);
		Parent applicant;
		if (requestOrigin instanceof MainMenuController) {
			applicant = requestParent(ControllerType.MAIN_MENU);
			Timeline anim = sceneTransitionAnimation(applicant, SlideDirection.TO_BOTTOM);
			anim.play();
			return;
		}
		if (requestOrigin instanceof HomeController) {
			applicant = requestParent(ControllerType.HOME_VIEW, requestOrigin);
			Timeline anim = sceneTransitionAnimation(applicant, SlideDirection.TO_BOTTOM);
			anim.play();
			return;
		}
	}
	
	/**
	 * Action performed when the "Default" button is pressed. Sets all options to the default value.
	 * @throws IOException
	 */
	@FXML
	void defaultSettings() {
		AppSettings def = new SettingDefaults(before);
		Launcher.contrManager.setSettings(def);
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		opt_title_lbl.set(resBundle.getString("opts_title"));
		lt_lbl.set(resBundle.getString("lang_toggle"));
		gt_lbl.set(resBundle.getString("graph_toggle"));
		def_btn_lbl.set(resBundle.getString("default_opt_btn"));
		undo_btn_lbl.set(resBundle.getString("undo_opt_btn"));
		save_btn_lbl.set(resBundle.getString("save_opt_btn"));
	}

	public void displayServerAlert() {
		JFXDialog dialog = serverAlert(stackPane, central_view.getPrefWidth());
		dialog.show();
	}

	public void displayServerConnectedAlert(String address_conn) {
		JFXDialog dialog = serverConnectedAlert(stackPane, central_view.getPrefWidth(), address_conn);
		dialog.show();
	}

	public AbstractMainController getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(AbstractMainController requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

	/* ---- Private internal methods for scaling and init ---- */
	
	private void bindLbls() {
		title_label.textProperty().bind(opt_title_lbl);
		lang_toggle.textProperty().bind(lt_lbl);
		graphics_toggle.textProperty().bind(gt_lbl);
		save_btn.textProperty().bind(save_btn_lbl);
		default_btn.textProperty().bind(def_btn_lbl);
		undo_btn.textProperty().bind(undo_btn_lbl);
	}
	
	private void setCentralAnchors(Parent parent) {
		AnchorPane.setBottomAnchor(parent, 0.0);
		AnchorPane.setLeftAnchor(parent, 0.0);
		AnchorPane.setRightAnchor(parent, 0.0);
		AnchorPane.setTopAnchor(parent, 0.0);
	}

	@Override
	protected void rescaleAll(double after) {
		super.rescaleAll(after);
		rescaleToolBar(after);
		rescaleBtnBar(after);
		rescaleTitleBar(after);
		central_view.setPrefSize(Launcher.contrManager.getCurrentresolution().getWidthHeight()[0]-toolbar.getPrefWidth(), 
				Launcher.contrManager.getCurrentresolution().getWidthHeight()[1]-title_bar.getPrefHeight()-btn_bar.getPrefHeight());
	}
	
	private void rescaleToolBar(double after) {
		double tool_w = after*this.ref.getReferences().get("OPT_TOOL_W") / this.ref.getReferences().get("REF_RESOLUTION");
		toolbar.setPrefWidth(tool_w);
		double tool_h = after*this.ref.getReferences().get("OPT_TOOL_H") / this.ref.getReferences().get("REF_RESOLUTION");
		toolbar.setPrefHeight(tool_h);
		double toggle_h = after*this.ref.getReferences().get("OPT_BTN_H") / this.ref.getReferences().get("REF_RESOLUTION");
		lang_toggle.setPrefHeight(toggle_h);
		graphics_toggle.setPrefHeight(toggle_h);
		server_toggle.setPrefHeight(toggle_h);
	}
	
	private void bindToggleWidth() {
		lang_toggle.prefWidthProperty().bind(toolbar.prefWidthProperty());
		graphics_toggle.prefWidthProperty().bind(toolbar.prefWidthProperty());
		server_toggle.prefWidthProperty().bind(toolbar.prefWidthProperty());
	}
	
	private void rescaleBtnBar(double after) {
		double btn_bar_h = after*this.ref.getReferences().get("OPT_BTNBAR_H") / this.ref.getReferences().get("REF_RESOLUTION");
		btn_bar.setPrefHeight(btn_bar_h);
	}
	
	private void rescaleTitleBar(double after) {
		double title_bar_h = after*this.ref.getReferences().get("OPT_TITLE_H") / this.ref.getReferences().get("REF_RESOLUTION");
		title_bar.setPrefHeight(title_bar_h);
		double title_font_size = after*ref.getReferences().get("FONT_SIZE_TITLE") / this.ref.getReferences().get("REF_RESOLUTION");
		title_label.setFont(new Font("Arial Black", title_font_size));;
		titleSize.set(title_font_size);
	}
	
	private void bindComponentsDim() {
		title_bar.prefWidthProperty().bind(root.prefWidthProperty());
		btn_bar.prefWidthProperty().bind(root.prefWidthProperty());
		title_label.prefHeightProperty().bind(title_bar.prefHeightProperty());
		icon.prefHeightProperty().bind(title_bar.prefHeightProperty());
		icon.prefWidthProperty().bind(title_bar.prefHeightProperty());
	}
	
}
