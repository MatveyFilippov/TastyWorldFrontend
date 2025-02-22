package homer.tastyworld.frontend.starterpack.base.utils.misc;

import java.util.List;

public class TypeChanger {

    public static Long[] toLongArray(Object object) {
        return ((List<Object>) object).stream().map(obj -> Long.parseLong(String.valueOf(obj))).toArray(Long[]::new);
    }

}
