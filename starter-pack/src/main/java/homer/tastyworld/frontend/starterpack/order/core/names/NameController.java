package homer.tastyworld.frontend.starterpack.order.core.names;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.utils.misc.Randomizer;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import org.apache.hc.core5.http.Method;
import java.util.Optional;

public abstract class NameController {

    private static final AppLogger logger = AppLogger.getFor(NameController.class);
    private static final Request CHECK_REQUEST = new Request("/order/is_name_in_active_order", Method.GET);

    public static boolean isFree(String name) {
        CHECK_REQUEST.putInBody("name", name);
        Response response = CHECK_REQUEST.request();
        return !TypeChanger.toBool(response.result);
    }

    private static String generateFreeRandomName() {
        int nameLength = 5;
        while (true) {
            for (int i = 0; i < Math.pow(nameLength, Randomizer.CHARACTERS_LENGTH); i++) {
                String randomName = Randomizer.getString(nameLength);
                if (isFree(randomName)) {
                    return randomName;
                }
            }
            nameLength++;
        }
    }

    protected abstract Optional<String> tryToFindFreeName();

    public abstract void back();

    public String getFreeName() {
        Optional<String> fromFinder = tryToFindFreeName();
        if (fromFinder.isEmpty()) {
            AlertWindow.showInfo(
                    "Будет сгенерирован необычный номер заказа",
                    "Программа не смогла найти свободный номер, если проблема не устранится в следующий раз, свяжитесь с разработчиками",
                    false
            );
            logger.warn("NameController couldn't find a free name, had to generate a random one");
            return generateFreeRandomName();
        }
        return fromFinder.get();
    }

}
