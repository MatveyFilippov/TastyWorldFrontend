package homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager;

import java.awt.Point;

public abstract class TableCursor {

    protected final Point cell = new Point();
    protected int columns = 0;
    protected int rows = 0;

    public abstract void forward();

    public abstract void back();

    public abstract TableCursor copyFrom(Point cell);

    public Point cell() {
        return new Point(cell);
    }

    public int columns() {
        return columns + 1;
    }

    public int rows() {
        return rows + 1;
    }

}
