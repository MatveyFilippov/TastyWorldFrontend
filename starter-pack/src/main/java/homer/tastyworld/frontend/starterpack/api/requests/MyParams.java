package homer.tastyworld.frontend.starterpack.api.requests;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.ResponseException;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
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
        Response response = null;
        do {
            try {
                response = request.request();
            } catch (ResponseException ex) {
                AppConfig.setToken(null);
                AlertWindow.showError("InvalidToken", ex.response.error, true);
            }
        } while (response == null);
        return response.getResultAsJSON();
    }

    public static long getTokenSubscriptionAvailableDays() {
        LocalDate paidTill = AppDateTime.parseDate((String) getTokenInfo().get("PAID_TILL"));
        return AppDateTime.getDaysDiff(AppDateTime.getBackendNowDate(), paidTill);
    }

    public static Long[] getActiveOrders() {
        Request request = new Request("/client_point/orders", Method.GET);
        Response response = request.request();
        return TypeChanger.toSortedLongArray(response.result);
    }

    public static Long[] getMenu() {
        Request request = new Request("/client_point/menu", Method.GET);
        Response response = request.request();
        return TypeChanger.toSortedLongArray(response.result);
    }

}
