package homer.tastyworld.frontend.starterpack.api.evotor.mobcashier;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier.CantCreateOrderWithoutPositionsException;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderCreator {

    private record ImportationDataObjectInPositionElementObjectInCreateOrderRequestBody(
            String country_origin_code,
            String customs_declaration_number,
            @Nullable Number excise
    ) {}

    private record AgentInfoObjectInPositionElementObjectInCreateOrderRequestBody(
            String type,  // Must be enum...
            String principal_inn,
            String principal_name,
            String principal_phone,
            @Nullable String agent_phone,
            @Nullable String subagent_phone,
            @Nullable String operation_description,
            @Nullable String transaction_operator_name,
            @Nullable String transaction_operator_inn,
            @Nullable String transaction_operator_phones,
            @Nullable String transaction_operator_address,
            @Nullable String receive_operator_phones
    ) {}

    private record SectorialItemPropsObjectInPositionElementObjectInCreateOrderRequestBody(
            String federal_id,
            String date,
            String number,
            String value
    ) {}

    private record PositionElementObjectInCreateOrderRequestBody(
            @Nullable String position_uuid,
            @Nullable String settlement_method_type,  // Must be enum...
            @Nullable String code,
            @Nullable BigDecimal price,
            @Nullable String prepayment_price,
            @Nullable ImportationDataObjectInPositionElementObjectInCreateOrderRequestBody importation_data,
            @Nullable BigDecimal priceWithDiscount,
            @Nullable Boolean is_excisable,
            @Nullable Boolean is_age_limited,
            String name,
            String measureName,  // Must be enum...
            BigDecimal quantity,
            String tax,  // Must be enum...
            @Nullable String commodity_id,
            @Nullable String mark,
            String type,  // Must be enum...
            @Nullable AgentInfoObjectInPositionElementObjectInCreateOrderRequestBody agentInfo,
            @Nullable SectorialItemPropsObjectInPositionElementObjectInCreateOrderRequestBody sectoral_item_props

    ) {}

    private record ExtraObjectInCreateOrderRequestBody(
            Long tw_order_id,
            String tw_order_name,
            @Nullable String delivery_info
    ) {}

    private record PurchaserObjectInCreateOrderRequestBody(
            String name,
            @Nullable String birth_date,
            @Nullable String document_type,  // Must be enum...
            @Nullable String document_number,
            @Nullable String inn_number,
            String type  // Must be enum...
    ) {}

    private record CallbackObjectInCreateOrderRequestBody(
            @Nullable String url,
            @Nullable String auth
    ) {}

    private record CreateOrderRequestBody(
            @Nullable String receipt_uuid,
            List<PositionElementObjectInCreateOrderRequestBody> positions,
            @Nullable String mdlp,
            @Nullable String client_phone,
            String client_email,
            @Nullable String cashier_uuid,
            Boolean should_print_receipt,
            @Nullable Boolean editable,
            ExtraObjectInCreateOrderRequestBody extra,
            @Nullable PurchaserObjectInCreateOrderRequestBody purchaser,
            @Nullable BigDecimal receiptDiscount,
            @Nullable String note,
            @Nullable CallbackObjectInCreateOrderRequestBody callback
    ) {}

    private static final URI createOrderURI = URI.create(AppConfig.EVOTOR_MC_API_URL + "/orders/create");
    private final String androidID;
    private final String authorizationToken;
    private final List<PositionElementObjectInCreateOrderRequestBody> positions = new ArrayList<>();

    public OrderCreator(String androidID, String authorizationToken) {
        this.androidID = androidID;
        this.authorizationToken = authorizationToken;
    }

    public void addPosition(String name, BigDecimal price, String measureName, BigDecimal quantity, String tax, String type, @Nullable String mark) {
        positions.add(
               new PositionElementObjectInCreateOrderRequestBody(
                       null,
                       "FULL",
                       null,
                       price,
                       null,
                       null,
                       null,
                       false,
                       false,
                       name,
                       measureName,
                       quantity,
                       tax,
                       null,
                       mark,
                       type,
                       null,
                       null
               )
        );
    }

    public void clearPositions() {
        positions.clear();
    }

    public void post(long twOrderID, String twOrderName, @Nullable BigDecimal discount, @Nullable String deliveryInfo) {
        if (positions.isEmpty()) {
            throw new CantCreateOrderWithoutPositionsException();
        }
        CreateOrderRequestBody createOrderRequestBody = new CreateOrderRequestBody(
                null,
                positions,
                null,
                null,
                AppConfig.getEvotorMobcashierFiscalizationEmail(),
                null,
                true,
                false,
                new ExtraObjectInCreateOrderRequestBody(twOrderID, twOrderName, deliveryInfo),
                null,
                discount,
                "Created by TastyWorld",
                null
        );
        Request createOrderRequest = Request.of(
                createOrderURI,
                Method.POST,
                Map.of("AndroidId", androidID, "Authorization", "Bearer " + authorizationToken),
                createOrderRequestBody
        );
        Requester.exchange(
                createOrderRequest, response -> {
                    if (response.getCode() != 200) {
                        throw new UnexpectedResponseStatusCodeException(response, 200);
                    }
                    return null;
                }
        );
    }

}
