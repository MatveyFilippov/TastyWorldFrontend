package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import org.apache.hc.core5.http.Method;
import java.util.Arrays;
import java.util.Map;

public class OrderStatusUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderStatusUpdatesListener.cooking = cooking;
        OrderStatusUpdatesListener.ready = ready;
        Arrays.stream(MyParams.getActiveOrders()).forEach(orderID -> process(orderID, false));
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID), true));
    }

    private static void route(TableForOrder table, long orderID, String name, boolean notifyIfReady) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(orderID);
            ready.remove(orderID);
        } else if (table == TableForOrder.COOKING) {
            cooking.append(orderID, name);
        } else if (table == TableForOrder.READY) {
            if (notifyIfReady) {
                AlertWindow.showInfo(String.format("Заказ #%s готов", name), "", false);
            }
            cooking.remove(orderID);
            ready.append(orderID, name);
        }
    }

    public static void process(long orderID, boolean notifyIfReady) {
        Request request = new Request("order/read", Method.GET);
        request.putInBody("id", orderID);
        Response response;
        try {
            response = request.request();
        } catch (BadRequestException ex) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null, false);
            return;
        }
        Map<String, Object> result = response.getResultAsJSON();
        route(TableForOrder.get((String) result.get("STATUS")), orderID, (String) result.get("NAME"), notifyIfReady);
    }

}
