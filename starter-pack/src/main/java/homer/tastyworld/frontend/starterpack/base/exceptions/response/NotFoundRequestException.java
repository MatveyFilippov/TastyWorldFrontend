package homer.tastyworld.frontend.starterpack.base.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class NotFoundRequestException extends ResponseException {

    public NotFoundRequestException(Response response) {
        super(response);
    }

    @Override
    protected void action() {
        String text = (response.error != null ? "Error: " + response.error + "\n" : "")
                      + (response.note != null ? "Note: " + response.note : "");
        AlertWindow.showError("NotFound", text, true);
    }

}
