package homer.tastyworld.frontend.starterpack.api.sra.entity.current;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.ErrorResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.Method;
import java.util.Set;

public class AuthorizationToken {

    private record GetAuthorizationTokenResponseBody(
            Long client_point_id,
            String token_sha256_hash,
            String name,
            Boolean is_active,
            Set<String> access_modules
    ) {}

    private static GetAuthorizationTokenResponseBody getToken() {
        GetAuthorizationTokenResponseBody getAuthorizationTokenResponseBody = null;
        do {
            Request getAuthorizationTokenRequest = Request.of(
                    RequestUtils.getURI("/authorization_tokens/me"), Method.GET, RequestUtils.getBearerAuthorization()
            );
            try {
                getAuthorizationTokenResponseBody = Requester.exchange(
                        getAuthorizationTokenRequest,
                        response -> RequestUtils.processEntityResponse(response, 200, GetAuthorizationTokenResponseBody.class)
                );
            } catch (UnexpectedResponseStatusCodeException ex) {
                AppConfig.setAuthorizationToken(null);
                String errorText = "Неожиданный ответ от облака (StatusCode != 200)";
                if (ex instanceof ErrorResponseStatusCodeException erscex) {
                    errorText = erscex.errorResponseBody != null
                                ? erscex.errorResponseBody.detail()
                                : "Не смог самостятельно разобарть проблему (%s)".formatted(ex.actualStatusCode);
                }
                AlertWindows.showError("Invalid AuthorizationToken", errorText, true);
            }
        } while (getAuthorizationTokenResponseBody == null);

        return getAuthorizationTokenResponseBody;
    }

    public static boolean isActive() {
        return getToken().is_active();
    }

}
