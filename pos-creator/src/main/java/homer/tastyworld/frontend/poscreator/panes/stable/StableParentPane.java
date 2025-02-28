package homer.tastyworld.frontend.poscreator.panes.stable;

import homer.tastyworld.frontend.poscreator.panes.ParentPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class StableParentPane extends ParentPane {

    public abstract void initialize();

}
