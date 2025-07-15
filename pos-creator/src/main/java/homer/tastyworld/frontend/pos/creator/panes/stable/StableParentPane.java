package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class StableParentPane extends ParentPane {

    public abstract void initialize();

}
