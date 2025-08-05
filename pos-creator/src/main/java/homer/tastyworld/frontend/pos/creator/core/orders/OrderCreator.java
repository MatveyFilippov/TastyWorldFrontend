package homer.tastyworld.frontend.pos.creator.core.orders;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.names.NameController;
import homer.tastyworld.frontend.starterpack.order.core.names.NumericalNameController;
import java.util.Objects;

public class OrderCreator {

    private static final AppLogger logger = AppLogger.getFor(OrderCreator.class);
    private static final NameController NAME_CONTROLLER = new NumericalNameController(1, 99);
    private static Order creating = null;

    public static void start(boolean continueIfExists) {
        if (creating != null) {
            if (continueIfExists) {
                return;
            } else {
                cancel();
            }
        }
        creating = Order.create(NAME_CONTROLLER, null);
    }

    public static void cancel() {
        if (creating != null) {
            try {
                creating.delete(NAME_CONTROLLER);
            } catch (BadRequestException ex) {
                if (!Objects.equals(ex.response.error(), "ExistenceException: The Order with id '%s' probably doesn't exist".formatted(creating.id))) {
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
        return creating;
    }

}
