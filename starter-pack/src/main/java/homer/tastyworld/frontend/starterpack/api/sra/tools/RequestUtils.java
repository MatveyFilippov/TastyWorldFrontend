package homer.tastyworld.frontend.starterpack.api.sra.tools;

import homer.tastyworld.frontend.starterpack.api.sra.ErrorResponseBody;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.ConflictStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.ErrorResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.BadRequestStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.ForbiddenStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.NotFoundStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.UnauthorizedStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantProcessResponseException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.InternalServerErrorStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.UnprocessableEntityStatusCodeException;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class RequestUtils {

    private static final AppLogger logger = AppLogger.getFor(RequestUtils.class);
    private static final CacheableFunction<String, String> cacheableGetBearerAuthorization = CacheManager.getForFunction("Bearer %s"::formatted);
    private static final CacheableFunction<String, URI> cacheableGetURI = CacheManager.getForFunction(endpoint -> {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return URI.create(AppConfig.TW_SRA_URL + endpoint);
    });

    public static URI getURI(String endpoint) {
        return cacheableGetURI.applyWithCache(endpoint);
    }

    public static String getBearerAuthorization() {
        return cacheableGetBearerAuthorization.applyWithCache(AppConfig.getAuthorizationTokenSRA());
    }

    private static ErrorResponseBody toErrorResponse(ClassicHttpResponse response) {
        try {
            return TypeChanger.toRecord(EntityUtils.toString(response.getEntity()), ErrorResponseBody.class);
        } catch (Exception ex) {
            logger.error("Can't parse error response", ex);
            return null;
        }
    }

    public static void throwIfUnexpectedStatusCode(ClassicHttpResponse response, int expected) throws UnexpectedResponseStatusCodeException {
        int statusCode = response.getCode();
        if (statusCode == expected) {
            return;
        }
        if (statusCode < 400) {
            throw new UnexpectedResponseStatusCodeException(response, expected);
        }
        switch (statusCode) {
            case 400 -> throw new BadRequestStatusCodeException(response, expected, toErrorResponse(response));
            case 401 -> throw new UnauthorizedStatusCodeException(response, expected, toErrorResponse(response));
            case 403 -> throw new ForbiddenStatusCodeException(response, expected, toErrorResponse(response));
            case 404 -> throw new NotFoundStatusCodeException(response, expected, toErrorResponse(response));
            case 409 -> throw new ConflictStatusCodeException(response, expected, toErrorResponse(response));
            case 422 -> throw new UnprocessableEntityStatusCodeException(response, expected, toErrorResponse(response));
            case 500 -> throw new InternalServerErrorStatusCodeException(response, expected, toErrorResponse(response));
            default -> throw new ErrorResponseStatusCodeException(response, statusCode, toErrorResponse(response));
        }
    }

    public static int processEmptyResponse(ClassicHttpResponse response, int expectedStatusCode) {
        throwIfUnexpectedStatusCode(response, expectedStatusCode);
        return response.getCode();
    }

    public static <E extends Record> E processEntityResponse(ClassicHttpResponse response, int expectedStatusCode, Class<? extends E> entityType) {
        throwIfUnexpectedStatusCode(response, expectedStatusCode);
        try {
            String json = EntityUtils.toString(response.getEntity());
            return TypeChanger.toRecord(json, entityType);
        } catch (Exception ex) {
            throw new CantProcessResponseException(response, ex);
        }
    }

    public static <E extends Record> List<E> processEntityArrayResponse(ClassicHttpResponse response, int expectedStatusCode, Class<E> elementType) {
        throwIfUnexpectedStatusCode(response, expectedStatusCode);
        try {
            String json = EntityUtils.toString(response.getEntity());
            return TypeChanger.toRecordList(json, elementType);
        } catch (Exception ex) {
            throw new CantProcessResponseException(response, ex);
        }
    }

}
