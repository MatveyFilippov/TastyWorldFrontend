package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.apache.hc.core5.http.Method;
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
        if (waiter == null) {
            return;
        }
        Request request = new Request("order/is_paid", Method.GET);
        request.putInBody("id", orderID);
        Response response;
        try {
            response = request.request();
        } catch (BadRequestException ex) {
            waiters.remove(orderID);
            return;
        }
        if ((Boolean) response.result) {
            waiter.setTextFill(MarkColors.PAID);
            waiters.remove(orderID);
        } else {
            waiter.setTextFill(MarkColors.UNPAID);
        }
    }

}
