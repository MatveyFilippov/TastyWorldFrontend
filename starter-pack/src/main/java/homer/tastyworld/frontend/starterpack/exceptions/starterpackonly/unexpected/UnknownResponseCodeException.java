package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class UnknownResponseCodeException extends UnexpectedException {

    public UnknownResponseCodeException(int code) {
        super(String.valueOf(code));
    }

}
