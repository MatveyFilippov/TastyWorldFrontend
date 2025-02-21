package homer.tastyworld.frontend.statusdisplay.base.tablemanager;

import javafx.scene.Node;
import java.awt.Point;

class TableItem {

    public final String name;
    public final Node node;
    public final Point cell;

    public TableItem(String name, Node node, Point cell) {
        this.name = name;
        this.node = node;
        this.cell = cell;
    }

    public TableItem(String name, TableNodeFactory nodeFactory, Point cell) {
        this(name, nodeFactory.getNode(name), cell);
    }

}
