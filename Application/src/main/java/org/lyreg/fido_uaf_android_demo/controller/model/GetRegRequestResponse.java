package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/19.
 */
public class GetRegRequestResponse {

    private String fidoRegistrationRequest;

    public GetRegRequestResponse() {
    }

    public void setFidoRegistrationRequest(String fidoRegistrationRequest) {
        this.fidoRegistrationRequest = fidoRegistrationRequest;
    }

    public String getFidoRegistrationRequest() {
        return fidoRegistrationRequest;
    }
}
