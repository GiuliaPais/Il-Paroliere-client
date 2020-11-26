package uninsubria.client.guicontrollers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;

/**
 * Represents a custom cell of a ListView, designed for showing preferred ip addresses.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class IpAddressListCell extends JFXListCell<String> {
    /*---Fields---*/
    private EventHandler<ActionEvent> delHandler;
    private AnchorPane anchorPane = new AnchorPane();
    private final Glyph delIcon = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
    private JFXButton delBtn = new JFXButton();
    private Label content = new Label();

    /*---Constructors---*/
    public IpAddressListCell(DoubleProperty fontsize) throws IOException {
        super();
        anchorPane.setPrefSize(this.getPrefWidth(), this.getPrefHeight());
        delHandler = getDelHandler();
        delIcon.setFontSize(fontsize.get());
        fontsize.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                delIcon.setFontSize(t1.doubleValue());
            }
        });
        delIcon.setPrefSize(this.getPrefHeight(), this.getPrefHeight());
        delBtn.setGraphic(delIcon);
        delBtn.setOnAction(delHandler);
        anchorPane.getChildren().add(content);
        anchorPane.getChildren().add(delBtn);
        content.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.equals("localhost")) {
                    delBtn.setDisable(true);
                } else {
                    delBtn.setDisable(false);
                }
            }
        });
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(delBtn, 0.0);
        setGraphic(anchorPane);
    }

    /*---Methods---*/
    @Override
    protected void updateItem(String address, boolean empty) {
        super.updateItem(address, empty);
        setText(null);
        if (address != null && !empty) {
            content.setText(address);
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
}
