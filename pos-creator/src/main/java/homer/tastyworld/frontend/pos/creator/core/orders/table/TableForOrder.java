package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.starterpack.entity.misc.OrderStatus;
import java.util.Set;

public enum TableForOrder {

    COOKING (Set.of(OrderStatus.CREATED, OrderStatus.PROCESSING)),
    READY (Set.of(OrderStatus.PROCESSED)),
    NOT_IN_TABLE (Set.of(OrderStatus.CREATING, OrderStatus.PROCESSED));

    public final Set<OrderStatus> statuses;

    TableForOrder(Set<OrderStatus> statuses) { this.statuses = statuses; }

    public static TableForOrder get(OrderStatus status) {
        for (TableForOrder table : TableForOrder.values()) {
            if (table.statuses.contains(status)) {
                return table;
            }
        }
        return NOT_IN_TABLE;
    }

}
