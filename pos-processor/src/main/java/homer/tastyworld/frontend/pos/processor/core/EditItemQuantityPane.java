package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.scale.ScaleManager;
import homer.tastyworld.frontend.starterpack.utils.managers.external.scale.ScaleState;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import javafx.application.Platform;
import javafx.beans.binding.StringExpression;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class EditItemQuantityPane {

    private static AnchorPane current;
    private static Label nameLabel, quantityLabel;
    private static Thread scaleAskingThread;
    private static OrderItem item;
    private static Integer quantity;

    public static OrderItem getItem() {
        if (item == null) {
            throw new NullPointerException("The order item being edited does not exist");
        }
        return item;
    }

    public static int getQuantity() {
        if (quantity == null) {
            throw new NullPointerException("The quantity of order item being edited does not exist");
        }
        return quantity;
    }

    private static void setQuantity(Integer quantity) {
        EditItemQuantityPane.quantity = quantity;
        String qtyMeasureShortName = item == null ? "?" : item.qtyMeasure().shortName;
        quantityLabel.setText(quantity + " " + qtyMeasureShortName);
    }

    private static void startAskingScale() {
        scaleAskingThread = new Thread(() -> {
            try (ScaleManager scaleManager = new ScaleManager()) {
                while (true) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    ScaleState state = scaleManager.getScaleState();
                    if (state.STATUS == ScaleState.Status.STABLE && state.WEIGHT > 0) {
                        int weight = 0;
                        if (state.UNIT == ScaleState.Unit.KG) {
                            weight = (int) (state.WEIGHT * 1000);
                        }
                        if (weight == 0) {
                            continue;
                        }
                        final int finalWeight = weight;
                        Platform.runLater(() -> setQuantity(finalWeight));
                        return;
                    }
                }
            } catch (InterruptedException | ExternalModuleUnavailableException ignored) {}
        });
        scaleAskingThread.setDaemon(true);
        scaleAskingThread.setName("Scale asking while edit item qty");
        scaleAskingThread.start();
    }

    private static void stopAskingScale() {
        if (scaleAskingThread != null && scaleAskingThread.isAlive()) {
            scaleAskingThread.interrupt();
            scaleAskingThread = null;
        }
    }

    public static void open(OrderItem item) {
        EditItemQuantityPane.item = item;
        setQuantity(item.quantity());
        nameLabel.setText(item.name());
        current.setVisible(true);
        startAskingScale();
    }

    public static void close() {
        stopAskingScale();
        item = null;
        nameLabel.setText("Продукт не выбран");
        setQuantity(null);
        current.setVisible(false);
    }

    private static AnchorPane getClickableNumberKbBtn(int num, StringExpression fontSize) {
        AnchorPane btn = new AnchorPane();
        btn.getStyleClass().add("numpad-button");
        String toAppend = String.valueOf(num);
        AdaptiveTextHelper.setTextCentre(btn, toAppend, fontSize, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(quantity);
            setQuantity(Integer.parseInt(oldQTY.equals("0") ? toAppend : oldQTY + toAppend));
        });
        return btn;
    }

    private static AnchorPane getClickableShiftBackNumberKbBtn() {
        AnchorPane btn = new AnchorPane();
        btn.getStyleClass().add("numpad-button");
        AdaptiveTextHelper.setTextCentre(btn, "<--", 0.33, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(quantity);
            int len = oldQTY.length();
            setQuantity(len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1)));
        });
        return btn;
    }

    private static void initNumbersKeyboard(GridPane numbersKeyboardGrid) {
        AnchorPane shiftBackBtn = getClickableShiftBackNumberKbBtn();
        StringExpression numbersFontSize = AdaptiveTextHelper.getFontSize(shiftBackBtn, 0.33);
        for (int i = 0; i < 9; i++) {
            numbersKeyboardGrid.add(getClickableNumberKbBtn(i + 1, numbersFontSize), i % 3, i / 3);
        }
        numbersKeyboardGrid.add(getClickableNumberKbBtn(0, numbersFontSize), 3, 0);
        numbersKeyboardGrid.add(shiftBackBtn, 3, 1);
    }

    public static void init(AnchorPane current, AnchorPane nameTopic, AnchorPane quantityTopic, GridPane numbersKeyboardGrid) {
        EditItemQuantityPane.current = current;
        EditItemQuantityPane.nameLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "Продукт не выбран", 0.08, null);
        EditItemQuantityPane.quantityLabel = AdaptiveTextHelper.setTextCentre(quantityTopic, "null", 0.1, null);
        initNumbersKeyboard(numbersKeyboardGrid);
    }

}
