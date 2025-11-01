package homer.tastyworld.frontend.pos.creator.core.orders.table.listeners;

import homer.tastyworld.frontend.pos.creator.core.orders.table.TableForOrder;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.sound.SoundManager;
import homer.tastyworld.frontend.starterpack.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import java.util.Arrays;

public class OrderStatusUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderStatusUpdatesListener.cooking = cooking;
        OrderStatusUpdatesListener.ready = ready;
        Arrays.stream(OrderUtils.getAllNotCompletedOrders()).forEach(o -> process(o, false));
        Subscriber.subscribe(Theme.ORDER_STATUS_UPGRADED, orderID -> process(Long.parseLong(orderID), true));
    }

    private static void route(TableForOrder table, long orderID, String name, boolean notifyIfReady) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(orderID);
            ready.remove(orderID);
        } else if (table == TableForOrder.COOKING) {
            cooking.put(orderID, name);
        } else if (table == TableForOrder.READY) {
            if (notifyIfReady) {
                AlertWindows.showInfo("Заказ %s готов".formatted(name), "", false);
                SoundManager.playAlert();
            }
            cooking.remove(orderID);
            ready.put(orderID, name);
        }
    }

    private static void process(Order order, boolean notifyIfReady) {
        TableForOrder table = TableForOrder.getFor(order.getStatus());
        route(table, order.getOrderID(), order.getName(), notifyIfReady);
    }

    private static void process(long orderID, boolean notifyIfReady) {
        Order order;
        try {
            order = OrderUtils.getOrCreateInstance(orderID);
        } catch (UnexpectedResponseStatusCodeException ignored) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null, false);
            return;
        }
        process(order, notifyIfReady);
    }

}
