package homer.tastyworld.frontend.starterpack.api.sra;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneOffset;

public class Common {

    private record PostErrorRequestBody(
            String app_name,
            String app_version,
            String error,
            @Nullable String stack_trace
    ) {}

    private record GetServiceTimeZoneOffsetResponseBody(
            ZoneOffset timezone_offset
    ) {}

    private static final AppLogger logger = AppLogger.getFor(Common.class);

    private static String getErrorStackTrace(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static void postError(String message, @Nullable Throwable throwable) {
        PostErrorRequestBody postErrorRequestBody = new PostErrorRequestBody(
                AppConfig.getAppIdentifierName(),
                AppConfig.getAppVersion(),
                message,
                throwable != null ? getErrorStackTrace(throwable) : null
        );
        Request postErrorRequest = Request.of(
                RequestUtils.getURI("/frontend/error"),
                Method.POST,
                RequestUtils.getBearerAuthorization(),
                postErrorRequestBody
        );
        int responseStatusCode = Requester.exchange(postErrorRequest);

        if (responseStatusCode != 202) {
            logger.errorOnlyWrite("Can't notify server about error", null);
            AlertWindows.showError(
                    "Срочно обратитесь к разработчикам",
                    "Приложению не удаётся самостоятельно оповестить о случившейся ошибке",
                    true
            );
        }
    }

    public static ZoneOffset getServiceTimezoneOffset() {
        Request getServiceTimezoneOffsetRequest = Request.of(
                RequestUtils.getURI("/service/timezone_offset"), Method.GET
        );
        GetServiceTimeZoneOffsetResponseBody getServiceTimeZoneOffsetResponseBody = Requester.exchange(
                getServiceTimezoneOffsetRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetServiceTimeZoneOffsetResponseBody.class)
        );
        return getServiceTimeZoneOffsetResponseBody.timezone_offset();
    }

}
