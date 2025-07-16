package homer.tastyworld.frontend.pos.processor.core.queue;

import homer.tastyworld.frontend.pos.processor.core.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.order.Order;
import java.util.Arrays;

class OrderUpdatesListener {

    public static void init() {
        Arrays.stream(ClientPoint.getActiveOrderIDs()).forEach(OrderUpdatesListener::process);
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID)));
    }

    public static void process(long orderID) {
        OrderStatus status = OrderStatus.CLOSED;
        String name = "UNKNOWN";
        try {
            Order order = Order.get(orderID);
            status = order.getStatus();
            name = order.name;
        } catch (BadRequestException ignored) {}
        switch (status) {
            case CREATED -> handleCreated(orderID);
            case PROCESSING -> handleProcessing(orderID, name);
            default -> handleOthers(orderID);
        }
    }

    private static void handleCreated(long orderID) {
        Order.get(orderID).setStatus(OrderStatus.PROCESSING);
    }

    private static void handleProcessing(long orderID, String name) {
        OrdersScrollQueue.putIfNotExists(orderID, name);
    }

    private static void handleOthers(long orderID) {
        OrdersScrollQueue.removeIfExists(orderID);
        OrderInfoPaneRenderer.cleanIfFilled(orderID);
    }

}
