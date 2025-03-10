package homer.tastyworld.frontend.pos.creator.panes;

import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ParentPane {

    protected static AnchorPane base;
    protected AnchorPane parent;

    public static void setBase(AnchorPane base) {
        ParentPane.base = base;
    }

    public AnchorPane getParent() {
        return parent;
    }

    public void openAndCloseOther() {
        base.getChildren().forEach(node -> {
            if (node instanceof AnchorPane && node.getId().endsWith("Parent")) {
                node.setVisible(false);
            }
        });
        parent.setVisible(true);
    }

    public void openAndCloseFrom(AnchorPane from) {
        if (from != null) {
            from.setVisible(false);
        }
        parent.setVisible(true);
    }

}
