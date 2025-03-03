package homer.tastyworld.frontend.poscreator.core.orders.internal;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class OrderCreating {

    public static Long id;
    private static int last_used_name_index = 0;

    private static String findFreeName() {
        Request request = new Request("/order/is_name_in_active_order", Method.GET);
        while (true) {
            last_used_name_index++;
            if (last_used_name_index >= 100) {
                last_used_name_index = 1;
            }
            String name = String.valueOf(last_used_name_index);
            request.putInBody("name", name);
            if (!TypeChanger.toBool(request.request().result)) {
                return name;
            }
        }
    }

    public static void start(boolean ignoreIfExists) {
        if (id != null) {
            if (ignoreIfExists) {
                return;
            }
            try {
                delete();
            } catch (BadRequestException ignore) {}
        }
        Request request = new Request("/order/create", Method.POST);
        request.putInBody("name", findFreeName());
        id = TypeChanger.toLong(request.request().result);
    }

    public static void appendProduct(long productID, int pieceQTY, Map<Long, Integer> notDefaultAdditives) {
        Request request = new Request("/order/append_item", Method.POST);
        request.putInBody("order_id", id);
        request.putInBody("product_id", productID);
        request.putInBody("piece_qty", pieceQTY);
        if (notDefaultAdditives != null && !notDefaultAdditives.isEmpty()) {
            request.putInBody("not_default_additives", notDefaultAdditives);
        }
        request.request();
    }

    public static void setPaid() {
        Request request = new Request("/order/mark_as_paid", Method.POST);
        request.putInBody("id", id);
        request.request();
    }

    public static void editDeliveryAddress(String address) {
        Request request = new Request("/order/edit_delivery_address", Method.POST);
        request.putInBody("id", id);
        if (address != null) {
            request.putInBody("address", address);
        } else {
            request.putInBody("not_for_delivery", true);
        }
        request.request();
    }

    public static void finish() {
        Request request = new Request("/order/set_status", Method.POST);
        request.putInBody("id", id);
        request.putInBody("new_status", "CREATED");
        request.request();
        id = null;
    }

    public static void delete() {
        if (id != null) {
            Request request = new Request("/order/delete", Method.POST);
            request.putInBody("id", id);
            request.request();
            id = null;
            last_used_name_index--;
        }
    }

}
