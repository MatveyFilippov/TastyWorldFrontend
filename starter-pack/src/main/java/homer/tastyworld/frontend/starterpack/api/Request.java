package homer.tastyworld.frontend.starterpack.api;

import homer.tastyworld.frontend.starterpack.api.engine.RequestCreator;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import org.apache.hc.core5.http.Method;

public class Request extends RequestCreator {

    public Request(String endpoint, Method method) {
        super(endpoint, method);
    }

    public Response request() {
        return Requester.exchange(this);
    }

}
