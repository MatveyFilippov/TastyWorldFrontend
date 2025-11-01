package homer.tastyworld.frontend.pos.creator.panes;

import javafx.scene.layout.AnchorPane;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class ParentPane<D> {

    protected static AnchorPane base;
    protected final AnchorPane current;

    public abstract void initialize();

    public static void setBase(AnchorPane base) {
        ParentPane.base = base;
    }

    protected abstract void beforeOpen(D data);

    public final void open(D data) {
        beforeOpen(data);
        current.setVisible(true);
    }

    protected abstract void beforeClose();

    public final void close() {
        beforeClose();
        current.setVisible(false);
    }

    public final void openAndCloseFrom(ParentPane<?> from, D data) {
        from.close();
        open(data);
    }

}
