package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class CantParseResponseBodyException extends UnexpectedException {

    public CantParseResponseBodyException(Exception ex) {
        super(ex);
    }

}
