package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.init;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;

public class CantInitAppConfigException extends SelfLoggedException {

    public CantInitAppConfigException(String message) {
        super(message);
    }

    @Override
    protected void logExtends() {}

}
