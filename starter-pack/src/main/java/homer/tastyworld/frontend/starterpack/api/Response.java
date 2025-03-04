package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import java.util.Map;

public class Response {

    public String status;
    public String error;
    public Object note;
    public Object result;

    @Override
    public String toString() {
        return String.format("Response {status=%s ; error=%s ; note=%s ; result=%s}", status, error, note, result);
    }

    public Map<String, Object> getResultAsJSON() {
        if (result == null) {
            return null;
        }
        return TypeChanger.toMap(result, String.class, Object.class);
    }

}
