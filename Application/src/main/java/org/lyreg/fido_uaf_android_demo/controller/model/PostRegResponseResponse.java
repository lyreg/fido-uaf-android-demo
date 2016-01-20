package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/20.
 */
public class PostRegResponseResponse {

    private String fidoRegistrationResponse;

    public PostRegResponseResponse() {}

    public void setFidoReqistrationResponse(String fidoRegistrationResponse) {
        this.fidoRegistrationResponse = fidoRegistrationResponse;
    }

    public String getFidoRegistrationResponse() { return fidoRegistrationResponse; }
}
