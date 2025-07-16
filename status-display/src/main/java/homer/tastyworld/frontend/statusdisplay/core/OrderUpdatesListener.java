package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.order.Order;
import java.util.Arrays;

public class OrderUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderUpdatesListener.cooking = cooking;
        OrderUpdatesListener.ready = ready;
        Arrays.stream(ClientPoint.getActiveOrderIDs()).forEach(OrderUpdatesListener::process);
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID)));
    }

    private static void route(TableForOrder table, long orderID, String name) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(name);
            ready.remove(name);
        } else if (table == TableForOrder.COOKING) {
            cooking.put(orderID, name);
        } else if (table == TableForOrder.READY) {
            cooking.remove(name);
            ready.put(orderID, name);
        }
    }

    public static void process(long orderID) {
        Order order;
        try {
            order = Order.get(orderID);
        } catch (BadRequestException ex) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null);
            return;
        }
        route(TableForOrder.get(order.getStatus()), orderID, order.name);
    }

}
