package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class UnknownResponseCodeException extends UnexpectedException {

    public UnknownResponseCodeException(int code) {
        super(String.valueOf(code));
    }

}
