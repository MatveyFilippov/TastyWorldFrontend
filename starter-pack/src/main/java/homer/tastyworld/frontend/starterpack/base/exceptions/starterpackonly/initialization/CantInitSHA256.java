package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.initialization;

public class CantInitSHA256 extends RuntimeException {

    public CantInitSHA256(Throwable throwable) {
        super("Can't init SHA-256 algorithm", throwable);
    }

}
