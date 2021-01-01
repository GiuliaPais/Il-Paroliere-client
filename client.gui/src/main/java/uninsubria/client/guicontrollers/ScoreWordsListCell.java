package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXListCell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import uninsubria.utils.business.Word;

/**
 * Represents a custom list cell for the player score tile element.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ScoreWordsListCell extends JFXListCell<Word> {
    /*---Fields---*/
    private AnchorPane anchorPane = new AnchorPane();
    private Label content = new Label();
    private Glyph wrong = new Glyph("FontAwesome", FontAwesome.Glyph.TIMES);
    private Glyph duplicate = new Glyph("FontAwesome", FontAwesome.Glyph.WARNING);
    private DoubleProperty fontSize;

    /*---Constructors---*/
    public ScoreWordsListCell() {
        super();
        this.fontSize = new SimpleDoubleProperty(18);
        init();
    }

    /*---Methods---*/
    @Override
    protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item != null && !empty) {
            content.setText(item.getWord() + " - " + item.getPoints());
            if (!item.isDuplicated()) {
                duplicate.setVisible(false);
            } else {
                duplicate.setVisible(true);
            }
            if (!item.isWrong()) {
                wrong.setVisible(false);
            } else {
                wrong.setVisible(true);
            }
            setGraphic(anchorPane);
        } else {
            setGraphic(null);
        }
    }

    public double getFontSize() {
        return fontSize.get();
    }

    public DoubleProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize.set(fontSize);
    }

    private void init() {
        anchorPane.setPrefSize(this.getPrefWidth(), this.getPrefHeight());
        wrong.setFontSize(fontSize.get());
        duplicate.setFontSize(fontSize.get());
        wrong.getStyleClass().add("wrong-icon");
        duplicate.getStyleClass().add("duplicate-icon");
        fontSize.addListener((observable, oldValue, newValue) -> {
            wrong.setFontSize(newValue.doubleValue());
            duplicate.setFontSize(newValue.doubleValue());
        });
        anchorPane.getChildren().add(content);
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().addAll(wrong, duplicate);
        anchorPane.getChildren().add(hBox);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        setGraphic(anchorPane);
    }
}
