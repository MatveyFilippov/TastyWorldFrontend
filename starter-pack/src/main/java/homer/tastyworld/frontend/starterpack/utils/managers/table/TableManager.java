package homer.tastyworld.frontend.starterpack.utils.managers.table;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import java.awt.Point;

public class TableManager {

    private final GridPane table;
    private final TableCursor nexFreeCell;
    private final TableItems items = new TableItems();
    private final TableNodeFactory nodeFactory;
    private int rows = 1;
    private int columns = 1;

    public TableManager(GridPane table, TableCursor cursor, TableNodeFactory nodeFactory) {
        this.table = table;
        this.nexFreeCell = cursor;
        this.nodeFactory = nodeFactory;
        extendIfRequired();
    }

    private void extendIfRequired() {
        while (rows < nexFreeCell.getRowsQTY()) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setValignment(VPos.CENTER);
            row.setFillHeight(true);
            table.getRowConstraints().add(row);
            rows++;
            final int percent = 100 / rows;
            table.getRowConstraints().forEach(r -> r.setPercentHeight(percent));
        }
        while (columns < nexFreeCell.getColumnsQTY()) {
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
        TableCursor actual = nexFreeCell.copyWith(nexFreeCell.getCurrentCell());
        actual.back();
        while (rows > actual.getRowsQTY()) {
            table.getRowConstraints().removeLast();
            rows--;
            final int percent = 100 / rows;
            table.getRowConstraints().forEach(r -> r.setPercentHeight(percent));
        }
        while (columns > actual.getColumnsQTY()) {
            table.getColumnConstraints().removeLast();
            columns--;
            final int percent = 100 / columns;
            table.getColumnConstraints().forEach(c -> c.setPercentWidth(percent));
        }
    }

    private void shiftBackCellsFromRemoved(Point removed) {
        TableCursor to = nexFreeCell.copyWith(removed);
        TableCursor from = nexFreeCell.copyWith(removed);
        TableItems.Item itemToMove;
        while (true) {
            from.forward();
            itemToMove = items.get(from.getCurrentCell());
            if (itemToMove == null) {
                return;
            }
            table.getChildren().remove(itemToMove.node());
            Point newCell = to.getCurrentCell();
            table.add(itemToMove.node(), newCell.x, newCell.y);
            items.updateCell(itemToMove.cell(), newCell);
            to.forward();
        }
    }

    private void put(TableItems.Item item) {
        extendIfRequired();
        table.add(item.node(), item.cell().x, item.cell().y);
        items.put(item);
        nexFreeCell.forward();
    }

    private void remove(TableItems.Item item) {
        table.getChildren().remove(item.node());
        items.remove(item);
        shiftBackCellsFromRemoved(item.cell());
        nexFreeCell.back();
        reduceIfRequired();
    }

    public final void put(long id, String name) {
        if (items.isExists(id) || items.isExists(name)) {
            return;
        }
        TableItems.Item item = new TableItems.Item(id, name, nodeFactory, nexFreeCell.getCurrentCell());
        put(item);
    }

    public final void remove(long id) {
        if (items.isExists(id)) {
            remove(items.get(id));
        }
    }

    public final void remove(String name) {
        if (items.isExists(name)) {
            remove(items.get(name));
        }
    }

}
