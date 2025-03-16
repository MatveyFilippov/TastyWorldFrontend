package homer.tastyworld.frontend.pos.processor.core.helpers;

import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.ScaleState;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class EditItemQtyPane {

    public static Long itemID = null;
    public static Integer qty = null;
    private static AnchorPane editItemQtyPaneParent;
    private static Label editItemQtyPaneNameTopic, editItemQtyPaneTotalTopic;
    private static Thread thread;

    private static void setQTY(Integer qty) {
        EditItemQtyPane.qty = qty;
        editItemQtyPaneTotalTopic.setText(String.valueOf(qty));
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
        setQTY(qty);
        editItemQtyPaneNameTopic.setText(name);
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
        editItemQtyPaneNameTopic.setText("null");
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

    private static void initKeyboard(GridPane editItemQtyPaneNumbersKeyboard) {
        for (int i = 0; i < 9; i++) {
            editItemQtyPaneNumbersKeyboard.add(getClickableNumberKbBtn(String.valueOf(i + 1)), i % 3, i / 3);
        }
        editItemQtyPaneNumbersKeyboard.add(getClickableNumberKbBtn("0"), 3, 0);
        editItemQtyPaneNumbersKeyboard.add(getClickableShiftBackKbBtn(), 3, 1);
    }

    public static void init(AnchorPane editItemQtyPaneParent, AnchorPane editItemQtyPaneNameTopic, AnchorPane editItemQtyPaneTotalTopic, GridPane editItemQtyPaneNumbersKeyboard) {
        TextHelper.setTextCentre(editItemQtyPaneNameTopic, "null", TextHelper.getAdaptiveFontSize(editItemQtyPaneNameTopic, 12), null);
        EditItemQtyPane.editItemQtyPaneNameTopic = (Label) editItemQtyPaneNameTopic.getChildren().getLast();
        TextHelper.setTextCentre(editItemQtyPaneTotalTopic, "null", TextHelper.getAdaptiveFontSize(editItemQtyPaneTotalTopic, 10), null);
        EditItemQtyPane.editItemQtyPaneTotalTopic = (Label) editItemQtyPaneTotalTopic.getChildren().getLast();
        EditItemQtyPane.editItemQtyPaneParent = editItemQtyPaneParent;
        initKeyboard(editItemQtyPaneNumbersKeyboard);
    }

}
