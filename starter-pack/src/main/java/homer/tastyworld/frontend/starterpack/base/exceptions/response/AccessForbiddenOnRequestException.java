package homer.tastyworld.frontend.starterpack.base.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class AccessForbiddenOnRequestException extends ResponseException {

    public AccessForbiddenOnRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        AlertWindow.showError("AccessForbidden", response.error, true);
    }

}
