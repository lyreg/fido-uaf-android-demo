package org.lyreg.fido_uaf_android_demo.http;

import org.lyreg.fido_uaf_android_demo.controller.model.CreateAuthRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.CreateRegRequestResponse;

/**
 * Created by Administrator on 2016/1/19.
 */
public interface IRelyingPartyComms {
    /***
     * PROTECTED OPERATION - the server will only process this if a valid session is in place
     *
     * Create a FIDO registration request, allowing a user to assocaited a FIDO authenticator with
     * the account.
     *
     * @return CreateRegRequestResponse
     */
    CreateRegRequestResponse CreateRegRequest();

    /***
     * In order to authenticate with FIDO, an authentication request must be created.
     * This call does that.
     *
     * @return CreateAuthRequestResponse
     */
    CreateAuthRequestResponse CreateAuthRequest();
}
