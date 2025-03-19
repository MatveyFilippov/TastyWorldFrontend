package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import java.io.InputStream;

public class PhotoRequest extends Request {

    public PhotoRequest(String endpoint) {
        super(endpoint, null);
    }

    public InputStream read() {
        return Requester.exchangeImage(getURL(endpoint), getToken(), body);
    }

    @Deprecated
    @Override
    public Response request() {
        return null;
    }

}
