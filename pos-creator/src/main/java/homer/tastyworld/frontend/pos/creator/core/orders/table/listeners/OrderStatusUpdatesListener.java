package homer.tastyworld.frontend.pos.creator.core.orders.table.listeners;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.orders.table.TableForOrder;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.managers.sound.SoundManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.order.Order;
import java.util.Arrays;

public class OrderStatusUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderStatusUpdatesListener.cooking = cooking;
        OrderStatusUpdatesListener.ready = ready;
        Arrays.stream(ClientPoint.getActiveOrderIDs()).forEach(orderID -> process(orderID, false));
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID), true));
    }

    private static void route(TableForOrder table, long orderID, String name, boolean notifyIfReady) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(orderID);
            ready.remove(orderID);
        } else if (table == TableForOrder.COOKING) {
            cooking.put(orderID, name);
        } else if (table == TableForOrder.READY) {
            if (notifyIfReady) {
                AlertWindow.showInfo("Заказ %s готов".formatted(name), "", false);
                SoundManager.playAlert();
            }
            cooking.remove(orderID);
            ready.put(orderID, name);
        }
    }

    public static void process(long orderID, boolean notifyIfReady) {
        Order order;
        try {
            order = Order.get(orderID);
        } catch (BadRequestException ex) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null, false);
            return;
        }
        route(TableForOrder.get(order.getStatus()), orderID, order.name, notifyIfReady);
    }

}
