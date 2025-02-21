package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class CantMakeRequestException extends UnexpectedException {

    public CantMakeRequestException(Exception ex) {
        super(ex);
    }

}
