package homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier;

public class CantCreateOrderWithoutPositionsException extends RuntimeException {

    public CantCreateOrderWithoutPositionsException() {
        super("Can't create order in Evotor Mobile-Cashier because list of positions is empty");
    }

}
