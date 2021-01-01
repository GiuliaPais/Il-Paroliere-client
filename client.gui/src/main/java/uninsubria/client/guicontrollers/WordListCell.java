package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

/**
 * Represents the list cell for the word list in match view.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class WordListCell extends JFXListCell<String> {
    /*---Fields---*/
    private EventHandler<ActionEvent> delHandler;
    private AnchorPane anchorPane = new AnchorPane();
    private final Glyph delIcon = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
    private JFXButton delBtn = new JFXButton();
    private Label content = new Label();
    private DoubleProperty fontSize;

    /*---Constructors---*/
    public WordListCell() {
        super();
        this.fontSize = new SimpleDoubleProperty(18);
        init();
    }

    /*---Methods---*/
    public double getFontSize() {
        return fontSize.get();
    }

    public DoubleProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize.set(fontSize);
    }

    @Override
    protected void updateItem(String word, boolean empty) {
        super.updateItem(word, empty);
        setText(null);
        if (word != null && !empty) {
            content.setText(word);
            setGraphic(anchorPane);
        } else {
            setGraphic(null);
        }
    }

    private EventHandler<ActionEvent> getDelHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getListView().getItems().remove(getItem());
            }
        };
        return handler;
    }

    private void init() {
        anchorPane.setPrefSize(this.getPrefWidth(), this.getPrefHeight());
        delIcon.setFontSize(fontSize.get());
        fontSize.addListener((observable, oldValue, newValue) -> delIcon.setFontSize(newValue.doubleValue()));
        delHandler = getDelHandler();
        delBtn.setGraphic(delIcon);
        delBtn.setOnAction(delHandler);
        anchorPane.getChildren().add(content);
        anchorPane.getChildren().add(delBtn);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(delBtn, 0.0);
        setGraphic(anchorPane);
    }
}
