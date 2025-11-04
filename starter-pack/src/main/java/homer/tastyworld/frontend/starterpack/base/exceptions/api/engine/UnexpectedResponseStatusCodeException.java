package homer.tastyworld.frontend.starterpack.base.exceptions.api.engine;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class UnexpectedResponseStatusCodeException extends RuntimeException implements WithSelfUserNotificationException {

    public final int actualStatusCode;
    public final int expectedStatusCode;

    private static String getBody(HttpEntity entity) {
        try {
            return entity == null ? null : EntityUtils.toString(entity);
        } catch (Exception ignored) {
            return null;
        }
    }

    protected UnexpectedResponseStatusCodeException(String message, int actualStatusCode, int expectedStatusCode) {
        super(message);
        this.actualStatusCode = actualStatusCode;
        this.expectedStatusCode = expectedStatusCode;
    }

    public UnexpectedResponseStatusCodeException(ClassicHttpResponse response, int expectedStatusCode) {
        this(
                "Faced with unexpected response status code (Expected: %s; Actual: %s; Body: %s)".formatted(
                        expectedStatusCode, response.getCode(), getBody(response.getEntity())
                ),
                response.getCode(),
                expectedStatusCode
        );
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Безуспешная обработка API ответа",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
