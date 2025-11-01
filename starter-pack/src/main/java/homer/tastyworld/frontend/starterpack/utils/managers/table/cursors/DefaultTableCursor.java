package homer.tastyworld.frontend.starterpack.utils.managers.table.cursors;

import homer.tastyworld.frontend.starterpack.utils.managers.table.TableCursor;
import java.awt.Point;

public class DefaultTableCursor implements TableCursor {

    private final Point currentCell = new Point();
    private int maxRowsIndex, maxColumnsIndex;
    private final int minimumRowsQTY, minimumColumnsQTY;
    private boolean extentColumnIfTrueElseRow = true;

    public DefaultTableCursor(int minimumRowsQTY, int minimumColumnsQTY) {
        this.maxRowsIndex = minimumRowsQTY - 1;
        this.maxColumnsIndex = minimumColumnsQTY - 1;
        this.minimumRowsQTY = minimumRowsQTY;
        this.minimumColumnsQTY = minimumColumnsQTY;
    }

    public DefaultTableCursor(int minimumRowsQTY, int minimumColumnsQTY, Point initialCell) {
        this(minimumRowsQTY, minimumColumnsQTY);

        initialCell.setLocation(Math.max(initialCell.x, 0), Math.max(initialCell.y, 0));

        while (!this.currentCell.equals(initialCell)) {  // TODO: optimize me
            this.forward();
        }
    }

    @Override
    public void forward() {
        if (currentCell.x == maxColumnsIndex && currentCell.y == maxRowsIndex) {
            if (extentColumnIfTrueElseRow) {
                maxColumnsIndex++;
                currentCell.move(maxColumnsIndex, 0);
            } else {
                maxRowsIndex++;
                currentCell.move(0, maxRowsIndex);
            }
            extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
        } else if (currentCell.y == maxRowsIndex) {
            if (maxRowsIndex >= minimumRowsQTY && extentColumnIfTrueElseRow) {
                currentCell.move(currentCell.x + 1, currentCell.y);
            } else {
                currentCell.move(currentCell.x + 1, 0);
            }
        } else {
            currentCell.move(currentCell.x, currentCell.y + 1);
        }
    }

    @Override
    public void back() {  // TODO: optimize me
        if (currentCell.x == 0 && currentCell.y == 0) {
            return;
        }
        if (currentCell.x == maxColumnsIndex && currentCell.y == maxRowsIndex) {
            if (maxColumnsIndex < minimumColumnsQTY) {
                if (currentCell.y > 0) {
                    currentCell.move(currentCell.x, currentCell.y - 1);
                } else {
                    currentCell.move(currentCell.x - 1, maxRowsIndex);
                }
            } else {
                if (extentColumnIfTrueElseRow) {
                    if (currentCell.x > 0) {
                        currentCell.move(currentCell.x - 1, currentCell.y);
                    } else {
                        maxRowsIndex--;
                        currentCell.move(maxColumnsIndex, maxRowsIndex);
                        extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    }
                } else {
                    if (currentCell.y > 0) {
                        currentCell.move(currentCell.x, currentCell.y - 1);
                    } else {
                        maxColumnsIndex--;
                        currentCell.move(maxColumnsIndex, maxRowsIndex);
                        extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    }
                }
            }
        } else if (currentCell.x == maxColumnsIndex) {
            if (currentCell.y > 0) {
                currentCell.move(currentCell.x, currentCell.y - 1);
            } else {
                if (maxColumnsIndex >= minimumColumnsQTY) {
                    maxColumnsIndex--;
                    extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                }
                currentCell.move(currentCell.x - 1, maxRowsIndex);
            }
        } else if (currentCell.y == maxRowsIndex) {
            if (currentCell.x > 0 && maxRowsIndex >= minimumRowsQTY) {
                currentCell.move(currentCell.x - 1, currentCell.y);
            } else {
                if (maxRowsIndex >= minimumRowsQTY) {
                    maxRowsIndex--;
                    extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    currentCell.move(maxColumnsIndex, currentCell.y - 1);
                } else {
                    currentCell.move(currentCell.x, currentCell.y - 1);
                }
            }
        } else {
            if (currentCell.y > 0) {
                currentCell.move(currentCell.x, currentCell.y - 1);
            } else {
                currentCell.move(currentCell.x - 1, maxRowsIndex);
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
    public TableCursor copyWith(Point currentCell) {
        return new DefaultTableCursor(this.minimumRowsQTY, this.minimumColumnsQTY, currentCell);
    }

}
