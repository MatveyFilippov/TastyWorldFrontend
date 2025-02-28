package homer.tastyworld.frontend.poscreator.panes.dynamic;

import homer.tastyworld.frontend.poscreator.panes.stable.StableParentPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class DynamicParentPane extends StableParentPane {

    public abstract void cacheAll(Long[] ids);

    public abstract void fill(long id);

    public abstract void clean();

}
