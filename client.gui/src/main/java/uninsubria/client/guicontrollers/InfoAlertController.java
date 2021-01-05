package uninsubria.client.guicontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

/**
 * Controller for the info alert.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class InfoAlertController extends AbstractCustomAlertController {
    /*---Fields---*/
    @FXML Hyperlink github1, github2,github3;
    @FXML TextFlow textflow;

    private final Text attributions;
    private final String[] urls;

    /*---Constructors---*/
    public InfoAlertController() {
        attributions = new Text("\u2022 FREE MINIMAL ANIMALS : https://caluyadesign.com/free-minimal-animals-svg-cut-file/" + "\n" +
                                "\u2022 GEOMETRIC ANIMALS: https://caluyadesign.com/geometric-animals-svg-png-eps-dxf/" + "\n" +
                                "\u2022 LINEAR ANIMAL SETS: Designed by rawpixel.com / Freepik, https://it.freepik.com/rawpixel-com" + "\n" +
                                "\u2022 WOLF HEAD LOWPOLY: https://www.freevector.com/wolf-head-lowpoly-29798#" + "\n" +
                                "\u2022 BLUE GRADIENT LANDSCAPE BACKGROUND LANDING PAGE: Designed by pikisuperstar / Freepik");
        urls = new String[] {"https://github.com/GiuliaPais",
                             "https://github.com/DavideDiGiovanni",
                             "https://github.com/alessandrolerro"};
    }

    /*---Methods---*/
    @Override
    public void initialize() {
        super.initialize();
        textflow.getChildren().add(attributions);
        github1.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(URI.create(urls[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        github2.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(URI.create(urls[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        github3.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(URI.create(urls[2]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setTextResources(ResourceBundle resBundle) {
    }
}
