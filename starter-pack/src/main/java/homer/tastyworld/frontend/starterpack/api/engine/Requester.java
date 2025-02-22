package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.CantMakeRequestException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Requester {

    private static HttpUriRequestBase createEmptyHttpRequest(Method method, String url) {
        switch (method) {
            case GET:
                return new HttpGet(url);
            case POST:
                return new HttpPost(url);
            case PUT:
                return new HttpPut(url);
            case DELETE:
                return new HttpDelete(url);
            case HEAD:
                return new HttpHead(url);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    private static HttpUriRequestBase createFullHttpRequest(Method method, String url, String token, String jsonBody) {
        HttpUriRequestBase request = createEmptyHttpRequest(method, url);
        if (token != null) {
            request.setHeader("token", token);
        }
        if (jsonBody != null) {
            request.setHeader("Content-Type", ContentType.APPLICATION_JSON);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }
        return request;
    }

    public static Response exchange(Method method, String url, String token, Map<String, Object> jsonBody) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpUriRequestBase request = createFullHttpRequest(
                    method, url, token, RequestBodyProcessor.getStrBody(jsonBody)
            );
            return client.execute(request, ResponseProcessor.JSON_RESPONSE_HANDLER);
        } catch (IOException ex) {
            throw new CantMakeRequestException(ex);
        }
    }

    public static InputStream exchangeImage(String url, String token, Map<String, Object> jsonBody) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpUriRequestBase request = createFullHttpRequest(
                    Method.GET, url, token, RequestBodyProcessor.getStrBody(jsonBody)
            );
            return client.execute(request, ResponseProcessor.STREAM_RESPONSE_HANDLER);
        } catch (IOException ex) {
            throw new CantMakeRequestException(ex);
        }
    }

}
