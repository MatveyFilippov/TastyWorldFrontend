package homer.tastyworld.frontend.poscreator.core.orders.table;

public enum TableForOrder {

    COOKING,
    READY,
    NOT_IN_TABLE;

    public static TableForOrder get(String status) {
        if (status == null) {
            return NOT_IN_TABLE;
        } else if (status.equals("CREATED") || status.equals("PROCESSING")) {
            return COOKING;
        } else if (status.equals("PROCESSED")) {
            return READY;
        }
        return NOT_IN_TABLE;
    }

}
