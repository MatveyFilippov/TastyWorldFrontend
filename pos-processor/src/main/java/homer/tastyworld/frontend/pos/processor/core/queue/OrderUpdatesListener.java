package homer.tastyworld.frontend.pos.processor.core.queue;

import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.NotFoundStatusCodeException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.sound.SoundManager;
import java.util.Arrays;

class OrderUpdatesListener {

    public static void init() {
        Arrays.stream(OrderUtils.getAllNotCompletedOrders()).forEach(o -> process(o.getOrderID(), o.getStatus(), o.getName(), false));
        Subscriber.subscribe(Theme.ORDER_STATUS_UPGRADED, orderID -> process(Long.parseLong(orderID), true));
    }

    private static void process(long orderID, OrderStatus status, String name, boolean playAlert) {
        switch (status) {
            case FORMED -> handleFormed(orderID, name, playAlert);
            case PREPARING -> handlePreparing(orderID, name);
            default -> handleOthers(orderID);
        }
    }

    public static void process(long orderID, boolean playAlert) {
        OrderStatus status = OrderStatus.COMPLETED;
        String name = "UNKNOWN";

        try {
            Order order = OrderUtils.getOrCreateInstance(orderID);
            status = order.getStatus();
            name = order.getName();
        } catch (NotFoundStatusCodeException ignored) {}

        process(orderID, status, name, playAlert);
    }

    private static void handleFormed(long orderID, String name, boolean playAlert) {
        OrdersScrollQueue.putIfNotExists(orderID, name);
        if (playAlert) {
            SoundManager.playAlert();
        }
    }

    private static void handlePreparing(long orderID, String name) {
        OrdersScrollQueue.putIfNotExists(orderID, name);
        OrdersScrollQueue.setPreparing(orderID);
    }

    private static void handleOthers(long orderID) {
        OrdersScrollQueue.removeIfExists(orderID);
    }

}
