package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleState;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import javafx.application.Platform;
import javafx.beans.binding.StringExpression;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.util.Objects;

public class EditItemQtyPane {

    private static Long itemID = null;
    private static Integer startQTY = null;
    private static Integer qty = null;
    private static AnchorPane current;
    private static Label itemName, itemQTY;
    private static Thread scaleAskingThread;

    public static boolean isEdit() {
        return !Objects.equals(startQTY, qty);
    }

    public static Long getItemID() {
        return itemID;
    }

    public static Integer getQTY() {
        return qty;
    }

    private static void setQTY(Integer qty) {
        EditItemQtyPane.qty = qty;
        itemQTY.setText(String.valueOf(qty));
    }

    private static void startAskingScale() {
        if (!ScaleManager.IS_SCALE_AVAILABLE) {
            return;
        }
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
                        Platform.runLater(() -> setQTY(finalWeight));
                        return;
                    }
                }
            } catch (InterruptedException ignored) {}
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

    public static void open(long itemID, int qty, String name) {
        EditItemQtyPane.itemID = itemID;
        startQTY = qty;
        setQTY(qty);
        itemName.setText(name);
        current.setVisible(true);
        startAskingScale();
    }

    public static void close() {
        stopAskingScale();
        itemID = null;
        itemName.setText("null");
        startQTY = null;
        setQTY(null);
        current.setVisible(false);
    }

    private static AnchorPane getClickableNumberKbBtn(int num, StringExpression fontSize) {
        String toAppend = String.valueOf(num);
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color: #555555; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        AdaptiveTextHelper.setTextCentre(btn, toAppend, fontSize, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(qty);
            setQTY(Integer.parseInt(oldQTY.equals("0") ? toAppend : oldQTY + toAppend));
        });
        return btn;
    }

    private static AnchorPane getClickableShiftBackKbBtn() {
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color: #555555; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        AdaptiveTextHelper.setTextCentre(btn, "<--", 3, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(qty);
            int len = oldQTY.length();
            setQTY(len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1)));
        });
        return btn;
    }

    private static void initKeyboard(GridPane numbersKeyboard) {
        AnchorPane shiftBackBtn = getClickableShiftBackKbBtn();
        StringExpression numbersFontSize = AdaptiveTextHelper.getFontSize(shiftBackBtn, 3);
        for (int i = 0; i < 9; i++) {
            numbersKeyboard.add(getClickableNumberKbBtn(i + 1, numbersFontSize), i % 3, i / 3);
        }
        numbersKeyboard.add(getClickableNumberKbBtn(0, numbersFontSize), 3, 0);
        numbersKeyboard.add(shiftBackBtn, 3, 1);
    }

    public static void init(AnchorPane current, AnchorPane itemName, AnchorPane itemQTY, GridPane numbersKeyboard) {
        EditItemQtyPane.current = current;
        EditItemQtyPane.itemName = AdaptiveTextHelper.setTextCentre(itemName, "null", 10, null);
        EditItemQtyPane.itemQTY = AdaptiveTextHelper.setTextCentre(itemQTY, "null", 10, null);
        initKeyboard(numbersKeyboard);
    }

}
