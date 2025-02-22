package homer.tastyworld.frontend.starterpack.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.ui.ErrorAlert;

public class NotFoundRequestException extends ResponseException {

    public NotFoundRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        String text = (response.error != null ? "Error: " + response.error + "\n" : "")
                      + (response.note != null ? "Note: " + response.note : "");
        ErrorAlert.showAlert("NotFound", text, true);
    }

}
