package homer.tastyworld.frontend.pos.creator.core.orders.table.listeners;

import homer.tastyworld.frontend.pos.creator.core.orders.printer.OrderPageFactory;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterManager;
import homer.tastyworld.frontend.starterpack.order.Order;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class OrderPaidMarkUpdateListener {

    public static class MarkColors {

        public static final Color UNPAID = Color.web("#FF4040");
        public static final Color PAID = Color.BLACK;

    }

    private static final Map<Long, Label> waiters = new HashMap<>();

    static {
        Subscriber.subscribe(Theme.ORDER_PAID_MARKED, orderID -> process(Long.parseLong(orderID)));
    }

    public static void addWaiter(long orderID, Label waiter) {
        waiters.put(orderID, waiter);
        process(orderID);
    }

    public static void process(long orderID) {
        Label waiter = waiters.get(orderID);
        if (waiter != null && Order.get(orderID).isPaid()) {
            PrinterManager.print(OrderPageFactory.getFor(orderID));
            waiter.setTextFill(MarkColors.PAID);
            waiters.remove(orderID);
        }
    }

}
