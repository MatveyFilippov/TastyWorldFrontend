package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.SimpleCacheProcessor;
import org.apache.hc.core5.http.Method;
import java.util.HashMap;
import java.util.Map;

public class Request {

    public static final SimpleCacheProcessor<String, String> urlCache = CacheManager.register(endpoint -> {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return AppConfig.API_URL + endpoint;
    });
    protected final String endpoint;
    protected final Method method;
    protected final Map<String, Object> body = new HashMap<>();
    private String token = null;

    public Request(String endpoint, Method method) {
        this.endpoint = endpoint;
        this.method = method;
    }

    public void putInBody(String key, Object value) {
        body.put(key, value);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token == null ? AppConfig.getToken() : token;
    }

    public Response request() {
        return Requester.exchange(method, urlCache.get(endpoint), getToken(), body);
    }

}
