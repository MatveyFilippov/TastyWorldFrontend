package homer.tastyworld.frontend.starterpack.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.ui.ErrorAlert;

public class AccessForbiddenOnRequestException extends ResponseException {

    public AccessForbiddenOnRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        ErrorAlert.showAlert("AccessForbidden", response.error, true);
    }

}
