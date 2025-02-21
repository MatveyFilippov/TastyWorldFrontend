package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class CantParseRequestBodyException extends UnexpectedException {

    public CantParseRequestBodyException(String requestBody) {
        super("invalid body: " + requestBody);
    }

}
