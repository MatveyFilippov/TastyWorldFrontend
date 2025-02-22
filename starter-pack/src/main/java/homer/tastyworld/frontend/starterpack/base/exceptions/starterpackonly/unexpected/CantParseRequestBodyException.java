package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class CantParseRequestBodyException extends UnexpectedException {

    public CantParseRequestBodyException(String requestBody) {
        super("invalid body: " + requestBody);
    }

}
