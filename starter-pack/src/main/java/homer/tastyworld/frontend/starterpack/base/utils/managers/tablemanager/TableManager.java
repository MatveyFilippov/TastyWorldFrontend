package homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import java.awt.Point;

public class TableManager {

    private final GridPane table;
    private final TableCursor nexFreeCell = new TableCursor();
    private final TableItemCollection items = new TableItemCollection();
    private final TableNodeFactory nodeFactory;
    private final int minimumRows;
    private final int minimumColumns;
    private int rows = 1;
    private int columns = 1;

    public TableManager(GridPane table, TableNodeFactory nodeFactory, int minimumRows, int minimumColumns) {
        this.table = table;
        this.nodeFactory = nodeFactory;
        this.minimumRows = minimumRows;
        this.minimumColumns = minimumColumns;
        while (rows < minimumRows || columns < minimumColumns) {
            extendIfRequired();
        }
    }

    private void extendIfRequired() {
        if (rows < nexFreeCell.rows() || rows < minimumRows) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setPercentHeight(-1);
            row.setValignment(VPos.CENTER);
            row.setFillHeight(true);
            table.getRowConstraints().add(row);
            rows++;
        }
        if (columns < nexFreeCell.columns() || columns < minimumColumns) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            column.setPercentWidth(-1);
            column.setHalignment(HPos.CENTER);
            column.setFillWidth(true);
            table.getColumnConstraints().add(column);
            columns++;
        }
    }

    private void reduceIfRequired() {
        if (rows >= nexFreeCell.rows() && rows > minimumRows) {
            table.getRowConstraints().removeLast();
            rows--;
        }
        if (columns >= nexFreeCell.columns() && columns > minimumColumns) {
            table.getColumnConstraints().removeLast();
            columns--;
        }
    }

    public void append(long id, String name) {
        extendIfRequired();
        TableItem item = new TableItem(id, name, nodeFactory, nexFreeCell.cell());
        table.add(item.node, item.cell.x, item.cell.y);
        items.put(item);
        nexFreeCell.forward();
    }

    private void remove(TableItem item) {
        table.getChildren().remove(item.node);
        items.remove(item);
        shiftBackCellsFromRemoved(item.cell);
        nexFreeCell.back();
        reduceIfRequired();
    }

    public void remove(long id) {
        TableItem item = items.get(id);
        if (item == null) {
            return;
        }
        remove(item);
    }

    public void remove(String name) {
        TableItem item = items.get(name);
        if (item == null) {
            return;
        }
        remove(item);
    }

    private void shiftBackCellsFromRemoved(Point removed) {
        TableCursor to = new TableCursor(removed);
        TableCursor from = new TableCursor(removed);
        TableItem itemToMove;
        while (true) {
            from.forward();
            itemToMove = items.get(from.cell());
            if (itemToMove == null) {
                return;
            }
            table.getChildren().remove(itemToMove.node);
            Point temp = to.cell();
            table.add(itemToMove.node, temp.x, temp.y);
            items.updateCell(itemToMove.cell, temp);
            to.forward();
        }
    }

}
