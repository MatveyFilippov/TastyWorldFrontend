package homer.tastyworld.frontend.starterpack.utils.config;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.utils.AppDateTime;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.util.Map;

public class MyParams {

    public static final Map<String, Object> CLIENT_POINT_INFO;
    public static final Map<String, Object> TOKEN_INFO;

    static {
        CLIENT_POINT_INFO = getClientPointInfo();
        TOKEN_INFO = getTokenInfo();
    }

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

    public static long getAvailableDays() {
        LocalDate paidTill = AppDateTime.parse((String) TOKEN_INFO.get("PAID_TILL"));
        return AppDateTime.getNowDate().datesUntil(paidTill).count();
    }

}
