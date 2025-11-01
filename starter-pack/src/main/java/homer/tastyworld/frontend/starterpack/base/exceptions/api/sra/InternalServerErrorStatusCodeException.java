package homer.tastyworld.frontend.starterpack.base.exceptions.api.sra;

import homer.tastyworld.frontend.starterpack.api.sra.ErrorResponseBody;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class InternalServerErrorStatusCodeException extends ErrorResponseStatusCodeException {

    public InternalServerErrorStatusCodeException(ClassicHttpResponse response, int expectedStatusCode, ErrorResponseBody errorResponseBody) {
        super(response, expectedStatusCode, errorResponseBody);
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Внешняя ошибка",
                "Произошла ошибка в облаке, разработчики уже получили уведомление",
                true
        );
    }

}
