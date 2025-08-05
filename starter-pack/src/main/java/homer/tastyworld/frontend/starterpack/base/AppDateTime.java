package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import org.apache.hc.core5.http.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AppDateTime {

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final ZoneOffset BACKEND_DATETIME_ZONE_OFFSET;
    public static final ZoneOffset LOCAL_DATETIME_ZONE_OFFSET;

    static {
        Request request = new Request("/frontend_app/datetime_zone_offset", Method.GET);
        Response response = request.request();
        BACKEND_DATETIME_ZONE_OFFSET = ZoneOffset.of((String) response.result());
        String localZoneOffset = AppConfig.getAppDateTimeZoneOffset();
        LOCAL_DATETIME_ZONE_OFFSET = localZoneOffset != null ? ZoneOffset.of(localZoneOffset) : OffsetDateTime.now().getOffset();;
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }

    public static LocalDate getBackendNowDate() {
        return LocalDate.now(BACKEND_DATETIME_ZONE_OFFSET);
    }

    public static LocalDate plusDaysFromBackendNow(long days) {
        return getBackendNowDate().plusDays(days);
    }

    public static LocalDate getPasterDate(LocalDate firs, LocalDate second) {
        return firs.isAfter(second) ? firs : second;
    }

    public static LocalDate getEarlierDate(LocalDate first, LocalDate second) {
        return first.isBefore(second) ? first : second;
    }

    public static long getDaysDiff(LocalDate from, LocalDate to) {
        return from.isBefore(to) ? from.datesUntil(to).count() : (to.datesUntil(from).count() * -1);
    }

    public static LocalDateTime parseDateTime(String datetime) {
        return OffsetDateTime.parse(datetime).toLocalDateTime();
    }

    public static LocalDateTime getBackendNowDateTime() {
        return LocalDateTime.now(BACKEND_DATETIME_ZONE_OFFSET);
    }

    public static LocalDateTime backendToLocal(LocalDateTime backend) {
        OffsetDateTime sourceOffsetDateTime = backend.atOffset(BACKEND_DATETIME_ZONE_OFFSET);
        OffsetDateTime targetOffsetDateTime = sourceOffsetDateTime.withOffsetSameInstant(LOCAL_DATETIME_ZONE_OFFSET);
        return targetOffsetDateTime.toLocalDateTime();
    }

}
