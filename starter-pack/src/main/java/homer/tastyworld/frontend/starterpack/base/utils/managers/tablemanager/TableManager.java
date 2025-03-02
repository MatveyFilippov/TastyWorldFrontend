package homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager;

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
    private int rows = 0;
    private int columns = 0;

    public TableManager(GridPane table, TableNodeFactory nodeFactory) {
        this.table = table;
        this.nodeFactory = nodeFactory;
    }

    private void extendIfRequired() {
        if (rows < nexFreeCell.rows()) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setPercentHeight(-1);
            table.getRowConstraints().add(row);
            rows++;
        } else if (columns < nexFreeCell.columns()) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            column.setPercentWidth(-1);
            table.getColumnConstraints().add(column);
            columns++;
        }
    }

    private void reduceIfRequired() {
        Point cell = nexFreeCell.cell();
        if (cell.x == 0) {
            table.getRowConstraints().removeLast();
            rows--;
        } else if (cell.y == 0) {
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

    private void shiftBackCellsFromRemoved(Point removed) {  // TODO: optimize me
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
