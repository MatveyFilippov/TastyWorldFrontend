package homer.tastyworld.frontend.starterpack.utils.managers.external.payment;

import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;

public class EvotorMobcashierChecker extends ExternalModuleChecker {

    @Override
    public void check() {
        try {
            EvotorMobcashier.getOrderCreator();
        } catch (ExternalModuleUnavailableException ignored) {}
    }

}
