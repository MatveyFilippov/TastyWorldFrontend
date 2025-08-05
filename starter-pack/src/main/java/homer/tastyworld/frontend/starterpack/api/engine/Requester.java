package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.api.PhotoResponse;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest.CantMakeRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
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

    public static final CacheProcessor<String, String> URL_CACHE = CacheManager.register(endpoint -> {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return AppConfig.TW_SRA_URL + endpoint;
    });

    private static HttpUriRequestBase createEmptyHttpRequest(RequestCreator creator) {
        return switch (creator.method) {
            case GET -> new HttpGet(URL_CACHE.get(creator.endpoint));
            case POST -> new HttpPost(URL_CACHE.get(creator.endpoint));
            case PUT -> new HttpPut(URL_CACHE.get(creator.endpoint));
            case DELETE -> new HttpDelete(URL_CACHE.get(creator.endpoint));
            case HEAD -> new HttpHead(URL_CACHE.get(creator.endpoint));
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + creator.method);
        };
    }

    private static HttpUriRequestBase createFullHttpRequest(RequestCreator creator) {
        HttpUriRequestBase request = createEmptyHttpRequest(creator);

        String token = creator.getToken();
        if (token != null) {
            request.setHeader("token", token);
        }

        String jsonBody = creator.getBodyAsJSON();
        if (jsonBody != null) {
            request.setHeader("Content-Type", ContentType.APPLICATION_JSON);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }

        return request;
    }

    public static Response exchange(RequestCreator creator) {
        HttpUriRequestBase request = createFullHttpRequest(creator);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(request, ResponseProcessor.JSON_RESPONSE_HANDLER);
        } catch (IOException ex) {
            throw new CantMakeRequestException(
                    creator.method, URL_CACHE.get(creator.endpoint), creator.getToken(), creator.getBodyAsJSON(), ex
            );
        }
    }

    public static InputStream exchangeStream(RequestCreator creator) {
        HttpUriRequestBase request = createFullHttpRequest(creator);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(request, ResponseProcessor.STREAM_RESPONSE_HANDLER);
        } catch (IOException ex) {
            throw new CantMakeRequestException(
                    creator.method, URL_CACHE.get(creator.endpoint), creator.getToken(), creator.getBodyAsJSON(), ex
            );
        }
    }

    public static PhotoResponse exchangeImage(RequestCreator creator) {
        HttpUriRequestBase request = createFullHttpRequest(creator);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(request, ResponseProcessor.IMAGE_RESPONSE_HANDLER);
        } catch (IOException ex) {
            throw new CantMakeRequestException(
                    creator.method, URL_CACHE.get(creator.endpoint), creator.getToken(), creator.getBodyAsJSON(), ex
            );
        }
    }

}
