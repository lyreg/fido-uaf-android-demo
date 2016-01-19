package org.lyreg.fido_uaf_android_demo.http;

/**
 * Created by Administrator on 2016/1/19.
 */
public class HttpResponse {
    private final int httpStatusCode;
    private final String payload;

    public HttpResponse(int httpStatusCode, String payload) {
        this.httpStatusCode = httpStatusCode;
        this.payload = payload;
    }

    public int getHttpStatusCode() { return httpStatusCode; }

    public String getPayload() { return payload; }
}
