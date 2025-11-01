package homer.tastyworld.frontend.starterpack.api.sra.entity.current;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;

public class ClientPoint {

    private record GetClientPointResponseBody(
            Long client_point_id,
            String name,
            String address,
            String owner_full_name,
            String owner_email,
            @Nullable String owner_phone_number,
            LocalDate paid_till
    ) {}

    private record GetClientPointSubscriptionDaysResponseBody(
            Long client_point_id,
            Integer days
    ) {}

    public static final long id;
    public static final String name;
    public static final String address;
    public static final String ownerFullName;
    public static final String ownerEmail;
    public static final String ownerPhoneNumber;

    static {
        Request getClientPointRequest = Request.of(
                RequestUtils.getURI("/client_points/me"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetClientPointResponseBody getClientPointResponseBody = Requester.exchange(
                getClientPointRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetClientPointResponseBody.class)
        );

        id = getClientPointResponseBody.client_point_id();
        name = getClientPointResponseBody.name();
        address = getClientPointResponseBody.address();
        ownerFullName = getClientPointResponseBody.owner_full_name();
        ownerEmail = getClientPointResponseBody.owner_email();
        ownerPhoneNumber = getClientPointResponseBody.owner_phone_number();
    }

    public static int getSubscriptionDays() {
        Request getClientPointRequest = Request.of(
                RequestUtils.getURI("/client_points/me/subscription_days"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetClientPointSubscriptionDaysResponseBody getClientPointResponse = Requester.exchange(
                getClientPointRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetClientPointSubscriptionDaysResponseBody.class)
        );

        return getClientPointResponse.days();
    }

}
