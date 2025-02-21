package homer.tastyworld.frontend.starterpack.utils.config;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class MyParams {
    
    public static final Map<String, Object> CLIENT_POINT_INFO;
    public static final Map<String, Object> TOKEN_INFO;

    static {
        CLIENT_POINT_INFO = getClientPointInfo();
        TOKEN_INFO = getTokenInfo();
    }

    public static Map<String, Object> getClientPointInfo() {
        Request request = new Request("/client_point/me", Method.GET);
        Response response = request.request();
        return (Map<String, Object>) response.result;
    }

    public static Map<String, Object> getTokenInfo() {
        Request request = new Request("/token/me", Method.GET);
        Response response = request.request();
        return (Map<String, Object>) response.result;
    }
    
}
