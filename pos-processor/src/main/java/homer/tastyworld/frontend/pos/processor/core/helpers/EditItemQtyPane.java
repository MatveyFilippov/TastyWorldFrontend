package homer.tastyworld.frontend.pos.processor.core.helpers;

import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleState;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class EditItemQtyPane {

    public static Long itemID = null;
    public static Integer qty = null;
    private static AnchorPane editItemQtyPaneParent;
    private static Label editItemQtyTopic;
    private static Thread thread;

    private static void setQTY(Integer qty) {
        EditItemQtyPane.qty = qty;
        editItemQtyTopic.setText(String.valueOf(qty));
    }

    private static void startAskingScale() {
        thread = new Thread(() -> {
            try (ScaleManager scaleManager = new ScaleManager()) {
                while (true) {
                    if (!Thread.interrupted()) {
                        ScaleState state = scaleManager.getScaleState();
                        if (state.STATUS == ScaleState.Status.STABLE) {
                            int weight = 0;
                            if (state.UNIT == ScaleState.Unit.KG) {
                                weight = (int) (state.WEIGHT * 1000);
                            }
                            setQTY(weight);
                            return;
                        }
                    }
                }
            } catch (InterruptedException ignored) {}
        });
        thread.setDaemon(true);
        thread.setName("Scale asking thread");
        thread.start();
    }

    public static void open(long itemID, int qty) {
        EditItemQtyPane.itemID = itemID;
        setQTY(qty);
        editItemQtyPaneParent.setVisible(true);
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
        setQTY(null);
        editItemQtyPaneParent.setVisible(false);
    }

    private static AnchorPane getClickableNumberKbBtn(String toAppend) {
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color:  #000000; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        TextHelper.setTextCentre(btn, toAppend, TextHelper.getAdaptiveFontSize(btn, 2), null);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(qty);
            setQTY(Integer.parseInt(oldQTY.equals("0") ? toAppend : oldQTY + toAppend));
        });
        return btn;
    }

    private static AnchorPane getClickableShiftBackKbBtn() {
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color:  #000000; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        TextHelper.setTextCentre(btn, "<--", TextHelper.getAdaptiveFontSize(btn, 4), null);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(qty);
            int len = oldQTY.length();
            setQTY(len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1)));
        });
        return btn;
    }

    private static void initKeyboard(GridPane editItemQtyNumbersKeyboard) {
        for (int i = 0; i < 9; i++) {
            editItemQtyNumbersKeyboard.add(getClickableNumberKbBtn(String.valueOf(i + 1)), i % 3, i / 3);
        }
        editItemQtyNumbersKeyboard.add(getClickableNumberKbBtn("0"), 3, 0);
        editItemQtyNumbersKeyboard.add(getClickableShiftBackKbBtn(), 3, 1);
    }

    public static void init(AnchorPane editItemQtyPaneParent, AnchorPane editItemQtyTopic, GridPane editItemQtyNumbersKeyboard) {
        TextHelper.setTextCentre(editItemQtyTopic, "null", TextHelper.getAdaptiveFontSize(editItemQtyTopic, 10), null);
        EditItemQtyPane.editItemQtyTopic = (Label) editItemQtyTopic.getChildren().getLast();
        EditItemQtyPane.editItemQtyPaneParent = editItemQtyPaneParent;
        initKeyboard(editItemQtyNumbersKeyboard);
    }

}
