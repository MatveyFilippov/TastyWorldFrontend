package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AppDateTime {

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final ZoneId BACKEND_DATETIME_ZONE_ID;
    public static final ZoneId LOCAL_DATETIME_ZONE_ID;

    static {
        Request request = new Request("/frontend_app/datetime_zone", Method.GET);
        Response response = request.request();
        BACKEND_DATETIME_ZONE_ID = ZoneId.of((String) response.result);
        String localZoneID = AppConfig.getAppDateTimeZoneID();
        LOCAL_DATETIME_ZONE_ID = localZoneID != null ? ZoneId.of(localZoneID) : BACKEND_DATETIME_ZONE_ID;
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date);
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

    public static long getDaysDiff(LocalDate from, LocalDate to) {
        return from.isBefore(to) ? from.datesUntil(to).count() : (to.datesUntil(from).count() * -1);
    }

    public static LocalDateTime parseDateTime(String date) {
        return LocalDateTime.parse(date);
    }

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now(BACKEND_DATETIME_ZONE_ID);
    }

    public static LocalDateTime backendToLocal(LocalDateTime backend) {
        ZonedDateTime sourceZonedDateTime = backend.atZone(BACKEND_DATETIME_ZONE_ID);
        ZonedDateTime targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(LOCAL_DATETIME_ZONE_ID);
        return targetZonedDateTime.toLocalDateTime();
    }

}
