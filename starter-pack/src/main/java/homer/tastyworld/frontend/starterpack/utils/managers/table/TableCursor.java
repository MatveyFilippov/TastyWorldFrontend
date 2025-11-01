package homer.tastyworld.frontend.starterpack.utils.managers.table;

import java.awt.Point;

public interface TableCursor {

    void forward();

    void back();

    Point getCurrentCell();

    int getRowsQTY();

    int getColumnsQTY();

    TableCursor copyWith(Point currentCell);

}
