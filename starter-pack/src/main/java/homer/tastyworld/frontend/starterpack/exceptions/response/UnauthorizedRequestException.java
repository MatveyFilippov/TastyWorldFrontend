package homer.tastyworld.frontend.starterpack.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.ui.ErrorAlert;

public class UnauthorizedRequestException extends ResponseException {

    public UnauthorizedRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        ErrorAlert.showAlert("Unauthorized", response.error, true);
    }

}
