package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class CantParseResponseBodyException extends UnexpectedException {

    public CantParseResponseBodyException(Exception ex) {
        super(ex);
    }

}
