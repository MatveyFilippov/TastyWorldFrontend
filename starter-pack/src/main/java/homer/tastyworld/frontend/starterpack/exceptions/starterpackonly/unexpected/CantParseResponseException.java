package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class CantParseResponseException extends UnexpectedException {

    public CantParseResponseException(Exception ex) {
        super(ex);
    }

}
