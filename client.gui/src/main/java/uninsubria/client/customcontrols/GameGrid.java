package uninsubria.client.customcontrols;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class GameGrid extends GridPane {
    /*---Fields---*/
    private static final String DEFAULT_STYLE_CLASS = "ip-game-grid";

    public class GridIndex {
        private final int rowIndex;
        private final int columnIndex;

        public GridIndex(int rowIndex, int columnIndex) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridIndex gridIndex = (GridIndex) o;
            return rowIndex == gridIndex.rowIndex &&
                    columnIndex == gridIndex.columnIndex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rowIndex, columnIndex);
        }

        @Override
        public String toString() {
            return "GridIndex{" +
                    "rowIndex=" + rowIndex +
                    ", columnIndex=" + columnIndex +
                    '}';
        }
    }

    private final HashMap<GridIndex, DiceFace> dicePosition;
    private final HashMap<GridIndex, List<GridIndex>> adjacentNodes;
    private ConcurrentLinkedDeque<DiceFace> selectedDices;
    private int last_selected_index = 0;

    /*---Constructors---*/
    public GameGrid() {
        super();
        this.dicePosition = new HashMap<>();
        this.adjacentNodes = new HashMap<>();
        this.selectedDices = new ConcurrentLinkedDeque<>();
        initialize();
    }

    public GameGrid(String[] faces, String[] diceNumbers) {
        this();
        assert faces.length == 16 & diceNumbers.length == 16;
        for (int i = 0; i < 16; i++) {
            int rowIndex = i/4;
            int columnIndex = i % 4;
            GridIndex index = new GridIndex(rowIndex, columnIndex);
            String face = faces[i];
            String diceN = diceNumbers[i];
            DiceFace df = dicePosition.get(index);
            df.setDiceFace(face);
            df.setDiceNumber(diceN);
        }
    }

    /*---Methods---*/
    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(25);
            column.setHalignment(HPos.CENTER);
            column.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(column);
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(25);
            row.setValignment(VPos.CENTER);
            row.setVgrow(Priority.ALWAYS);
            this.getRowConstraints().add(row);
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                AnchorPane anchorPane = new AnchorPane();
                DiceFace dice = new DiceFace();
                anchorPane.getChildren().add(dice);
                AnchorPane.setBottomAnchor(dice, 5.0);
                AnchorPane.setTopAnchor(dice, 5.0);
                AnchorPane.setRightAnchor(dice, 5.0);
                AnchorPane.setLeftAnchor(dice, 5.0);
                setRowIndex(anchorPane, i);
                setColumnIndex(anchorPane, j);
                getChildren().add(anchorPane);
                GridIndex index = new GridIndex(i, j);
                dice.setIndexInGrid(index);
                dicePosition.put(index, dice);
                List<GridIndex> adjacent = new ArrayList<>();
                switch(i) {
                     case 0 -> {
                         switch(j) {
                             case 0 -> {
                                 GridIndex a1 = new GridIndex(0, 1);
                                 GridIndex a2 = new GridIndex(1, 1);
                                 GridIndex a3 = new GridIndex(1, 0);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacentNodes.put(index, adjacent);
                             }
                             case 1, 2 -> {
                                 GridIndex a1 = new GridIndex(i, j-1);
                                 GridIndex a2 = new GridIndex(i+1, j-1);
                                 GridIndex a3 = new GridIndex(i+1, j);
                                 GridIndex a4 = new GridIndex(i+1, j+1);
                                 GridIndex a5 = new GridIndex(i, j+1);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacent.add(a4);
                                 adjacent.add(a5);
                                 adjacentNodes.put(index, adjacent);
                             }
                             case 3 -> {
                                 GridIndex a1 = new GridIndex(0, 2);
                                 GridIndex a2 = new GridIndex(1, 2);
                                 GridIndex a3 = new GridIndex(1, 3);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacentNodes.put(index, adjacent);
                             }
                         }
                     }
                     case 1, 2 -> {
                         switch (j) {
                             case 0 -> {
                                 GridIndex a1 = new GridIndex(i-1, j);
                                 GridIndex a2 = new GridIndex(i-1, j+1);
                                 GridIndex a3 = new GridIndex(i, j+1);
                                 GridIndex a4 = new GridIndex(i+1, j+1);
                                 GridIndex a5 = new GridIndex(i+1, j);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacent.add(a4);
                                 adjacent.add(a5);
                                 adjacentNodes.put(index, adjacent);
                             }
                             case 1, 2 -> {
                                 GridIndex a1 = new GridIndex(i-1, j-1);
                                 GridIndex a2 = new GridIndex(i-1, j);
                                 GridIndex a3 = new GridIndex(i-1, j+1);
                                 GridIndex a4 = new GridIndex(i, j+1);
                                 GridIndex a5 = new GridIndex(i+1, j+1);
                                 GridIndex a6 = new GridIndex(i+1, j);
                                 GridIndex a7 = new GridIndex(i+1, j-1);
                                 GridIndex a8 = new GridIndex(i, j-1);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacent.add(a4);
                                 adjacent.add(a5);
                                 adjacent.add(a6);
                                 adjacent.add(a7);
                                 adjacent.add(a8);
                                 adjacentNodes.put(index, adjacent);
                             }
                             case 3 -> {
                                 GridIndex a1 = new GridIndex(i-1, j);
                                 GridIndex a2 = new GridIndex(i-1, j-1);
                                 GridIndex a3 = new GridIndex(i, j-1);
                                 GridIndex a4 = new GridIndex(i+1, j-1);
                                 GridIndex a5 = new GridIndex(i+1, j);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacent.add(a4);
                                 adjacent.add(a5);
                                 adjacentNodes.put(index, adjacent);
                             }
                         }
                     }
                     case 3 -> {
                         switch(j) {
                             case 0 -> {
                                 GridIndex a1 = new GridIndex(2, 0);
                                 GridIndex a2 = new GridIndex(2, 1);
                                 GridIndex a3 = new GridIndex(3, 1);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacentNodes.put(index, adjacent);
                             }
                             case 1, 2 -> {
                                 GridIndex a1 = new GridIndex(i, j-1);
                                 GridIndex a2 = new GridIndex(i-1, j-1);
                                 GridIndex a3 = new GridIndex(i-1, j);
                                 GridIndex a4 = new GridIndex(i-1, j+1);
                                 GridIndex a5 = new GridIndex(i, j+1);
                                 adjacent.add(a1);
                                 adjacent.add(a2);
                                 adjacent.add(a3);
                                 adjacent.add(a4);
                                 adjacent.add(a5);
                                 adjacentNodes.put(index, adjacent);
                             }
                         }
                     }
                }
                dice.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        if (!selectedDices.isEmpty()) {
                            selectedDices.peekFirst().setDeselectable(false);
                        }
                        selectedDices.addFirst(dice);
                        dice.setSelectedNumber(Integer.toString(++last_selected_index));
                        for (GridIndex ind : adjacentNodes.keySet()) {
                            if (!adjacentNodes.get(dice.getIndexInGrid()).contains(ind) & !ind.equals(dice.getIndexInGrid())) {
                                dicePosition.get(ind).setSelectable(false);
                            } else {
                                dicePosition.get(ind).setSelectable(true);
                            }
                        }
                    } else if (dice.isDeselectable()) {
                        selectedDices.removeFirst();
                        dice.setSelectedNumber("");
                        last_selected_index--;
                        if (!selectedDices.isEmpty()) {
                            DiceFace last = selectedDices.peekFirst();
                            last.setDeselectable(true);
                            for (GridIndex ind : adjacentNodes.keySet()) {
                                if (!adjacentNodes.get(last.getIndexInGrid()).contains(ind) & !ind.equals(last.getIndexInGrid())) {
                                    dicePosition.get(ind).setSelectable(false);
                                } else {
                                    dicePosition.get(ind).setSelectable(true);
                                }
                            }
                        } else {
                            for (GridIndex ind : adjacentNodes.keySet()) {
                                dicePosition.get(ind).setSelectable(true);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        return this.getClass().getResource("/css/ip-game-grid.css").toExternalForm();
    }

    public ConcurrentLinkedDeque<DiceFace> getSelectedDices() {
        return selectedDices;
    }
}
