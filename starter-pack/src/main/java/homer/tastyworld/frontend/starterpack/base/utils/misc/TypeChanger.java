package homer.tastyworld.frontend.starterpack.base.utils.misc;

import java.util.List;

public class TypeChanger {

    public static String toDaysFormat(long days) {
        if (days % 10 == 1 && days % 100 != 11) {
            return days + " день";
        } else if (days % 10 >= 2 && days % 10 <= 4 && (days % 100 < 10 || days % 100 >= 20)) {
            return days + " дня";
        } else {
            return days + " дней";
        }
    }

    public static Long[] toLongArray(Object object) {
        return ((List<Object>) object).stream().map(obj -> Long.parseLong(String.valueOf(obj))).toArray(Long[]::new);
    }

}
