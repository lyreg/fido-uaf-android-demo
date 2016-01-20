package org.lyreg.fido_uaf_android_demo.uaf;

import android.content.Intent;

import org.json.JSONException;
import org.lyreg.fido_uaf_android_demo.exception.UafProcessingException;

/**
 * UAF client intent management methods.
 * Created by Administrator on 2016/1/14.
 */
public interface IUafClientUtils {

    /**
     * Gets an intent which will perform a discovery operation using the UAF client app.
     *
     */
    Intent getDiscoverIntent();

    /**
     * Gets an intent which will perform a UAF Registration, Authentication or Deregistration operation using the UAF client app.
     * @param fidoOpType FIDO UAF operation type (Registration, Authentication, Deregistration)
     * @param uafRequest UAF request message
     * @return Intent
     */
    Intent getUafOperationIntent(FidoOperation fidoOpType, String uafRequest);


    /**
     * Return response data from a UAF message.
     * @param fidoOpType FIDO UAF operation type
     * @param resultIntent UAF client result intent
     * @return client output in JSON format - the UAF response message in the case of Registration and Authentication and the
     * DiscoveryData for a Discovery operation. Deregistration and Check Policy operations return null.
     * @throws UafProcessingException with error details if the intent contains an error
     */
    String getUafClientResponse(FidoOperation fidoOpType, Intent resultIntent) throws UafProcessingException, JSONException;
}
