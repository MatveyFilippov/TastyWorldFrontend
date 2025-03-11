package homer.tastyworld.frontend.starterpack.base.utils.managers.table;

import javafx.scene.Node;
import java.awt.Point;

class TableItem {

    public final long id;
    public final String name;
    public final Node node;
    public final Point cell;

    public TableItem(long id, String name, Node node, Point cell) {
        this.id = id;
        this.name = name;
        this.node = node;
        this.cell = cell;
    }

    public TableItem(long id, String name, TableNodeFactory nodeFactory, Point cell) {
        this(id, name, nodeFactory.getNode(id, name), cell);
    }

}
