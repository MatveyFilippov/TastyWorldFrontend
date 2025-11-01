package homer.tastyworld.frontend.starterpack.base.exceptions.api.sra;

import homer.tastyworld.frontend.starterpack.api.sra.ErrorResponseBody;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class NotFoundStatusCodeException extends ErrorResponseStatusCodeException {

    public NotFoundStatusCodeException(ClassicHttpResponse response, int expectedStatusCode, ErrorResponseBody errorResponseBody) {
        super(response, expectedStatusCode, errorResponseBody);
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Not Found API Request",
                errorResponseBody != null ? errorResponseBody.detail() : "",
                true
        );
    }

}
