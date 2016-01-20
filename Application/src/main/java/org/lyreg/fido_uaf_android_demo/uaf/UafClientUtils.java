package org.lyreg.fido_uaf_android_demo.uaf;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lyreg.fido_uaf_android_demo.exception.UafProcessingException;

/**
 * Implementation of {@link IUafClientUtils} which conforms to v1.0 FIDO UAF protocols.
 *
 * Created by Administrator on 2016/1/14.
 */
public class UafClientUtils implements IUafClientUtils {
    private final String TAG = "UafClientUtils";

    private final static String channelBindings =
            "{\"serverEndPoint\":null,\"tlsServerCertificate\":null,\"tlsUnique\":null,\"cid_pubkey\":null}";

    /**
     * Gets an intent which will perform a discovery operation using the UAF client app.
     */
    @Override
    public Intent getDiscoverIntent() {
        UafOperation discoverOperation = this.getUafDiscoverOperation();
        return discoverOperation.toIntent();
    }

    /**
     * Gets an intent which will perform a UAF Registration, Authentication or Deregistration operation using the UAF client app.
     * @param fidoOpType FIDO UAF operation type (Registration, Authentication, Deregistration)
     * @param uafRequest UAF request message
     * @return Intent
     */
    public Intent getUafOperationIntent(FidoOperation fidoOpType, String uafRequest) {
        switch(fidoOpType) {
            case Registration:
            case Authentication:
            case Deregistration:
                UafOperation uafOperation = this.getFidoUafOperation(uafRequest);
                Intent uafOperationIntent = uafOperation.toIntent();
                UafClientLogUtils.logUafOperationRequest(uafOperationIntent, fidoOpType);
                Log.e("getUafOperationIntent", "getUafOperationIntent");
                return uafOperationIntent;
            case Discover:
            case CheckPolicy:
            default:
                throw new UafProcessingException("Invalid FIDO operation type specified: " + fidoOpType.toString());
        }
    }

    /**
     * Creates a FIDO UAF discover operation
     * @return FIDO UAF discover operation
     */
    private UafOperation getUafDiscoverOperation() {
        return UafOperationFactory.createDiscover();
    }

    /**
     * Creates a FIDO UAF operation from a UAF message and a hard-coded {@link #channelBindings} string.
     *
     * @param uafRequest UAF request message
     * @return FIDO UAF operation
     */
    private UafOperation getFidoUafOperation(String uafRequest) {

//        UAFMessage uafMessage = new UAFMessage(uafRequest, null);
        String msg = handleUafOperationRequest(uafRequest);
        UAFMessage uafMessage = new UAFMessage(msg, null);
        String channelBindings = new Gson().toJson(uafMessage);
        return UafOperationFactory.createUAFOperation(uafMessage, null, channelBindings);
    }

    private String handleUafOperationRequest(String uafRequest) {
        String facetID = "android:apk-key-hash:QM1hAKXXm9uxhxw1m9vRTxpXNEo";
        Log.e("handleUafOperationRequest", uafRequest);
        JSONArray reg = null;
        try {
            reg = new JSONArray(uafRequest);
            ((JSONObject)reg.get(0)).getJSONObject("header").put("appID", facetID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  reg.toString();
    }

    /**
     *  Check response intent from UAF client for errors.
     *  If it's OK send the response message created by the client to the server.
     *  @return message from UAF client
     */
    @Override
    public String getUafClientResponse(FidoOperation fidoOpType, Intent resultIntent) throws UafProcessingException, JSONException {
        Log.d(TAG, "processClientResultIntent called.");

        short errorCode = resultIntent.getShortExtra("errorCode", (short) -1);

        if(errorCode != ErrorCode.NO_ERROR.getValue()) {
            throw new UafProcessingException("FIDO Client Processing Error: " + ErrorCode.getByValue(errorCode).getDescription());
        } else {
            String intentType = resultIntent.getStringExtra("UAFIntentType");
            if(intentType != null) {
                if(intentType.equals(UAFIntentType.DISCOVER_RESULT.getDescription())) {
                    return resultIntent.getStringExtra("discoveryData");
                } else if(intentType.equals(UAFIntentType.CHECK_POLICY_RESULT.getDescription())) {
                    return null;
                } else if(intentType.equals(UAFIntentType.UAF_OPERATION_RESULT.getDescription())) {
                    String fidoUafMessage = resultIntent.getStringExtra("message");
                    if(fidoUafMessage != null) {
                        JSONObject uafMsgJsonObj = new JSONObject(fidoUafMessage);
                        String message = uafMsgJsonObj.getString("uafProtocolMessage");
                        return message;
                    } else {
                        return null;
                    }
                } else {
                    throw new UafProcessingException("Unrecognised UAF client response intent type: " + intentType);
                }
            } else {
                throw new UafProcessingException("UAF client response intent is missing the UAFIntentType extra.");
            }
        }
    }
}
