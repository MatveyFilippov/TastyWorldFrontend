package homer.tastyworld.frontend.pos.creator.core.orders.internal;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class OrderLooking {

    public static Long id = null;
    private static Map<String, Object> orderInfo;

    private static Object getOrderInfo(String key) {
        if (orderInfo == null) {
            Request request = new Request("/order/read", Method.GET);
            request.putInBody("id", id);
            orderInfo = request.request().getResultAsJSON();
            if (orderInfo.get("DELIVERY_ADDRESS").equals("NOT FOR DELIVERY")) {
                orderInfo.put("DELIVERY_ADDRESS", "");
            }
        }
        return orderInfo.get(key);
    }

    public static void start(long id) {
        OrderLooking.id = id;
        getOrderInfo("ID");
    }

    public static boolean isPaid() {
        return TypeChanger.toBool(getOrderInfo("IS_PAID"));
    }

    public static void setPaid() {
        if (isPaid()) {
            return;
        }
        Request request = new Request("/order/mark_as_paid", Method.POST);
        request.putInBody("id", id);
        request.request();
    }

    public static void editDeliveryAddressIfNotEqual(String address) {
        if (getOrderInfo("DELIVERY_ADDRESS").equals(address)) {
            return;
        }
        Request request = new Request("/order/edit_delivery_address", Method.POST);
        request.putInBody("id", id);
        if (address != null) {
            request.putInBody("address", address);
        } else {
            request.putInBody("not_for_delivery", true);
        }
        request.request();
    }

    public static void setDone() {
        Request request = new Request("/order/close", Method.POST);
        request.putInBody("id", id);
        request.request();
    }

    public static void finish() {
        OrderLooking.id = null;
    }

}
