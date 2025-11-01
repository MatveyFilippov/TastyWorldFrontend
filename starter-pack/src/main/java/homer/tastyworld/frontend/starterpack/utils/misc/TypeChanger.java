package homer.tastyworld.frontend.starterpack.utils.misc;

import homer.tastyworld.frontend.starterpack.base.exceptions.unexpected.TypeChangerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Map;

public class TypeChanger {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert Map to JSON str", ex);
        }
    }

    public static String toJSON(Record record) {
        try {
            return OBJECT_MAPPER.writeValueAsString(record);
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert Record to JSON str", ex);
        }
    }

    public static <T extends Record> T toRecord(String json, Class<T> recordType) {
        try {
            return OBJECT_MAPPER.readValue(json, recordType);
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert JSON str to Record", ex);
        }
    }

    public static <T extends Record> List<T> toRecordList(String json, Class<T> elementType) {
        try {
            return OBJECT_MAPPER.readValue(
                    json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert JSON str to List<Record>", ex);
        }
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert JSON str to Map<String, Object>", ex);
        }
    }

    public static <T> T convert(Object actual, Class<T> targetClass) {
        try {
            return OBJECT_MAPPER.convertValue(actual, targetClass);
        } catch (Exception ex) {
            throw new TypeChangerException("Can't convert to " + targetClass, ex);
        }
    }

}
