package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

public record Request(
        URI uri,
        Method method,
        Map<String, String> headers,
        @Nullable ContentType contentType,
        @Nullable String bodyString
) {

    public static Request of(URI uri, Method method) {
        return new Request(uri, method, Map.of(), null, null);
    }

    public static Request of(URI uri, Method method, String authorization) {
        return new Request(uri, method, Map.of("Authorization", authorization), null, null);
    }

    public static Request of(URI uri, Method method, ContentType contentType, String bodyString) {
        return new Request(uri, method, Map.of(), contentType, bodyString);
    }

    public static Request of(URI uri, Method method, String authorization, ContentType contentType, String bodyString) {
        return new Request(uri, method, Map.of("Authorization", authorization), contentType, bodyString);
    }

    public static Request of(URI uri, Method method, Map<String, String> headers, Map<String, Object> jsonBody) {
        return new Request(uri, method, headers, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }

    public static Request of(URI uri, Method method, Map<String, String> headers, Record jsonBody) {
        return new Request(uri, method, headers, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }

    public static Request of(URI uri, Method method, Map<String, Object> jsonBody) {
        return of(uri, method, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }

    public static Request of(URI uri, Method method, Record jsonBody) {
        return of(uri, method, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }

    public static Request of(URI uri, Method method, String authorization, Map<String, Object> jsonBody) {
        return of(uri, method, authorization, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }

    public static Request of(URI uri, Method method, String authorization, Record jsonBody) {
        return of(uri, method, authorization, ContentType.APPLICATION_JSON, TypeChanger.toJSON(jsonBody));
    }


}
