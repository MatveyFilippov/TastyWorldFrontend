package homer.tastyworld.frontend.starterpack.entity.current;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.ResponseException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.util.Map;

public class Token {

    public static Map<String, Object> getTokenInfo() {
        Request request = new Request("/token/me", Method.GET);
        Response response = null;
        do {
            try {
                response = request.request();
            } catch (ResponseException ex) {
                AppConfig.setToken(null);
                AlertWindow.showError("InvalidToken", ex.response.error(), true);
            }
        } while (response == null);
        return response.getResultAsJSON();
    }

    public static long getTokenSubscriptionAvailableDays() {
        LocalDate paidTill = AppDateTime.parseDate((String) getTokenInfo().get("PAID_TILL"));
        return AppDateTime.getDaysDiff(AppDateTime.getBackendNowDate(), paidTill);
    }

}
