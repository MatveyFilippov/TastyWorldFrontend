package homer.tastyworld.frontend.starterpack.base.exceptions.api.engine;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class UnexpectedResponseStatusCodeException extends RuntimeException implements WithSelfUserNotificationException {

    public final ClassicHttpResponse response;
    public final int actualStatusCode;
    public final int expectedStatusCode;

    public UnexpectedResponseStatusCodeException(String message, ClassicHttpResponse response, int expectedStatusCode) {
        super(message);
        this.response = response;
        this.actualStatusCode = response.getCode();
        this.expectedStatusCode = expectedStatusCode;
    }

    public UnexpectedResponseStatusCodeException(ClassicHttpResponse response, int expectedStatusCode) {
        this(
                "Faced with unexpected response status code (Expected: %s; Actual: %s)".formatted(
                        expectedStatusCode, response.getCode()
                ),
                response,
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
