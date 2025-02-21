package homer.tastyworld.frontend.statusdisplay.base.tablemanager;

import java.awt.Point;

class TableCursor {

    private final Point cell = new Point();
    private int columns = 0;
    private int rows = 0;

    public TableCursor() {}

    public TableCursor(Point cell) {
        while (!this.cell.equals(cell)) {  // TODO: optimize me
            forward();
        }
    }

    public void forward() {
        if (cell.x == cell.y) {
            columns++;
            cell.move(columns, 0);
        } else if (cell.x == columns) {
            if (cell.y == rows) {
                rows++;
                cell.move(0, rows);
            } else {
                cell.move(cell.x, cell.y + 1);
            }
        } else {
            cell.move(cell.x + 1, cell.y);
        }
    }

    public void back() {
        if (columns == rows) {
            if (cell.x == 0) {
                rows--;
                cell.move(columns, rows);
            } else {
                cell.move(cell.x - 1, cell.y);
            }
        } else {
            if (cell.y == 0) {
                columns--;
                cell.move(columns, rows);
            } else {
                cell.move(cell.x, cell.y - 1);
            }
        }
    }

    public Point cell() {
        return new Point(cell);
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

}
