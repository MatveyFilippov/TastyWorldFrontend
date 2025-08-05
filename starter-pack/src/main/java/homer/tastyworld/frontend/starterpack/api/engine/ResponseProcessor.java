package homer.tastyworld.frontend.starterpack.api.engine;

import homer.tastyworld.frontend.starterpack.api.PhotoResponse;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.AccessForbiddenOnRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.NotFoundRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.UnauthorizedRequestException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest.InternalServerException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest.CantProcessResponseException;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest.UnknownResponseCodeException;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class ResponseProcessor {

    public static final HttpClientResponseHandler<InputStream> STREAM_RESPONSE_HANDLER = ResponseProcessor::processStreamResponse;
    public static final HttpClientResponseHandler<PhotoResponse> IMAGE_RESPONSE_HANDLER = ResponseProcessor::processImageResponse;
    public static final HttpClientResponseHandler<Response> JSON_RESPONSE_HANDLER = ResponseProcessor::processJSONResponse;

    private static Response jsonStrToResponse(HttpEntity response) {
        try {
            return TypeChanger.OBJECT_MAPPER.readValue(EntityUtils.toString(response), Response.class);
        } catch (IOException | ParseException ex) {
            throw new CantProcessResponseException(ex);
        }
    }

    private static void throwIfNotOkStatus(ClassicHttpResponse response) {
        int statusCode = response.getCode();
        if (statusCode == 200) {
            return;
        }
        switch (statusCode) {
            case 400 -> throw new BadRequestException(jsonStrToResponse(response.getEntity()));
            case 401 -> throw new UnauthorizedRequestException(jsonStrToResponse(response.getEntity()));
            case 403 -> throw new AccessForbiddenOnRequestException(jsonStrToResponse(response.getEntity()));
            case 404 -> throw new NotFoundRequestException(jsonStrToResponse(response.getEntity()));
            case 500 -> throw new InternalServerException();
            default -> throw new UnknownResponseCodeException(statusCode);
        }
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
            throw new CantProcessResponseException(ex);
        }
    }

    private static PhotoResponse processImageResponse(ClassicHttpResponse response) {
        throwIfNotOkStatus(response);
        try {
            String abstractPath = response.getHeader("X-Image-AbstractPath").getValue();
            String hashSHA256 = response.getHeader("X-Image-SHA256-Hash").getValue();
//            InputStream inputStream = response.getEntity().getContent();
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            byte[] data = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, bytesRead);
//            }
//            buffer.flush();
//            byte[] imageBytes = buffer.toByteArray();
            byte[] imageBytes = response.getEntity().getContent().readAllBytes();
            return new PhotoResponse(abstractPath, hashSHA256, imageBytes);
        } catch (IOException | ProtocolException ex) {
            throw new CantProcessResponseException(ex);
        }
    }

    private static Response processJSONResponse(ClassicHttpResponse response) {
        throwIfNotOkStatus(response);
        return jsonStrToResponse(response.getEntity());
    }

}
