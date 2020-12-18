package uninsubria.client.customcontrols;

import com.jfoenix.controls.JFXToggleNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DiceFaceDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        HBox hBox = new HBox();
//        DiceFace diceFace1 = new DiceFace("16", "Qu");
//        DiceFace diceFace2 = new DiceFace("1", "A");
//        diceFace2.setSelectable(false);
//        hBox.getChildren().addAll(diceFace1, diceFace2);
//        hBox.getStylesheets().add(getClass().getResource("/css/night_sky.css").toExternalForm());
//        Scene scene = new Scene(hBox, 600, 400);
        String[] faces = new String[] {
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "I", "L", "M", "N",
                "O", "P", "Q", "R"
        };
        String[] dn = new String[] {
                "1", "2", "3", "4",
                "5", "6", "7", "8",
                "9", "10", "11", "12",
                "13", "14", "15", "16"
        };
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStylesheets().add(getClass().getResource("/css/night_sky.css").toExternalForm());
        anchorPane.setPrefSize(400, 400);
        GameGrid gameGrid = new GameGrid(faces, dn);
        anchorPane.getChildren().add(gameGrid);
        AnchorPane.setLeftAnchor(gameGrid, 0.0);
        AnchorPane.setRightAnchor(gameGrid, 0.0);
        AnchorPane.setTopAnchor(gameGrid, 0.0);
        AnchorPane.setBottomAnchor(gameGrid, 0.0);
        Scene scene = new Scene(anchorPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
