package homer.tastyworld.frontend.starterpack.order.core;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.entity.misc.OrderStatus;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;

public class PreparedRequests {

    private static final Request CREATE = new Request("/order/create", Method.POST);
    private static final Request APPEND_ITEM = new Request("/order/append_item", Method.POST);
    private static final Request EDIT_ITEM = new Request("/order/edit_item", Method.POST);
    private static final Request EDIT_ITEM_ADDITIVE = new Request("/order/edit_item_additive", Method.POST);
    private static final Request REMOVE_ITEM = new Request("/order/remove_item", Method.POST);
    private static final Request GET_STATUS = new Request("/order/status", Method.GET);
    private static final Request SET_STATUS = new Request("/order/status", Method.POST);
    private static final Request CLOSE = new Request("/order/close", Method.POST);
    private static final Request READ = new Request("/order/read", Method.GET);
    private static final Request IS_PAID = new Request("/order/is_paid", Method.GET);
    private static final Request MARK_AS_PAID = new Request("/order/mark_as_paid", Method.POST);
    private static final Request EDIT_DELIVERY_INFO = new Request("/order/edit_delivery_info", Method.POST);
    private static final Request DELETE = new Request("/order/delete", Method.POST);

    public static long create(String name, String deliveryInfo) {
        CREATE.cleanBody();
        CREATE.putInBody("name", name);
        CREATE.putInBody("delivery_info", deliveryInfo);
        Response response = CREATE.request();
        return TypeChanger.toLong(response.result());
    }

    public static long appendItem(long id, long productID, Integer pieceQTY, Map<Long, Integer> netDefaultAdditives) {
        APPEND_ITEM.cleanBody();
        APPEND_ITEM.putInBody("order_id", id);
        APPEND_ITEM.putInBody("product_id", productID);
        APPEND_ITEM.putInBody("piece_qty", pieceQTY);
        APPEND_ITEM.putInBody("not_default_additives", netDefaultAdditives);
        Response response = APPEND_ITEM.request();
        return TypeChanger.toLong(response.getResultAsJSON().get("NEW_ITEM_ID"));
    }

    public static void editItem(long itemID, int newQTY) {
        EDIT_ITEM.putInBody("id", itemID);
        EDIT_ITEM.putInBody("new_piece_qty", newQTY);
        EDIT_ITEM.request();
    }

    public static void editItemAdditive(long additiveID, int newQTY) {
        EDIT_ITEM_ADDITIVE.putInBody("id", additiveID);
        EDIT_ITEM_ADDITIVE.putInBody("new_piece_qty", newQTY);
        EDIT_ITEM_ADDITIVE.request();
    }

    public static void removeItem(long itemID) {
        REMOVE_ITEM.putInBody("id", itemID);
        REMOVE_ITEM.request();
    }

    public static long[] getItemIDs(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return TypeChanger.toSortedPrimitiveLongArray(response.getResultAsJSON().get("ITEM_IDs"));
    }

    public static OrderStatus getStatus(long id) {
        GET_STATUS.putInBody("id", id);
        Response response = GET_STATUS.request();
        return OrderStatus.valueOf((String) response.result());
    }

    public static void setStatus(long id, OrderStatus status) {
        SET_STATUS.putInBody("id", id);
        SET_STATUS.putInBody("new_status", status);
        SET_STATUS.request();
    }

    public static void close(long id) {
        CLOSE.putInBody("id", id);
        CLOSE.request();
    }

    public static BigDecimal getTotalPrice(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return TypeChanger.toBigDecimal(response.getResultAsJSON().get("TOTAL_PRICE"));
    }

    public static boolean isPaid(long id) {
        IS_PAID.putInBody("id", id);
        Response response = IS_PAID.request();
        return TypeChanger.toBool(response.result());
    }

    public static String getPaidAt(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return (String) response.getResultAsJSON().get("PAID_AT");
    }

    public static void markAsPaid(long id) {
        MARK_AS_PAID.putInBody("id", id);
        MARK_AS_PAID.request();
    }

    public static String getDeliveryInfo(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return (String) response.getResultAsJSON().get("DELIVERY_INFO");
    }

    public static void editDeliveryInfo(long id, String info) {
        EDIT_DELIVERY_INFO.cleanBody();
        EDIT_DELIVERY_INFO.putInBody("id", id);
        if (info == null) {
            EDIT_DELIVERY_INFO.putInBody("not_for_delivery", true);
        } else {
            EDIT_DELIVERY_INFO.putInBody("info", info);
        }
        EDIT_DELIVERY_INFO.request();
    }

    public static String getClosedAt(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return (String) response.getResultAsJSON().get("CLOSED_AT");
    }

    public static Map<String, Object> fullRead(long id) {
        READ.putInBody("id", id);
        Response response = READ.request();
        return response.getResultAsJSON();
    }

    public static void delete(long id) {
        DELETE.putInBody("id", id);
        DELETE.request();
    }

}
