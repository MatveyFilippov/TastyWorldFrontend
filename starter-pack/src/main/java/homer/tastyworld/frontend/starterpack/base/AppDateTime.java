package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.sra.Common;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AppDateTime {

    public static final ZoneOffset SERVICE_DATETIME_ZONE_OFFSET;
    public static final ZoneOffset LOCAL_DATETIME_ZONE_OFFSET;

    static {
        SERVICE_DATETIME_ZONE_OFFSET = Common.getServiceTimezoneOffset();
        LOCAL_DATETIME_ZONE_OFFSET = AppConfig.getAppTimeZoneOffset();
    }

    public static LocalDate getServiceNowDate() {
        return LocalDate.now(SERVICE_DATETIME_ZONE_OFFSET);
    }

    public static LocalDateTime getLocalNowDateTime() {
        return LocalDateTime.now(LOCAL_DATETIME_ZONE_OFFSET);
    }

    public static LocalDateTime getServiceNowDateTime() {
        return LocalDateTime.now(SERVICE_DATETIME_ZONE_OFFSET);
    }

    public static LocalDateTime toLocal(long source) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(source), LOCAL_DATETIME_ZONE_OFFSET);
    }

    public static OffsetDateTime toLocal(LocalDateTime source) {
        return source.atOffset(LOCAL_DATETIME_ZONE_OFFSET);
    }

    public static LocalDateTime toLocal(OffsetDateTime source) {
        OffsetDateTime targetOffsetDateTime = source.withOffsetSameInstant(LOCAL_DATETIME_ZONE_OFFSET);
        return targetOffsetDateTime.toLocalDateTime();
    }

    public static LocalDateTime serviceToLocal(LocalDateTime service) {
        return toLocal(service.atOffset(SERVICE_DATETIME_ZONE_OFFSET));
    }

}
