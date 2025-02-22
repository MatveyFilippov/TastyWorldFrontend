package homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager;

import javafx.scene.Node;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

class TableItemCollection {

    private final Map<Long, TableItem> fromID = new HashMap<>();
    private final Map<String, TableItem> fromName = new HashMap<>();
    private final Map<Point, TableItem> fromCell = new HashMap<>();
    private final Map<Node, TableItem> fromNode = new HashMap<>();
    private final Map<Integer, Point> cells = new HashMap<>();

    private Point getCell(Point point) {
        int index = (point.x + point.y) * (point.x + point.y + 1) / 2 + point.y;
        return cells.computeIfAbsent(index, ignored -> new Point(point));
    }

    public void put(TableItem item) {
        fromID.put(item.id, item);
        fromName.put(item.name, item);
        fromCell.put(getCell(item.cell), item);
        fromNode.put(item.node, item);
    }

    public TableItem get(long id) {
        return fromID.get(id);
    }

    public TableItem get(String name) {
        return fromName.get(name);
    }

    public TableItem get(Point cell) {
        return fromCell.get(getCell(cell));
    }

    public TableItem get(Node node) {
        return fromNode.get(node);
    }

    public void updateCell(Point oldCell, Point newCell) {
        TableItem item = get(oldCell);
        remove(item);
        item.cell.setLocation(newCell);
        put(item);

    }

    public void remove(TableItem item) {
        fromID.remove(item.id);
        fromName.remove(item.name);
        fromCell.remove(getCell(item.cell));
        fromNode.remove(item.node);
    }

}
