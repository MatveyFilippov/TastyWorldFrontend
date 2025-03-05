package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import org.apache.hc.core5.http.Method;
import java.util.Arrays;
import java.util.Map;

public class OrderUpdatesListener {

    private static TableManager cooking, ready;

    public static void init(TableManager cooking, TableManager ready) {
        OrderUpdatesListener.cooking = cooking;
        OrderUpdatesListener.ready = ready;
        Arrays.stream(MyParams.getActiveOrders()).forEach(OrderUpdatesListener::process);
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID)));
    }

    private static void route(TableForOrder table, long orderID, String name) {
        if (table == TableForOrder.NOT_IN_TABLE) {
            cooking.remove(orderID);
            ready.remove(orderID);
        } else if (table == TableForOrder.COOKING) {
            cooking.append(orderID, name);
        } else if (table == TableForOrder.READY) {
            cooking.remove(orderID);
            ready.append(orderID, name);
        }
    }

    public static void process(long orderID) {
        Request request = new Request("order/read", Method.GET);
        request.putInBody("id", orderID);
        Response response;
        try {
            response = request.request();
        } catch (BadRequestException ex) {
            route(TableForOrder.NOT_IN_TABLE, orderID, null);
            return;
        }
        Map<String, Object> result = response.getResultAsJSON();
        route(TableForOrder.get((String) result.get("STATUS")), orderID, (String) result.get("NAME"));
    }

}
