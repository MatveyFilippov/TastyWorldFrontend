package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.CantParseRequestBodyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

class RequestBodyProcessor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getStrBody(Map<String, Object> body) {
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
            throw new CantParseRequestBodyException(body.toString());
        }
    }

}
