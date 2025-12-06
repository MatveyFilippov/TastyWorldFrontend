package homer.tastyworld.frontend.starterpack.api.sra;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.CantProcessResponseException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Method;
import java.io.ByteArrayInputStream;

public class FileResponse {

    private record GetFileInfoResponseBody(
            String file_sha256_hash,
            String file_abstract_path
    ) {}

    public record Info(
            String hashSHA256,
            String abstractPath
    ) {}

    public final Info info;
    private final byte[] content;

    private FileResponse(byte[] content, String hashSHA256, String abstractPath) {
        this.content = content;
        this.info = new Info(hashSHA256, abstractPath);
    }

    public ByteArrayInputStream getContent() {
        return new ByteArrayInputStream(content);
    }

    private static FileResponse fileResponseHandler(ClassicHttpResponse response) {
        RequestUtils.throwIfUnexpectedStatusCode(response, 200);
        try {
            byte[] content = response.getEntity().getContent().readAllBytes();
            String abstractPath = response.getHeader("File-AbstractPath").getValue();
            String hashSHA256 = response.getHeader("File-SHA256-Hash").getValue();
            return new FileResponse(content, hashSHA256, abstractPath);
        } catch (Exception ex) {
            throw new CantProcessResponseException(ex);
        }
    }

    public static FileResponse request(String endpoint) {
        return Requester.exchange(
                Request.of(RequestUtils.getURI(endpoint), Method.GET, RequestUtils.getBearerAuthorization()),
                FileResponse::fileResponseHandler
        );
    }

    public static Info requestInfo(String endpoint) {
        GetFileInfoResponseBody getFileInfoResponseBody = Requester.exchange(
                Request.of(RequestUtils.getURI(endpoint), Method.GET, RequestUtils.getBearerAuthorization()),
                response -> RequestUtils.processEntityResponse(response, 200, GetFileInfoResponseBody.class)
        );
        return new Info(getFileInfoResponseBody.file_sha256_hash(), getFileInfoResponseBody.file_abstract_path());
    }

}
