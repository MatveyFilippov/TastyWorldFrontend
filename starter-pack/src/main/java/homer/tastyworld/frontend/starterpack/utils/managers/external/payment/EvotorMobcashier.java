package homer.tastyworld.frontend.starterpack.utils.managers.external.payment;

import homer.tastyworld.frontend.starterpack.api.evotor.mobcashier.OrderCreator;
import homer.tastyworld.frontend.starterpack.api.evotor.mobcashier.authorize.Integration;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductType;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier.NoDataToCreateIntegrationException;
import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.util.Optional;

public class EvotorMobcashier {

    private static final AppLogger logger = AppLogger.getFor(EvotorMobcashier.class);

    private static ExternalModuleUnavailableException notifyUserIfRequiredAndGetExternalModuleUnavailableException(@Nullable Throwable throwable) {
        String error = "Try to use Evotor Mobile-Cashier, but it is unavailable (can't get authorization token or android-id)";
        if (AppConfig.getEvotorAccountPhone() != null || AppConfig.getEvotorMobcashierIdentifierSecret() != null) {
            logger.error(error, throwable);
            AlertWindows.showError("Ошибка кассы", "Не могу подключиться к Эвотору, обратитесь за помощью к разарботчикам", false);
        }
        return throwable != null ? new ExternalModuleUnavailableException(error, throwable) : new ExternalModuleUnavailableException(error);
    }

    static OrderCreator getOrderCreator() throws ExternalModuleUnavailableException {
        Optional<String> optionalToken, optionalAndroidID;
        try {
            optionalToken = Integration.getToken();
            optionalAndroidID = Integration.getAndroidID();
        } catch (NoDataToCreateIntegrationException ex) {
            throw notifyUserIfRequiredAndGetExternalModuleUnavailableException(ex);
        }
        if (optionalToken.isEmpty() || optionalAndroidID.isEmpty()) {
            throw notifyUserIfRequiredAndGetExternalModuleUnavailableException(null);
        }
        return new OrderCreator(optionalAndroidID.get(), optionalToken.get());
    }

    private static String getMeasureName(MenuQuantitativeMeasure menuQuantitativeMeasure) {
        // return switch (menuQuantitativeMeasure) {
        //     case MenuQuantitativeMeasure.PIECES -> "шт";
        //     case MenuQuantitativeMeasure.GRAMS -> "г";
        //     default -> "иные ед измерения";
        // };
        if (menuQuantitativeMeasure == MenuQuantitativeMeasure.PIECES) {
            return "шт";
        } else if (menuQuantitativeMeasure == MenuQuantitativeMeasure.GRAMS) {
            return "г";
        } else {
            return "иные ед измерения";
        }
    }

    private static void addOrderItemToOrderCreator(OrderItem item, OrderCreator creator) {
        creator.addPosition(
                item.name(),
                item.unitPrice(),
                getMeasureName(item.qtyMeasure()),
                BigDecimal.valueOf(item.quantity()),
                item.tax().name(),
                item.type().name(),
                item.mark()
        );
        for (OrderItemModifier modifier : item.notDefaultModifiers()) {
            if (modifier.quantity() > modifier.qtyDefault() && modifier.unitPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal modifierOverQTY = BigDecimal.valueOf(modifier.quantity() - modifier.qtyDefault());
                creator.addPosition(
                        " + " + modifier.name(),
                        modifier.unitPrice(),
                        getMeasureName(modifier.qtyMeasure()),
                        modifierOverQTY,
                        item.tax().name(),
                        ProductType.NORMAL.name(),
                        null
                );
            }
        }
    }

    public static void sendToCashRegister(Order order) throws ExternalModuleUnavailableException {
        OrderCreator creator = getOrderCreator();
        for (OrderItem item : order.getItems()) {
            addOrderItemToOrderCreator(item, creator);
        }
        BigDecimal discount = order.getDiscount();
        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            discount = null;
        }
        creator.post(order.getOrderID(), order.getName(), discount, order.getDeliveryInfo());
    }

}
