package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.displayed.InternalServerException;
import homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected.CantParseResponseException;
import homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected.UnknownResponseCodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.io.IOException;

class ResponseProcessor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final HttpClientResponseHandler<Response> RESPONSE_HANDLER = ResponseProcessor::processResponse;

    private static Response processResponse(ClassicHttpResponse response) {
        int statusCode = response.getCode();  // TODO: process all statuses
        if (statusCode == 200) {
            return jsonStrToResponse(response.getEntity());
        } else if (statusCode == 500) {
            throw new InternalServerException();
        }
        throw new UnknownResponseCodeException(statusCode);
    }

    private static Response jsonStrToResponse(HttpEntity response) {
        try {
            return OBJECT_MAPPER.readValue(EntityUtils.toString(response), Response.class);
        } catch (IOException | ParseException ex) {
            throw new CantParseResponseException(ex);
        }
    }

}
