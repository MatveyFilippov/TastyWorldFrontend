package homer.tastyworld.frontend.starterpack.api.sra.entity.order.names;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.utils.misc.Randomizer;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.Method;
import java.util.Optional;

public abstract class NameController {

    private record CheckNameAvailableResponseBody(
            Long client_point_id,
            String name,
            Boolean is_available
    ) {}


    protected final AppLogger logger = AppLogger.getFor(getClass());

    public static boolean isAvailable(String name) {
        Request checkNameAvailableRequest = Request.of(
                RequestUtils.getURI("/orders/check_name_available?name=" + name), Method.GET, RequestUtils.getBearerAuthorization()
        );
        CheckNameAvailableResponseBody checkNameAvailableResponseBody = Requester.exchange(
                checkNameAvailableRequest,
                response -> RequestUtils.processEntityResponse(response, 200, CheckNameAvailableResponseBody.class)
        );
        return checkNameAvailableResponseBody.is_available();
    }

    private static String generateFreeRandomName() {
        int nameLength = 3;
        while (true) {
            for (int i = 0; i < Math.pow(nameLength, Randomizer.CHARACTERS_QUANTITY); i++) {
                String randomName = Randomizer.getString(nameLength);
                if (isAvailable(randomName)) {
                    return randomName;
                }
            }
            nameLength++;
        }
    }

    protected abstract Optional<String> tryToFindNextName();

    public abstract void backIfCurrent(String name);

    public final String getFreeName() {
        Optional<String> fromFinder = tryToFindNextName();
        if (fromFinder.isEmpty()) {
            AlertWindows.showInfo(
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
