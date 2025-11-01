package homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier;

public class NoDataToCreateIntegrationException extends RuntimeException {

    public NoDataToCreateIntegrationException() {
        super("Can't create Evotor Mobile-Cashier integration because some of required data is not exists");
    }

}
