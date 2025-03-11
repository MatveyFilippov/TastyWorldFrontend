package homer.tastyworld.frontend.starterpack.base.utils.managers.table.cursors;

import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableCursor;
import java.awt.Point;

public class SelfExtendedTableCursor extends TableCursor {

    public SelfExtendedTableCursor() {}

    public SelfExtendedTableCursor(Point cell) {
        cell.setLocation(Math.max(cell.x, 0), Math.max(cell.y, 0));
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
        if (cell.x == 0 && cell.y == 0) {
            return;
        }
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

    @Override
    public TableCursor copyFrom(Point cell) {
        return new SelfExtendedTableCursor(cell);
    }

}
