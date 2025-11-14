package homer.tastyworld.frontend.pos.creator.core.orders.table.listeners;

import homer.tastyworld.frontend.pos.creator.core.orders.printer.OrderWithItemsPrinterPageFactory;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterManager;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class OrderPaidMarkUpdateListener {

    private static final Map<Long, Label> waiters = new HashMap<>();

    static {
        Subscriber.subscribe(Theme.ORDER_PAID, orderID -> process(Long.parseLong(orderID)));
    }

    public static void addWaiter(long orderID, Label waiter) {
        waiters.put(orderID, waiter);
        waiter.getStyleClass().add("created-order-name");
        waiter.getStyleClass().add("");
        if (!OrderUtils.getOrCreateInstance(orderID).isPaid()) {
            waiter.getStyleClass().add("unpaid");
        }
    }

    public static void process(long orderID) {
        if (OrderUtils.getOrCreateInstance(orderID).isPaid()) {
            PrinterManager.print(OrderWithItemsPrinterPageFactory.getFor(orderID));
            Label waiter = waiters.get(orderID);
            if (waiter != null) {
                waiter.getStyleClass().remove("unpaid");
                waiters.remove(orderID);
            }
        }
    }

}
