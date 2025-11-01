package homer.tastyworld.frontend.starterpack.utils.managers.table;

import javafx.scene.Node;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

class TableItems {

    record Item(
            long id,
            String name,
            Node node,
            Point cell
    ) {

        public Item(long id, String name, TableNodeFactory nodeFactory, Point cell) {
            this(id, name, nodeFactory.getNode(id, name), cell);
        }

    }

    private final Map<Long, Item> fromID = new HashMap<>();
    private final Map<String, Item> fromName = new HashMap<>();
    private final Map<Point, Item> fromCell = new HashMap<>();
    private final Map<Node, Item> fromNode = new HashMap<>();
    private final Map<Integer, Point> cells = new HashMap<>();

    private Point getCell(Point point) {
        int index = (point.x + point.y) * (point.x + point.y + 1) / 2 + point.y;
        return cells.computeIfAbsent(index, ignored -> new Point(point));
    }

    public void put(Item item) {
        fromID.put(item.id(), item);
        fromName.put(item.name(), item);
        fromCell.put(getCell(item.cell()), item);
        fromNode.put(item.node(), item);
    }

    public boolean isExists(long id) {
        return fromID.containsKey(id);
    }

    public boolean isExists(String name) {
        return fromName.containsKey(name);
    }

    public Item get(long id) {
        return fromID.get(id);
    }

    public Item get(String name) {
        return fromName.get(name);
    }

    public Item get(Point cell) {
        return fromCell.get(getCell(cell));
    }

    public Item get(Node node) {
        return fromNode.get(node);
    }

    public void updateCell(Point oldCell, Point newCell) {
        Item item = get(oldCell);
        remove(item);
        item.cell().setLocation(newCell);
        put(item);

    }

    public void remove(Item item) {
        fromID.remove(item.id());
        fromName.remove(item.name());
        fromCell.remove(getCell(item.cell()));
        fromNode.remove(item.node());
    }

}
