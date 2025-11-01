package homer.tastyworld.frontend.starterpack.api.sra;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantProcessResponseException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Method;
import java.io.ByteArrayInputStream;

public class ImageResponse {

    private record ImageInfoResponseBody(
            String image_sha256_hash,
            String image_abstract_path
    ) {}

    public record Info(
            String hashSHA256,
            String abstractPath
    ) {}

    public final Info info;
    private final byte[] content;

    private ImageResponse(byte[] content, String hashSHA256, String abstractPath) {
        this.content = content;
        this.info = new Info(hashSHA256, abstractPath);
    }

    public ByteArrayInputStream getContent() {
        return new ByteArrayInputStream(content);
    }

    private static ImageResponse imageResponseHandler(ClassicHttpResponse response) {
        RequestUtils.throwIfUnexpectedStatusCode(response, 200);
        try {
            byte[] imageBytes = response.getEntity().getContent().readAllBytes();
            String abstractPath = response.getHeader("Image-AbstractPath").getValue();
            String hashSHA256 = response.getHeader("Image-SHA256-Hash").getValue();
            return new ImageResponse(imageBytes, hashSHA256, abstractPath);
        } catch (Exception ex) {
            throw new CantProcessResponseException(response, ex);
        }
    }

    public static ImageResponse request(String endpoint) {
        return Requester.exchange(
                Request.of(RequestUtils.getURI(endpoint), Method.GET, RequestUtils.getBearerAuthorization()),
                ImageResponse::imageResponseHandler
        );
    }

    public static Info requestInfo(String endpoint) {
        ImageInfoResponseBody imageInfoResponseBody = Requester.exchange(
                Request.of(RequestUtils.getURI(endpoint), Method.GET, RequestUtils.getBearerAuthorization()),
                response -> RequestUtils.processEntityResponse(response, 200, ImageInfoResponseBody.class)
        );
        return new Info(imageInfoResponseBody.image_sha256_hash(), imageInfoResponseBody.image_abstract_path());
    }

}
