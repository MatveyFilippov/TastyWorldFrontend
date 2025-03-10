package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.panes.stable.StableParentPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class DynamicParentPane extends StableParentPane {

    protected abstract String getCacheProcess(int total, int actual);

    protected abstract void cacheTask(Long id);

    public void cacheAll(Long[] ids) {  // TODO: cache in thread
        int total = ids.length;
        for (int i = 0; i < total; i++) {
            System.out.print("\r" + getCacheProcess(total, i));
            cacheTask(ids[i]);
        }
        System.out.println("\r" + getCacheProcess(total, total));
    };

    public abstract void fill(long id);

    protected abstract void cleanTask();

    public void clean() {  // TODO: clean in thread
        cleanTask();
    }

}
