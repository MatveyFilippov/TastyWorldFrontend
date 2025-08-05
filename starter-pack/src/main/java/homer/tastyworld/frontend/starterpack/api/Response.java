package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import java.util.Map;

public record Response(String status, String error, Object note, Object result) {

    public Map<String, Object> getResultAsJSON() {
        if (result == null) {
            return null;
        }
        return TypeChanger.toMap(result, String.class, Object.class);
    }

}
