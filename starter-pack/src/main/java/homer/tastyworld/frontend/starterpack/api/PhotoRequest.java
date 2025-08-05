package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.api.engine.RequestCreator;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import org.apache.hc.core5.http.Method;
import java.io.InputStream;

public class PhotoRequest extends RequestCreator {

    public PhotoRequest(String endpoint) {
        super(endpoint, Method.GET);
    }

    public InputStream read() {
        return Requester.exchangeStream(this);
    }

    public PhotoResponse request() {
        return Requester.exchangeImage(this);
    }

}
