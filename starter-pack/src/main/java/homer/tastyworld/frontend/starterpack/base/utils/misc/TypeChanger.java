package homer.tastyworld.frontend.starterpack.base.utils.misc;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

public class TypeChanger {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toDaysFormat(long days) {
        if (days % 10 == 1 && days % 100 != 11) {
            return days + " день";
        } else if (days % 10 >= 2 && days % 10 <= 4 && (days % 100 < 10 || days % 100 >= 20)) {
            return days + " дня";
        } else {
            return days + " дней";
        }
    }

    public static String toJSON(Map<String, Object> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            AppLogger.GLOBAL_LOGGER.errorOnlyServerNotify("Can't export Map to JSON str", ex);
        }
        return "{}";
    }

    public static Long[] toLongArray(Object object) {
        return ((List<Object>) object).stream().map(obj -> Long.parseLong(String.valueOf(obj))).toArray(Long[]::new);
    }

}
