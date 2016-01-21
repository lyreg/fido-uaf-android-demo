package org.lyreg.fido_uaf_android_demo.http;

import android.content.Context;

import org.lyreg.fido_uaf_android_demo.controller.model.*;
import org.lyreg.fido_uaf_android_demo.controller.model.Error;
import org.lyreg.fido_uaf_android_demo.exception.ServerError;
import org.lyreg.fido_uaf_android_demo.utils.Preferences;

import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2016/1/19.
 */
public class RelyingPartyServerComms implements IRelyingPartyComms {

    private Context context;
    private HttpComms httpComms;

    public RelyingPartyServerComms(Context context) {
        this.context = context;
        httpComms = new HttpComms(context);
    }

    /***
     * Get a FIDO registration request from server, allowing a user to assocaited a FIDO authenticator with
     * the account.
     *
     * @return CreateRegRequestResponse
     */
    @Override
    public GetRegRequestResponse GetRegRequest(String account) {

        HttpResponse httpResponse = httpComms.get(Preferences.GET_REG_REQUEST + "/" + account);
        if(httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_CREATED
                || httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_OK) {
            GetRegRequestResponse getRegResponse = new GetRegRequestResponse();
            getRegResponse.setFidoRegistrationRequest(httpResponse.getPayload());
            return getRegResponse;
        } else {
            Error error = new Error(httpResponse.getHttpStatusCode(), httpResponse.getPayload());
            throw new ServerError(error);
        }
    }

    /***
     * Post a FIDO registration response from FIDO client to server,
     * this operation processes the response from the FIDO authenticator
     *
     * @param payload - the response from the authenticator
     * @return PostRegResponseResponse
     */
    @Override
    public PostRegResponseResponse PostRegResponse(String payload) {

        HttpResponse httpResponse = httpComms.post(Preferences.POST_REG_RESPONSE, payload);
        if(httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_CREATED
                || httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_OK) {
            PostRegResponseResponse postRegResponse = new PostRegResponseResponse();
            postRegResponse.setFidoReqistrationResponse(httpResponse.getPayload());
            return postRegResponse;
        } else {
            Error error = new Error(httpResponse.getHttpStatusCode(), httpResponse.getPayload());
            throw new ServerError(error);
        }
    }

    /***
     * In order to authenticate with FIDO, an authentication request must be created.
     * This call does that.
     *
     * @return CreateAuthRequestResponse
     */
    @Override
    public GetAuthRequestResponse GetAuthRequest() {
        HttpResponse httpResponse = httpComms.get(Preferences.GET_AUTH_REQUEST);
        if(httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_CREATED
                || httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_OK ) {
            GetAuthRequestResponse getAuthResponse = new GetAuthRequestResponse();
            getAuthResponse.setFidoAuthenticationRequest(httpResponse.getPayload());
            return getAuthResponse;
        } else {
            Error error = new Error(httpResponse.getHttpStatusCode(), httpResponse.getPayload());
            throw new ServerError(error);
        }
    }


    /***
     * Post a FIDO Authentication response from FIDO client to server,
     * this operation processes the response from the FIDO authenticator
     *
     * @param payload - the response from the authenticator
     * @return PostAuthResponseResponse
     */
    public PostAuthResponseResponse PostAuthResponse(String payload) {
        HttpResponse httpResponse = httpComms.post(Preferences.POST_AUTH_RESPONSE, payload);
        if(httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_CREATED
                || httpResponse.getHttpStatusCode() == HttpURLConnection.HTTP_OK ) {
            PostAuthResponseResponse postAuthResponse = new PostAuthResponseResponse();
            postAuthResponse.setFidoAuthenticationResponse(httpResponse.getPayload());
            return postAuthResponse;
        } else {
            Error error = new Error(httpResponse.getHttpStatusCode(), httpResponse.getPayload());
            throw new ServerError(error);
        }
    }

}
