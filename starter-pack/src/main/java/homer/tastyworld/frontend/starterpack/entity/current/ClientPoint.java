package homer.tastyworld.frontend.starterpack.entity.current;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class ClientPoint {

    public static final long id;
    public static final String name;
    public static final String address;

    static {
        Request request = new Request("/client_point/me", Method.GET);
        Map<String, Object> response = request.request().getResultAsJSON();
        id = TypeChanger.toLong(response.get("ID"));
        name = (String) response.get("NAME");
        address = (String) response.get("ADDRESS");
    }

    public static long[] getActiveOrderIDs() {
        Request request = new Request("/client_point/my_active_orders", Method.GET);
        Response response = request.request();
        return TypeChanger.toSortedPrimitiveLongArray(response.result);
    }

    public static long[] getMenuIDs() {
        Request request = new Request("/client_point/my_menu", Method.GET);
        Response response = request.request();
        return TypeChanger.toSortedPrimitiveLongArray(response.result);
    }

}
