package homer.tastyworld.frontend.starterpack.base.utils.misc;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeChanger {

    private static final AppLogger logger = AppLogger.getFor(TypeChanger.class);
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
            logger.errorOnlyServerNotify("Can't export Map to JSON str", ex);
        }
        return "{}";
    }

    public static boolean toBool(Object object) {
        return Boolean.parseBoolean(String.valueOf(object));
    }

    public static long toLong(Object object) {
        return Long.parseLong(String.valueOf(object));
    }

    public static int toInt(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

    public static BigDecimal toBigDecimal(Object object) {
        return new BigDecimal(String.valueOf(object));
    }

    public static Long[] toLongArray(Object object) {
        return ((List<Object>) object).stream().map(TypeChanger::toLong).toArray(Long[]::new);
    }

    public static Long[] toSortedLongArray(Object object) {
        return ((List<Object>) object).stream().map(TypeChanger::toLong).sorted().toArray(Long[]::new);
    }

    public static long[] toPrimitiveLongArray(Object object) {
        return ((List<Object>) object).stream().map(TypeChanger::toLong).mapToLong(Long::longValue).toArray();
    }

    public static long[] toSortedPrimitiveLongArray(Object object) {
        return ((List<Object>) object).stream().map(TypeChanger::toLong).sorted().mapToLong(Long::longValue).toArray();
    }

    public static <K, V> Map<K, V> toMap(Object object, Class<K> keyType, Class<V> valueType) {
        Map<K, V> result = new HashMap<>();
        ((Map<Object, Object>) object).forEach(
                (k, v) -> result.put(
                        keyType == String.class
                        ? (K) String.valueOf(k) : OBJECT_MAPPER.convertValue(String.valueOf(k), keyType),
                        valueType == Object.class
                        ? (V) v : OBJECT_MAPPER.convertValue(String.valueOf(v), valueType)
                )
        );
        return result;
    }

}
