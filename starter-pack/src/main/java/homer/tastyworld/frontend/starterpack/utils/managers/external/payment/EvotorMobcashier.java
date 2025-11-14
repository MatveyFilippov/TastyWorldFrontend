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
import java.math.RoundingMode;
import java.util.Arrays;
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
        return switch (menuQuantitativeMeasure) {
            case PIECES -> "шт";
            case GRAMS -> "г";
            default -> "иные ед измерения";
        };
    }

    private static void addOrderItemModifierToOrderCreator(OrderItemModifier modifier, String tax, OrderCreator creator) {
        if (modifier.quantity() > modifier.qtyDefault() && modifier.unitPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal modifierOverQTY = BigDecimal.valueOf(modifier.quantity() - modifier.qtyDefault());
            creator.addPosition(
                    " + " + modifier.name(),
                    modifier.unitPrice(),
                    getMeasureName(modifier.qtyMeasure()),
                    modifierOverQTY,
                    tax,
                    ProductType.NORMAL.name(),
                    null
            );
        }
    }

    private static void addPieceOrderItemToOrderCreator(OrderItem item, OrderCreator creator) {
        OrderItemModifier[] notDefaultModifiers = item.notDefaultModifiers();
        String measureName = getMeasureName(item.qtyMeasure());
        if (notDefaultModifiers.length == 0) {
            creator.addPosition(
                    item.name(),
                    item.unitPrice(),
                    measureName,
                    BigDecimal.valueOf(item.quantity()),
                    item.tax().name(),
                    item.type().name(),
                    item.mark()
            );
        } else {
            for (int i = 0; i < item.quantity(); i++) {
                creator.addPosition(
                        item.name(),
                        item.unitPrice(),
                        measureName,
                        BigDecimal.ONE,
                        item.tax().name(),
                        item.type().name(),
                        item.mark()
                );
                for (OrderItemModifier modifier : notDefaultModifiers) {
                    addOrderItemModifierToOrderCreator(modifier, item.tax().name(), creator);
                }
            }
        }
    }

    private static void addWeightOrderItemToOrderCreator(OrderItem item, OrderCreator creator) {
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
            addOrderItemModifierToOrderCreator(modifier, item.tax().name(), creator);
        }
    }

    private static void addOrderItemToOrderCreator(OrderItem item, OrderCreator creator) {
        if (item.qtyMeasure() == MenuQuantitativeMeasure.PIECES) {
            addPieceOrderItemToOrderCreator(item, creator);
        } else {
            addWeightOrderItemToOrderCreator(item, creator);
        }
    }

    private static BigDecimal getOrderDiscount(Order order) {
        BigDecimal discount = order.getDiscount();
        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal total = Arrays.stream(order.getItems())
                                 .map(OrderItem::totalPrice)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (discount.compareTo(BigDecimal.ONE) == 0) {
            return total.subtract(BigDecimal.ONE);
        }
        return total.multiply(discount).setScale(2, RoundingMode.HALF_DOWN);
    }

    public static void sendToCashRegister(Order order) throws ExternalModuleUnavailableException {
        OrderCreator creator = getOrderCreator();
        for (OrderItem item : order.getItems()) {
            addOrderItemToOrderCreator(item, creator);
        }
        creator.post(order.getOrderID(), order.getName(), getOrderDiscount(order), order.getDeliveryInfo());
    }

}
