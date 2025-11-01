package homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier;

import homer.tastyworld.frontend.starterpack.api.evotor.mobcashier.authorize.Integration;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class ErrorResponseStatusCodeWhileCreatingIntegrationException extends UnexpectedResponseStatusCodeException {

    public final Integration.CreateIntegrationResponseBody errorResponseBody;

    public ErrorResponseStatusCodeWhileCreatingIntegrationException(ClassicHttpResponse response, Integration.CreateIntegrationResponseBody errorResponseBody) {
        super(
                "Faced with unexpected error response status code (Code: %s; Body: %s)".formatted(
                        response.getCode(), errorResponseBody
                ),
                response,
                200
        );
        this.errorResponseBody = errorResponseBody;
    }

}
