package org.lyreg.fido_uaf_android_demo.http;

import org.lyreg.fido_uaf_android_demo.controller.model.GetAuthRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.GetRegRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.PostRegResponseResponse;

/**
 * Created by Administrator on 2016/1/19.
 */
public interface IRelyingPartyComms {
    /***
     * Get a FIDO registration request from server, allowing a user to assocaited a FIDO authenticator with
     * the account.
     *
     * @return GetRegRequestResponse
     */
    GetRegRequestResponse GetRegRequest(String account);

    /***
     * Post a FIDO registration response from FIDO client to server,
     * this operation processes the response from the FIDO authenticator
     *
     * @param payload - the response from the authenticator
     * @return PostRegResponseResponse
     */
    PostRegResponseResponse PostRegResponse(String payload);

    /***
     * In order to authenticate with FIDO, an authentication request must be created.
     * This call does that.
     *
     * @return CreateAuthRequestResponse
     */
    GetAuthRequestResponse GetAuthRequest();
}
