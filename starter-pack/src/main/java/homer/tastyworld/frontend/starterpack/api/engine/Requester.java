package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantMakeRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantProcessResponseException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class Requester {

    private static HttpUriRequestBase createEmptyHttpRequest(Request request) {
        return switch (request.method()) {
            case GET -> new HttpGet(request.uri());
            case POST -> new HttpPost(request.uri());
            case PUT -> new HttpPut(request.uri());
            case PATCH -> new HttpPatch(request.uri());
            case DELETE -> new HttpDelete(request.uri());
            case HEAD -> new HttpHead(request.uri());
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + request.method().name());
        };
    }

    private static HttpUriRequestBase createFullHttpRequest(Request request) {
        HttpUriRequestBase result = createEmptyHttpRequest(request);

        request.headers().forEach(result::setHeader);

        String body = request.bodyString();
        if (body != null) {
            ContentType contentType = request.contentType();
            if (contentType != null) {
                result.setHeader("Content-Type", contentType);
            }
            StringEntity entity = new StringEntity(body, request.contentType());
            result.setEntity(entity);
        }

        return result;
    }

    public static int exchange(Request request) {
        HttpUriRequestBase fullHttpRequest = createFullHttpRequest(request);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(fullHttpRequest, HttpResponse::getCode);
        } catch (Exception ex) {
            throw new CantMakeRequestException(request, ex);
        }
    }

    public static <T> T exchange(Request request, HttpClientResponseHandler<? extends T> responseHandler) {
        HttpUriRequestBase fullHttpRequest = createFullHttpRequest(request);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(fullHttpRequest, responseHandler);
        } catch (CantProcessResponseException | UnexpectedResponseStatusCodeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CantMakeRequestException(request, ex);
        }
    }

}
