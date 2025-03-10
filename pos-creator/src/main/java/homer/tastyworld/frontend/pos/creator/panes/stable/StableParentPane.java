package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class StableParentPane extends ParentPane {

    public abstract void initialize();

}
