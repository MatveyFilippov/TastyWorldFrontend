package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.AccessForbiddenOnRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.NotFoundRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.UnauthorizedRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.displayed.InternalServerException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.CantParseResponseBodyException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.CantProcessResponseStreamException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.UnknownResponseCodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class ResponseProcessor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final HttpClientResponseHandler<InputStream> STREAM_RESPONSE_HANDLER = ResponseProcessor::processStreamResponse;
    public static final HttpClientResponseHandler<Response> JSON_RESPONSE_HANDLER = ResponseProcessor::processJSONResponse;

    private static void throwIfNotOkStatus(ClassicHttpResponse response) {
        int statusCode = response.getCode();
        if (statusCode == 200) {
            return;
        } else if (statusCode == 400) {
            throw new BadRequestException(jsonStrToResponse(response.getEntity()));
        } else if (statusCode == 401) {
            throw new UnauthorizedRequestException(jsonStrToResponse(response.getEntity()));
        } else if (statusCode == 403) {
            throw new AccessForbiddenOnRequestException(jsonStrToResponse(response.getEntity()));
        } else if (statusCode == 404) {
            throw new NotFoundRequestException(jsonStrToResponse(response.getEntity()));
        } else if (statusCode == 500) {
            throw new InternalServerException();
        }
        throw new UnknownResponseCodeException(statusCode);
    }

    private static InputStream processStreamResponse(ClassicHttpResponse response) {
        throwIfNotOkStatus(response);
        try {
//            InputStream inputStream = response.getEntity().getContent();
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            byte[] data = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, bytesRead);
//            }
//            buffer.flush();
//            byte[] imageBytes = buffer.toByteArray();
//            return new ByteArrayInputStream(imageBytes);
            return new ByteArrayInputStream(response.getEntity().getContent().readAllBytes());
        } catch (IOException ex) {
            throw new CantProcessResponseStreamException(ex);
        }
    }

    private static Response processJSONResponse(ClassicHttpResponse response) {
        throwIfNotOkStatus(response);
        return jsonStrToResponse(response.getEntity());
    }

    private static Response jsonStrToResponse(HttpEntity response) {
        try {
            return OBJECT_MAPPER.readValue(EntityUtils.toString(response), Response.class);
        } catch (IOException | ParseException ex) {
            throw new CantParseResponseBodyException(ex);
        }
    }

}
