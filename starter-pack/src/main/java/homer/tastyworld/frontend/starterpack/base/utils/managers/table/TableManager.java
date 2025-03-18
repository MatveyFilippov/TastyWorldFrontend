package homer.tastyworld.frontend.starterpack.base.utils.managers.table;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import java.awt.Point;

public class TableManager {

    private final GridPane table;
    private final TableCursor nexFreeCell;
    private final TableItemCollection items = new TableItemCollection();
    private final TableNodeFactory nodeFactory;
    private int rows = 1;
    private int columns = 1;

    public TableManager(GridPane table, TableCursor tableCursor, TableNodeFactory nodeFactory) {
        this.table = table;
        this.nexFreeCell = tableCursor;
        this.nodeFactory = nodeFactory;
        extendIfRequired();
    }

    private void extendIfRequired() {
        while (rows < nexFreeCell.rows()) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setValignment(VPos.CENTER);
            row.setFillHeight(true);
            table.getRowConstraints().add(row);
            rows++;
            final int percent = 100 / rows;
            table.getRowConstraints().forEach(r -> r.setPercentHeight(percent));
        }
        while (columns < nexFreeCell.columns()) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            column.setHalignment(HPos.CENTER);
            column.setFillWidth(true);
            table.getColumnConstraints().add(column);
            columns++;
            final int percent = 100 / columns;
            table.getColumnConstraints().forEach(c -> c.setPercentWidth(percent));
        }
    }

    private void reduceIfRequired() {
        TableCursor actual = nexFreeCell.copyFrom(nexFreeCell.cell());
        actual.back();
        while (rows > actual.rows()) {
            table.getRowConstraints().removeLast();
            rows--;
            final int percent = 100 / rows;
            table.getRowConstraints().forEach(r -> r.setPercentHeight(percent));
        }
        while (columns > actual.columns()) {
            table.getColumnConstraints().removeLast();
            columns--;
            final int percent = 100 / columns;
            table.getColumnConstraints().forEach(c -> c.setPercentWidth(percent));
        }
    }

    public void put(long id, String name) {
        if (items.isExists(id) || items.isExists(name)) {
            return;
        }
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
        if (items.isExists(id)) {
            remove(items.get(id));
        }
    }

    public void remove(String name) {
        if (items.isExists(name)) {
            remove(items.get(name));
        }
    }

    private void shiftBackCellsFromRemoved(Point removed) {
        TableCursor to = nexFreeCell.copyFrom(removed);
        TableCursor from = nexFreeCell.copyFrom(removed);
        TableItem itemToMove;
        while (true) {
            from.forward();
            itemToMove = items.get(from.cell());
            if (itemToMove == null) {
                return;
            }
            table.getChildren().remove(itemToMove.node);
            Point newCell = to.cell();
            table.add(itemToMove.node, newCell.x, newCell.y);
            items.updateCell(itemToMove.cell, newCell);
            to.forward();
        }
    }

}
