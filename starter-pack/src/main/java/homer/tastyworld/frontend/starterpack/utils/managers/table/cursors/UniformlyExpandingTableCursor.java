package homer.tastyworld.frontend.starterpack.utils.managers.table.cursors;

import homer.tastyworld.frontend.starterpack.utils.managers.table.TableCursor;
import java.awt.Point;

public class UniformlyExpandingTableCursor implements TableCursor {

    private final Point currentCell = new Point();
    private int maxRowsIndex, maxColumnsIndex;

    public UniformlyExpandingTableCursor() {
        maxRowsIndex = 0;
        maxColumnsIndex = 0;
    }

    public UniformlyExpandingTableCursor(Point initialCell) {
        initialCell.setLocation(Math.max(initialCell.x, 0), Math.max(initialCell.y, 0));

        while (!this.currentCell.equals(initialCell)) {  // TODO: optimize me
            this.forward();
        }
    }

    @Override
    public void forward() {
        if (currentCell.x == currentCell.y) {
            maxColumnsIndex++;
            currentCell.move(maxColumnsIndex, 0);
        } else if (currentCell.x == maxColumnsIndex) {
            if (currentCell.y == maxRowsIndex) {
                maxRowsIndex++;
                currentCell.move(0, maxRowsIndex);
            } else {
                currentCell.move(currentCell.x, currentCell.y + 1);
            }
        } else {
            currentCell.move(currentCell.x + 1, currentCell.y);
        }
    }

    @Override
    public void back() {
        if (currentCell.x == 0 && currentCell.y == 0) {
            return;
        }
        if (maxColumnsIndex == maxRowsIndex) {
            if (currentCell.x == 0) {
                maxRowsIndex--;
                currentCell.move(maxColumnsIndex, maxRowsIndex);
            } else {
                currentCell.move(currentCell.x - 1, currentCell.y);
            }
        } else {
            if (currentCell.y == 0) {
                maxColumnsIndex--;
                currentCell.move(maxColumnsIndex, maxRowsIndex);
            } else {
                currentCell.move(currentCell.x, currentCell.y - 1);
            }
        }
    }

    @Override
    public Point getCurrentCell() {
        return new Point(currentCell);
    }

    @Override
    public int getRowsQTY() {
        return maxRowsIndex + 1;
    }

    @Override
    public int getColumnsQTY() {
        return maxColumnsIndex + 1;
    }

    @Override
    public TableCursor copyWith(Point cell) {
        return new UniformlyExpandingTableCursor(cell);
    }

}
