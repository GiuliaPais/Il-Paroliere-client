package uninsubria.client.guicontrollers;

import uninsubria.client.gui.ObservableLobby;

import java.util.ResourceBundle;

/**
 * Controller for the match view.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class MatchController extends AbstractMainController {
    /*---Fields---*/
    private ObservableLobby activeRoom;
    private String[] gridFaces;
    private Integer[] gridNumb;

    /*---Constructors---*/
    /*---Methods---*/
    @Override
    public void setTextResources(ResourceBundle resBundle) {

    }

    public void setActiveRoom(ObservableLobby activeRoom) {
        this.activeRoom = activeRoom;
    }

    public void setGridFaces(String[] gridFaces) {
        this.gridFaces = gridFaces;
    }

    public void setGridNumb(Integer[] gridNumb) {
        this.gridNumb = gridNumb;
    }
}
