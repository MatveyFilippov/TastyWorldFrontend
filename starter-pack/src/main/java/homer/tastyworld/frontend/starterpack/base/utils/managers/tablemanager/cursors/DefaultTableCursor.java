package homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.cursors;

import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableCursor;
import java.awt.Point;

public class DefaultTableCursor extends TableCursor {

    private final int initAddColumnAfterRow;
    private final int initAddRowAfterColumn;
    private boolean extentColumnIfTrueElseRow = true;

    public DefaultTableCursor(int addColumnAfterRow, int addRowAfterColumn) {
        this.rows = addColumnAfterRow - 1;
        this.columns = addRowAfterColumn - 1;
        this.initAddColumnAfterRow = addColumnAfterRow;
        this.initAddRowAfterColumn = addRowAfterColumn;
    }

    public DefaultTableCursor(Point cell, int addColumnAfterRow, int addRowAfterColumn) {
        this(addColumnAfterRow, addRowAfterColumn);
        cell.setLocation(Math.max(cell.x, 0), Math.max(cell.y, 0));
        while (!this.cell.equals(cell)) {  // TODO: optimize me
            forward();
        }
    }

    @Override
    public void forward() {
        if (cell.x == columns && cell.y == rows) {
            if (extentColumnIfTrueElseRow) {
                columns++;
                cell.move(columns, 0);
            } else {
                rows++;
                cell.move(0, rows);
            }
            extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
        } else if (cell.y == rows) {
            if (rows >= initAddColumnAfterRow && extentColumnIfTrueElseRow) {
                cell.move(cell.x + 1, cell.y);
            } else {
                cell.move(cell.x + 1, 0);
            }
        } else {
            cell.move(cell.x, cell.y + 1);
        }
    }

    @Override
    public void back() {  // TODO: optimize me
        if (cell.x == 0 && cell.y == 0) {
            return;
        }
        if (cell.x == columns && cell.y == rows) {
            if (columns < initAddRowAfterColumn) {
                if (cell.y > 0) {
                    cell.move(cell.x, cell.y - 1);
                } else {
                    cell.move(cell.x - 1, rows);
                }
            } else {
                if (extentColumnIfTrueElseRow) {
                    if (cell.x > 0) {
                        cell.move(cell.x - 1, cell.y);
                    } else {
                        rows--;
                        cell.move(columns, rows);
                        extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    }
                } else {
                    if (cell.y > 0) {
                        cell.move(cell.x, cell.y - 1);
                    } else {
                        columns--;
                        cell.move(columns, rows);
                        extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    }
                }
            }
        } else if (cell.x == columns) {
            if (cell.y > 0) {
                cell.move(cell.x, cell.y - 1);
            } else {
                if (columns >= initAddRowAfterColumn) {
                    columns--;
                    extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                }
                cell.move(cell.x - 1, rows);
            }
        } else if (cell.y == rows) {
            if (cell.x > 0 && rows >= initAddColumnAfterRow) {
                cell.move(cell.x - 1, cell.y);
            } else {
                if (rows >= initAddColumnAfterRow) {
                    rows--;
                    extentColumnIfTrueElseRow = !extentColumnIfTrueElseRow;
                    cell.move(columns, cell.y - 1);
                } else {
                    cell.move(cell.x, cell.y - 1);
                }
            }
        } else {
            if (cell.y > 0) {
                cell.move(cell.x, cell.y - 1);
            } else {
                cell.move(cell.x - 1, rows);
            }
        }
    }

    @Override
    public TableCursor copyFrom(Point cell) {
        return new DefaultTableCursor(cell, initAddColumnAfterRow, initAddRowAfterColumn);
    }

}
