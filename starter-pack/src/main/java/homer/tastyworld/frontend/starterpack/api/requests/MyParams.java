package homer.tastyworld.frontend.starterpack.api.requests;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.util.Map;

public class MyParams {

    public static Map<String, Object> getClientPointInfo() {
        Request request = new Request("/client_point/me", Method.GET);
        Response response = request.request();
        return response.getResultAsJSON();
    }

    public static Map<String, Object> getTokenInfo() {
        Request request = new Request("/token/me", Method.GET);
        Response response = request.request();
        return response.getResultAsJSON();
    }

    public static long getTokenSubscriptionAvailableDays() {
        LocalDate paidTill = AppDateTime.parse((String) getTokenInfo().get("PAID_TILL"));
        return AppDateTime.getDaysDiff(AppDateTime.getNowDate(), paidTill);
    }

    public static Long[] getActiveOrders() {
        Request request = new Request("/client_point/orders", Method.GET);
        Response response = request.request();
        return TypeChanger.toLongArray(response.result);
    }

}
