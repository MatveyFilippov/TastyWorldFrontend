package homer.tastyworld.frontend.starterpack.utils;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class AppDateTime {

    public static final ZoneId BACKEND_DATETIME_ZONE_ID;

    static {
        Request request = new Request("/client_point/me", Method.GET);
        Response response = request.request();
        BACKEND_DATETIME_ZONE_ID = ZoneId.of((String) response.result);
    }

    public static LocalDate getNowDate() {
        return LocalDate.now(BACKEND_DATETIME_ZONE_ID);
    }

    public static LocalDate plusDaysFromNowToDate(long days) {
        return getNowDate().plusDays(days);
    }

    public static LocalDate getPasterDate(LocalDate firs, LocalDate second) {
        return firs.isBefore(second) ? firs : second;
    }

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now(BACKEND_DATETIME_ZONE_ID);
    }

}
