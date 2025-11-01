package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import java.util.EnumSet;

public enum TableForOrder {

    COOKING (EnumSet.of(OrderStatus.FORMED, OrderStatus.PREPARING)),
    READY (EnumSet.of(OrderStatus.READY)),
    NOT_IN_TABLE (EnumSet.of(OrderStatus.DRAFT, OrderStatus.COMPLETED));

    private final EnumSet<OrderStatus> statuses;

    TableForOrder(EnumSet<OrderStatus> statuses) { this.statuses = statuses; }

    public static TableForOrder getFor(OrderStatus status) {
        for (TableForOrder table : TableForOrder.values()) {
            if (table.statuses.contains(status)) {
                return table;
            }
        }
        return NOT_IN_TABLE;
    }

}
