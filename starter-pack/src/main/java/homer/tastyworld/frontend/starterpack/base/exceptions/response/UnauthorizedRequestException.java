package homer.tastyworld.frontend.starterpack.base.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class UnauthorizedRequestException extends ResponseException {

    public UnauthorizedRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        AlertWindow.showError("Unauthorized", response.error, true);
    }

}
