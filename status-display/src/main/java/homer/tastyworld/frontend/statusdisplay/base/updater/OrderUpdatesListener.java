package homer.tastyworld.frontend.statusdisplay.base.updater;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.utils.AppLogger;
import homer.tastyworld.frontend.statusdisplay.base.tablemanager.TableManager;
import org.apache.hc.core5.http.Method;
import java.util.HashMap;
import java.util.Map;

public class OrderUpdatesListener {

    private static final Map<Long, String> orders = new HashMap<>();
    private static final AppLogger logger = AppLogger.getFor(OrderUpdatesListener.class);
    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderUpdatesListener.cooking = cooking;
        OrderUpdatesListener.ready = ready;
        Subscriber.subscribe(Theme.ORDER_STATUS, orderID -> process(Long.parseLong(orderID)));
    }

    private static void route(String status, long orderID) {
        TableForOrder table = TableForOrder.get(status);
        String name = orders.get(orderID);
        if (table == TableForOrder.NOT_IN_TABLE) {
            if (name != null) {
                cooking.remove(name);
                ready.remove(name);
                orders.remove(orderID);
            }
        } else if (table == TableForOrder.COOKING) {
            cooking.append(name);
        } else if (table == TableForOrder.READY) {
            cooking.remove(name);
            ready.append(name);
        }
    }

    public static void process(long orderID) {
        Request request = new Request("order/read", Method.GET);
        request.putInBody("id", orderID);
        Response response;
        try {
            response = request.request();
        } catch (BadRequestException ex) {
            route("NOT_EXISTS", orderID);
            return;
        }
        if (response.result == null) {
            logger.error("Something wrong with order, response while reading info ('order/read'): " + response);
            route("NOT_EXISTS", orderID);
            return;
        }
        Map<String, Object> result = response.getResultAsJSON();
        orders.put(orderID, (String) result.get("NAME"));
        route((String) result.get("STATUS"), orderID);
    }

}
