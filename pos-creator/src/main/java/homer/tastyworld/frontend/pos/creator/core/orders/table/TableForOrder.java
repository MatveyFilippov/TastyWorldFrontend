package homer.tastyworld.frontend.pos.creator.core.orders.table;

import java.util.Set;

public enum TableForOrder {

    COOKING (Set.of("PROCESSING")),
    READY (Set.of("PROCESSED")),
    NOT_IN_TABLE (Set.of());

    public final Set<String> statuses;

    TableForOrder(Set<String> statuses) {
        this.statuses = statuses;
    }

    public static TableForOrder get(String status) {
        for (TableForOrder table : TableForOrder.values()) {
            if (table.statuses.contains(status)) {
                return table;
            }
        }
        return NOT_IN_TABLE;
    }

}
