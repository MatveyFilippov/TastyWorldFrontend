package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import java.util.EnumSet;
import java.util.Set;

public enum TableForOrder {

    COOKING (EnumSet.of(OrderStatus.FORMED, OrderStatus.PREPARING)),
    READY (EnumSet.of(OrderStatus.READY)),
    NOT_IN_TABLE (EnumSet.of(OrderStatus.DRAFT, OrderStatus.COMPLETED));

    public final Set<OrderStatus> statuses;

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
