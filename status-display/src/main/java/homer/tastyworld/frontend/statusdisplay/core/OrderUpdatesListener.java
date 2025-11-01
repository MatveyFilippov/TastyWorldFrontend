package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.NotFoundStatusCodeException;
import homer.tastyworld.frontend.starterpack.utils.managers.table.TableManager;
import java.util.Arrays;

public class OrderUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderUpdatesListener.cooking = cooking;
        OrderUpdatesListener.ready = ready;
        Arrays.stream(OrderUtils.getAllNotCompletedOrders()).forEach(OrderUpdatesListener::process);
        Subscriber.subscribe(Theme.ORDER_STATUS_UPGRADED, orderID -> process(Long.parseLong(orderID)));
    }

    private static void route(TableForOrder table, long orderID, String name) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(orderID);
            ready.remove(orderID);
        } else if (table == TableForOrder.COOKING) {
            cooking.put(orderID, name);
        } else if (table == TableForOrder.READY) {
            cooking.remove(orderID);
            ready.put(orderID, name);
        }
    }

    private static void process(Order order) {
        TableForOrder table = TableForOrder.getFor(order.getStatus());
        route(table, order.getOrderID(), order.getName());
    }

    private static void process(long orderID) {
        Order order;
        try {
            order = Order.get(orderID);
        } catch (NotFoundStatusCodeException ignored) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null);
            return;
        }
        process(order);
    }

}
