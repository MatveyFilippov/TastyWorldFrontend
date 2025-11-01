package homer.tastyworld.frontend.starterpack.api.evotor.mobcashier.authorize;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantProcessResponseException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier.ErrorResponseStatusCodeWhileCreatingIntegrationException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier.NoDataToCreateIntegrationException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier.UserNotExistsWhileCreatingIntegrationException;
import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.Nullable;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Integration {

    private record CreateIntegrationRequestBody(
            @Nullable String inn,
            String phone,
            String identifier_secret
    ) {}

    private record DataObjectInCreateIntegrationResponseBody(
            Boolean user_exist,
            String user_id,
            String token,
            String android_id
    ) {}

    private record ErrorDetailElementObjectInCreateIntegrationResponseBody(
            String reason,
            String detail
    ) {}

    public record CreateIntegrationResponseBody(
            Long timestamp,
            @Nullable DataObjectInCreateIntegrationResponseBody data,
            Integer error,
            @Nullable String error_message,
            @Nullable List<ErrorDetailElementObjectInCreateIntegrationResponseBody> error_details
    ) {}

    private static class IssuedAt {

        private static String token;
        private static LocalDateTime issuedAt;

        public static void set(String token) {
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = TypeChanger.toMap(payloadJson);
            long iat = (long) payload.get("iat");

            IssuedAt.issuedAt = AppDateTime.toLocal(iat);
            IssuedAt.token = token;
        }

        public static LocalDateTime getFor(String token) {
            if (!token.equals(IssuedAt.token)) {
                set(token);
            }
            return issuedAt;
        }

    }

    private static final Set<Integer> createIntegrationExpectedResponseCodes = Set.of(200, 400, 401, 403, 500);
    private static final URI createIntegrationURI = URI.create(AppConfig.EVOTOR_MC_API_URL + "/authorize/integration");
    private static String token;
    private static String androidID;

    private static CreateIntegrationRequestBody getCreateIntegrationRequestBody() {
        String phone = AppConfig.getEvotorAccountPhone();
        String identifierSecret = AppConfig.getEvotorMobcashierIdentifierSecret();
        if (phone == null || identifierSecret == null) {
            throw new NoDataToCreateIntegrationException();
        }
        return new CreateIntegrationRequestBody(null, phone, identifierSecret);
    }

    private static DataObjectInCreateIntegrationResponseBody requestAndGetDataObjectFromCreateIntegrationResponseBody() {
        CreateIntegrationRequestBody createIntegrationRequestBody = getCreateIntegrationRequestBody();
        Request createIntegrationRequest = Request.of(createIntegrationURI, Method.POST, createIntegrationRequestBody);
        return Requester.exchange(
                createIntegrationRequest, response -> {
                    if (!createIntegrationExpectedResponseCodes.contains(response.getCode())) {
                        throw new UnexpectedResponseStatusCodeException(response, 200);
                    }

                    CreateIntegrationResponseBody responseBody;
                    try {
                        String json = EntityUtils.toString(response.getEntity());
                        responseBody = TypeChanger.toRecord(json, CreateIntegrationResponseBody.class);
                    } catch (Exception ex) {
                        throw new CantProcessResponseException(response, ex);
                    }
                    if (responseBody.error() != 0) {
                        throw new ErrorResponseStatusCodeWhileCreatingIntegrationException(response, responseBody);
                    }

                    return responseBody.data();
                }
        );
    }

    private static void resetTokenAndAndroidID() {
        DataObjectInCreateIntegrationResponseBody dataObjectInCreateIntegrationResponseBody = requestAndGetDataObjectFromCreateIntegrationResponseBody();
        if (!dataObjectInCreateIntegrationResponseBody.user_exist()) {
            throw new UserNotExistsWhileCreatingIntegrationException();
        }
        token = dataObjectInCreateIntegrationResponseBody.token();
        androidID = dataObjectInCreateIntegrationResponseBody.android_id();
    }

    private static void resetIfOutdatedTokenAndAndroidID() {
        if (token == null || androidID == null) {
            resetTokenAndAndroidID();
            return;
        }
        if (IssuedAt.getFor(token).isBefore(AppDateTime.getLocalNowDateTime())) {
            resetTokenAndAndroidID();
            return;
        }
    }

    public static void dropTokenAndAndroidID() {
        token = null;
        androidID = null;
    }

    public static Optional<String> getToken() {
        resetIfOutdatedTokenAndAndroidID();
        return Optional.ofNullable(token);
    }

    public static Optional<String> getAndroidID() {
        resetIfOutdatedTokenAndAndroidID();
        return Optional.ofNullable(androidID);
    }

}
