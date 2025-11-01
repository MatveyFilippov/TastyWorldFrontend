package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.initialization;

public class CantInitAppLoggerException extends RuntimeException {

    public CantInitAppLoggerException(Throwable throwable) {
        super("Can't init AppLogger", throwable);
    }

}
