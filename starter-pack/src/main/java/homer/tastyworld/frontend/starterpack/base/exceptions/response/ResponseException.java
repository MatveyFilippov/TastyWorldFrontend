package homer.tastyworld.frontend.starterpack.base.exceptions.response;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;

public abstract class ResponseException extends DisplayedException {

    public final Response response;

    public ResponseException(Response response) {
        super("Encountered a 'Not OK' status in the response: " + response);
        this.response = response;
    }

}
