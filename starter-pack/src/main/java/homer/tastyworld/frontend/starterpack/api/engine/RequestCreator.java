package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestCreator {

    protected final String endpoint;
    protected final Method method;
    private final Map<String, Object> body = new HashMap<>();
    private String token = null;

    public RequestCreator(String endpoint, Method method) {
        this.endpoint = endpoint;
        this.method = method;
    }

    public void putInBody(String key, Object value) {
        body.put(key, value);
    }

    public void cleanBody() {
        body.clear();
    }

    public String getBodyAsJSON() {
        return TypeChanger.toJSON(body);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token == null ? AppConfig.getToken() : token;
    }

}
