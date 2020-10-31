/**
 * 
 */
package uninsubria.client.guicontrollers;

import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import uninsubria.client.gui.AspectRatio;
import uninsubria.client.gui.Launcher;
import uninsubria.client.gui.Resolution;

/**
 * Controller class for the "Graphics" pane in the options menu.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class OptionGraphicsController extends AbstractOptionPaneController{
	/*---Fields---*/
	@FXML Label fullscreen_lbl, ar_lbl, resol_lbl;
	@FXML ChoiceBox<Double> ar_choice;
	@FXML ChoiceBox<Resolution> res_choice;
	@FXML JFXToggleButton fullscreen_toggle;
	
	private StringProperty fs_text, ar_text, r_text;
	
	private ChangeListener<Resolution> res_change_listener;

	/*---Constructors---*/
	/**
	 * Builds an object of type OptionGraphicsController
	 */
	public OptionGraphicsController() {
		super();
		fs_text = new SimpleStringProperty();
		ar_text = new SimpleStringProperty();
		r_text = new SimpleStringProperty();
		res_change_listener = new ChangeListener<Resolution>() {

			@Override
			public void changed(ObservableValue<? extends Resolution> observable, Resolution oldValue,
					Resolution newValue) {
				Launcher.contrManager.setChosenResolution(newValue);		
			}
			
		};
	}
	
	/*---Methods---*/
	@Override
	public void initialize() {
		super.initialize();
		/* Set bindings */
		fullscreen_lbl.textProperty().bind(fs_text);
		ar_lbl.textProperty().bind(ar_text);
		resol_lbl.textProperty().bind(r_text);
		fullscreen_lbl.prefHeightProperty().bind(fullscreen_toggle.prefHeightProperty());
		ar_lbl.prefHeightProperty().bind(ar_choice.prefHeightProperty());
		resol_lbl.prefHeightProperty().bind(res_choice.prefHeightProperty());
		/* Fullscreen toggle */
		fullscreen_toggle.setSelected(Launcher.contrManager.getSettings().isFullscreen());
		Launcher.contrManager.getSettings().fullscreenProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				fullscreen_toggle.setSelected(newValue);
			}
			
		});
		fullscreen_toggle.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Launcher.contrManager.getSettings().setFullscreen(fullscreen_toggle.isSelected());	
			}
			
		});
		/* Manage choice boxes */
		populateARChoiceBox();
		changeResChoiceBox(Launcher.contrManager.getChosenResolution().getAspectRatio(), Launcher.contrManager.getChosenResolution());
		ar_choice.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				AspectRatio ar = AspectRatio.getByRatio(newValue.doubleValue());
				Double closestW = ar.findClosestWidth(Launcher.contrManager.chosenResolutionProperty().get().getWidthHeight()[0]);
				Resolution newRes = new Resolution(newValue.doubleValue(), closestW);
				Launcher.contrManager.setChosenResolution(newRes);
				changeResChoiceBox(newValue.doubleValue(), newRes);
			}
			
		});
		if (Launcher.contrManager.getSettings().isFullscreen()) {
			ar_choice.setDisable(true);
			res_choice.setDisable(true);
		}
		fullscreen_toggle.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ar_choice.setDisable(newValue);
				res_choice.setDisable(newValue);
			}
			
		});
		Launcher.contrManager.chosenResolutionProperty().addListener(new ChangeListener<Resolution>() {

			@Override
			public void changed(ObservableValue<? extends Resolution> observable, Resolution oldValue,
					Resolution newValue) {
				ar_choice.setValue(newValue.getAspectRatio());				
			}
			
		});
	}
	
	@Override
	public void setTextResources(ResourceBundle resBundle) {
		fs_text.set(resBundle.getString("opt_fs_label"));
		ar_text.set(resBundle.getString("opt_ar_label"));
		r_text.set(resBundle.getString("opt_res_label"));
	}

	@Override
	protected void rescaleAll(double after) {
		super.rescaleAll(after);
		rescaleChoiceBoxes(after);
		rescaleToggle(after);
	}
	
	/* ---- Private internal methods for rescaling and init ---- */
	
	private void rescaleChoiceBoxes(double after) {
		double lenAfter = after*ref.getReferences().get("OPT_CBOX_W") / ref.getReferences().get("REF_RESOLUTION");
		double hAfter = after*ref.getReferences().get("OPT_CBOX_H") / ref.getReferences().get("REF_RESOLUTION");
		ar_choice.setPrefHeight(hAfter);
		res_choice.setPrefHeight(hAfter);
		ar_choice.setPrefWidth(lenAfter);
		res_choice.setPrefWidth(lenAfter);
	}
	
	private void rescaleToggle(double after) {
		double widthAfter = after*ref.getReferences().get("OPT_TOGGLE_DIM") / ref.getReferences().get("REF_RESOLUTION");
		fullscreen_toggle.setScaleX(widthAfter / ref.getReferences().get("OPT_TOGGLE_DIM"));
		fullscreen_toggle.setScaleY(widthAfter / ref.getReferences().get("OPT_TOGGLE_DIM"));
	}
	
	private void populateARChoiceBox() {
		StringConverter<Double> converter = new StringConverter<>() {

			@Override
			public String toString(Double object) {
				if (object.doubleValue() == 16.0/9.0) {
					return "16:9";
				}
				if (object.doubleValue() == 16.0/10.0) {
					return "16:10";
				}
				if (object.doubleValue() == 4.0/3.0) {
					return "4:3";
				}
				return "UNKNOWN";
			}

			@Override
			public Double fromString(String string) {
				String[] nd = string.split(".");
				double numerator, denominator;
				numerator = Double.parseDouble(nd[0]);
				denominator = Double.parseDouble(nd[1]);
				return numerator / denominator;
			}
			
		};
		for (AspectRatio a : EnumSet.allOf(AspectRatio.class)) {
			ar_choice.getItems().add(a.getRatio());
		}
		ar_choice.setValue(Launcher.contrManager.getChosenResolution().getAspectRatio());
		ar_choice.setConverter(converter);
	}
	
	private void populateResolutionChoiceBox(Double selectedRatio, Resolution selectedValue) {
		 AspectRatio ar = AspectRatio.getByRatio(selectedRatio);
		 List<Double[]> resolutions = ar.getResolutions();
		 res_choice.getItems().clear();
		 for (Double[] r : resolutions) {
			 res_choice.getItems().add(new Resolution(r));
		 }
		 res_choice.setValue(selectedValue);
	}
	
	private void changeResChoiceBox(Double selectedRatio, Resolution selectedValue) {
		res_choice.valueProperty().removeListener(res_change_listener);
		populateResolutionChoiceBox(selectedRatio, selectedValue);
		res_choice.valueProperty().addListener(res_change_listener);
	}
	
}
