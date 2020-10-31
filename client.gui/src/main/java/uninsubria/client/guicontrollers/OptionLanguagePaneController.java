/**
 * 
 */
package uninsubria.client.guicontrollers;

import java.util.EnumSet;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import uninsubria.client.gui.Launcher;
import uninsubria.utils.languages.Language;

/**
 * Controller class for the "Language" pane in the options menu.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class OptionLanguagePaneController extends AbstractOptionPaneController {

	/*---Fields---*/
	@FXML Label int_language_lbl;
	@FXML ChoiceBox<String> choice_box;
	
	private StringProperty lbl_text;

	/*---Constructors---*/
	/**
	 * Builds an object of type OptionLanguagePaneController
	 */
	public OptionLanguagePaneController() {
		super();
		lbl_text = new SimpleStringProperty();
	}

	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		EnumSet<Language> available_languages = EnumSet.allOf(Language.class);
		for (Language l: available_languages) {
			choice_box.getItems().add(l.name());
		}
		choice_box.valueProperty().bindBidirectional(Launcher.contrManager.getSettings().languageProperty());
		setTextResources(Launcher.contrManager.getBundleValue());
		int_language_lbl.textProperty().bind(lbl_text);
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		lbl_text.set(resBundle.getString("int_lang_label"));		
	}

}
