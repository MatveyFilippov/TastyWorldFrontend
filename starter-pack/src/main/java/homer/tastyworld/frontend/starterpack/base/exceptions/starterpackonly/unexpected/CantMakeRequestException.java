package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class CantMakeRequestException extends UnexpectedException {

    public CantMakeRequestException(Exception ex) {
        super(ex);
    }

}
