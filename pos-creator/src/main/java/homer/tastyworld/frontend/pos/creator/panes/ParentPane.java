package homer.tastyworld.frontend.pos.creator.panes;

import javafx.scene.layout.AnchorPane;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ParentPane {

    protected static AnchorPane base;
    protected AnchorPane current;

    public static void setBase(AnchorPane base) {
        ParentPane.base = base;
    }

    public void open() {
        current.setVisible(true);
    }

    public void openAndCloseOther() {
        base.getChildren().forEach(node -> {
            if (node instanceof AnchorPane && node.getId().endsWith("Parent")) {
                node.setVisible(false);
            }
        });
        open();
    }

    public void openAndCloseFrom(AnchorPane from) {
        if (from != null) {
            from.setVisible(false);
        }
        open();
    }

}
