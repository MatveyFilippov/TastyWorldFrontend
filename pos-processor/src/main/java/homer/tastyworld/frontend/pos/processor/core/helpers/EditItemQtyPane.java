package homer.tastyworld.frontend.pos.processor.core.helpers;

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

    public static Long itemID = null;
    private static Integer startQTY = null;
    public static Integer qty = null;
    private static AnchorPane parent;
    private static Label itemName, itemQTY;
    private static Thread thread;

    public static boolean isEdit() {
        return !Objects.equals(startQTY, qty);
    }

    private static void setQTY(Integer qty) {
        EditItemQtyPane.qty = qty;
        itemQTY.setText(String.valueOf(qty));
    }

    private static void startAskingScale() {
        thread = new Thread(() -> {
            try (ScaleManager scaleManager = new ScaleManager()) {
                while (true) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    ScaleState state = scaleManager.getScaleState();
                    if (state.STATUS == ScaleState.Status.STABLE) {
                        int weight = 0;
                        if (state.UNIT == ScaleState.Unit.KG) {
                            weight = (int) (state.WEIGHT * 1000);
                        }
                        final int finalWeight = weight;
                        Platform.runLater(() -> setQTY(finalWeight));
                        return;
                    }
                }
            } catch (InterruptedException ignored) {}
        });
        thread.setDaemon(true);
        thread.setName("Scale asking while edit item qty");
        thread.start();
    }

    public static void open(long itemID, int qty, String name) {
        EditItemQtyPane.itemID = itemID;
        startQTY = qty;
        setQTY(qty);
        itemName.setText(name);
        parent.setVisible(true);
        if (ScaleManager.IS_SCALE_AVAILABLE) {
            startAskingScale();
        }
    }

    public static void close() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
        itemID = null;
        itemName.setText("null");
        startQTY = null;
        setQTY(null);
        parent.setVisible(false);
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

    public static void init(AnchorPane parent, AnchorPane itemName, AnchorPane itemQTY, GridPane numbersKeyboard) {
        EditItemQtyPane.parent = parent;
        EditItemQtyPane.itemName = AdaptiveTextHelper.setTextCentre(itemName, "null", 10, null);
        EditItemQtyPane.itemQTY = AdaptiveTextHelper.setTextCentre(itemQTY, "null", 10, null);
        initKeyboard(numbersKeyboard);
    }

}
