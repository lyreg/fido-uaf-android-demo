package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/21.
 */
public class PostAuthResponseResponse {
    private String fidoAuthenticationResponse;

    public PostAuthResponseResponse() {}

    public void setFidoAuthenticationResponse(String fidoAuthResponse) {
        this.fidoAuthenticationResponse = fidoAuthResponse;
    }

    public String getFidoAuthenticationResponse() { return fidoAuthenticationResponse; }
}
