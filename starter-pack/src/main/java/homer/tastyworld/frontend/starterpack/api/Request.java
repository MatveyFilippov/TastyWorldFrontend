package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.utils.config.AppConfig;
import org.apache.hc.core5.http.Method;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final Map<String, String> urlCache = new HashMap<>();
    private final String endpoint;
    private final Method method;
    private final Map<String, Object> body = new HashMap<>();
    private String token = AppConfig.getToken();

    public Request(String endpoint, Method method) {
        this.endpoint = endpoint;
        this.method = method;
    }

    public static String getURL(String endpoint) {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return urlCache.computeIfAbsent(endpoint, toAppend -> (AppConfig.API_URL + toAppend));
    }

    public void putInBody(String key, Object value) {
        body.put(key, value);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Response request() {
        return Requester.exchange(method, getURL(endpoint), token, body);
    }

}
