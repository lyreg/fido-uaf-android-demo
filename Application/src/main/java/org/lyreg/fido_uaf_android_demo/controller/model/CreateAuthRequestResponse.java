package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/19.
 */
public class CreateAuthRequestResponse {

    private String fidoAuthenticationRequest;

    public CreateAuthRequestResponse() {}

    public void setFidoAuthenticationRequest(String fidoAuthenticationRequest) {
        this.fidoAuthenticationRequest = fidoAuthenticationRequest;
    }

    public String getFidoAuthenticationRequest() {
        return fidoAuthenticationRequest;
    }
}
