package homer.tastyworld.frontend.pos.creator.core.orders;

import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.names.NameController;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.names.NumericalNameController;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.sra.NotFoundStatusCodeException;

public class OrderCreator {

    private static final AppLogger logger = AppLogger.getFor(OrderCreator.class);
    private static final NameController nameController = new NumericalNameController(1, 99);
    private static Order creating = null;

    public static void start(boolean continueIfExists) {
        if (creating != null) {
            if (continueIfExists) {
                return;
            } else {
                cancel();
            }
        }
        creating = Order.draft(nameController, null, null, null);
    }

    public static void cancel() {
        if (creating != null) {
            try {
                creating.delete(nameController);
            } catch (UnexpectedResponseStatusCodeException ex) {
                if (!(ex instanceof NotFoundStatusCodeException)) {
                    logger.error("Something is wrong when canceling the order creation", ex);
                }
            }
            creating = null;
        }
    }

    public static void finish() {
        creating = null;
    }

    public static Order get() {
        if (creating == null) {
            throw new NullPointerException("The order being created does not exist");
        }
        return creating;
    }

}
