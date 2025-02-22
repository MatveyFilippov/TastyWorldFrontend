package homer.tastyworld.frontend.starterpack.base.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.ui.ErrorAlert;

public class AccessForbiddenOnRequestException extends ResponseException {

    public AccessForbiddenOnRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        ErrorAlert.showAlert("AccessForbidden", response.error, true);
    }

}
