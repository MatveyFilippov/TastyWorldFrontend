package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.panes.stable.StableParentPane;
import lombok.experimental.SuperBuilder;
import java.util.Arrays;

@SuperBuilder
public abstract class DynamicParentPane extends StableParentPane {

    protected abstract void cacheTask(long id);

    public void cacheAll(long[] ids) {  // TODO: cache in thread
        Arrays.stream(ids).forEach(this::cacheTask);
    }

    public abstract void fill(long id);

    protected abstract void cleanTask();

    public void clean() {  // TODO: clean in thread
        cleanTask();
    }

}
