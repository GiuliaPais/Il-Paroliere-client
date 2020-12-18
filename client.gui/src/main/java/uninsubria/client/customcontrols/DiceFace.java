package uninsubria.client.customcontrols;

import com.jfoenix.controls.JFXToggleNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;

/**
 * Custom jfx control representing a selectable dice in the game grid.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DiceFace extends JFXToggleNode {
    /*---Fields---*/
    private static final String DEFAULT_STYLE_CLASS = "ip-dice-face";
    private static final String BASE_STYLE_CLASS = "dice-base";
    private static final String FACE_LABEL_CLASS = "face-label";
    private static final String NUM_LABEL_CLASS = "number-label";
    private static final String SELECTED_NUM_LABEL_CLASS = "selected-number-label";

    /**
     * This pseudo class indicates if the toggle can be selected.
     */
    private static final PseudoClass PSEUDO_CLASS_SELECTABLE = PseudoClass.getPseudoClass("selectable");
    /**
     * This pseudo class indicates if the toggle can be un-selected.
     */
    private static final PseudoClass PSEUDO_CLASS_DESELECTABLE = PseudoClass.getPseudoClass("deselectable");

    private StackPane diceBase;
    private Label diceNumber, diceFace, selectedNumber;
    private BooleanProperty selectable, deselectable;
    private GameGrid.GridIndex indexInGrid;

    /*---Constructors---*/
    public DiceFace() {
        super();
        initialize();
    }

    public DiceFace(String diceN, String diceF) {
        this();
        this.diceNumber.setText(diceN);
        this.diceFace.setText(diceF);
    }

    /*---Methods---*/
    private void initialize() {
        this.diceBase = new StackPane();
        this.diceNumber = new Label();
        this.diceFace = new Label();
        this.selectedNumber = new Label();
        this.selectable = new BooleanPropertyBase(true) {

            @Override
            public void set(boolean newValue) {
                super.set(newValue);
                pseudoClassStateChanged(PSEUDO_CLASS_SELECTABLE, newValue);
            }

            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "selectable";
            }
        };
        this.deselectable = new BooleanPropertyBase() {
            @Override
            public void set(boolean newValue) {
                super.set(newValue);
                pseudoClassStateChanged(PSEUDO_CLASS_DESELECTABLE, newValue);
            }

            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "deselectable";
            }
        };
        diceBase.getChildren().addAll(diceNumber, diceFace, selectedNumber);
        diceBase.setPrefSize(100, 100);
        StackPane.setAlignment(diceNumber, Pos.TOP_LEFT);
        StackPane.setAlignment(diceFace, Pos.CENTER);
        StackPane.setAlignment(selectedNumber, Pos.TOP_RIGHT);
        super.setGraphic(diceBase);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        diceBase.getStyleClass().add(BASE_STYLE_CLASS);
        diceFace.getStyleClass().add(FACE_LABEL_CLASS);
        diceNumber.getStyleClass().add(NUM_LABEL_CLASS);
        selectedNumber.getStyleClass().add(SELECTED_NUM_LABEL_CLASS);
        this.setSelectable(true);
        this.setDeselectable(true);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DiceSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return this.getClass().getResource("/css/ip-dice-face.css").toExternalForm();
    }

    @Override
    public void fire() {
        if (!isDisabled()) {
            if (!isSelected() & isSelectable()) {
                setSelected(true);
                fireEvent(new ActionEvent());
                return;
            }
            if (isSelected() & isDeselectable()) {
                setSelected(false);
                fireEvent(new ActionEvent());
                return;
            }
        }
    }

    public StackPane getDiceBase() {
        return diceBase;
    }

    public void setDiceBase(StackPane diceBase) {
        this.diceBase = diceBase;
    }

    public Label getDiceNumber() {
        return diceNumber;
    }

    public void setDiceNumber(Label diceNumber) {
        this.diceNumber = diceNumber;
    }

    public void setDiceNumber(String number) {
        this.diceNumber.setText(number);
    }

    public void setDiceNumber(Integer number) {
        this.diceNumber.setText(number.toString());
    }

    public Label getDiceFace() {
        return diceFace;
    }

    public void setDiceFace(Label diceFace) {
        this.diceFace = diceFace;
    }

    public void setDiceFace(String diceFace) {
        this.diceFace.setText(diceFace);
    }

    public boolean isSelectable() {
        return selectable.get();
    }

    public BooleanProperty selectableProperty() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    public boolean isDeselectable() {
        return deselectable.get();
    }

    public BooleanProperty deselectableProperty() {
        return deselectable;
    }

    public void setDeselectable(boolean deselectable) {
        this.deselectable.set(deselectable);
    }

    public GameGrid.GridIndex getIndexInGrid() {
        return indexInGrid;
    }

    public void setIndexInGrid(GameGrid.GridIndex indexInGrid) {
        this.indexInGrid = indexInGrid;
    }

    public Label getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(Label selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public void setSelectedNumber(String sn) {
        selectedNumber.setText(sn);
    }
}
