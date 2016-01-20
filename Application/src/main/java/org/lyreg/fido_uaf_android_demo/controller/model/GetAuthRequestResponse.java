package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/19.
 */
public class GetAuthRequestResponse {

    private String fidoAuthenticationRequest;

    public GetAuthRequestResponse() {}

    public void setFidoAuthenticationRequest(String fidoAuthenticationRequest) {
        this.fidoAuthenticationRequest = fidoAuthenticationRequest;
    }

    public String getFidoAuthenticationRequest() {
        return fidoAuthenticationRequest;
    }
}
