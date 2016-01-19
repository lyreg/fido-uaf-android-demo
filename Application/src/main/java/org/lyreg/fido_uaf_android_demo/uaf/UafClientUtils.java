package org.lyreg.fido_uaf_android_demo.uaf;

import android.content.Intent;
import android.util.Log;

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
     * Creates a FIDO UAF discover operation
     * @return FIDO UAF discover operation
     */
    private UafOperation getUafDiscoverOperation() {
        return UafOperationFactory.createDiscover();
    }

    /**
     *  Check response intent from UAF client for errors.
     *  If it's OK send the response message created by the client to the server.
     *  @return message from UAF client
     */
    @Override
    public String getUafClientResponse(FidoOperation fidoOpType, Intent resultIntent) throws UafProcessingException {
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
                    return null;
                } else {
                    throw new UafProcessingException("Unrecognised UAF client response intent type: " + intentType);
                }
            } else {
                throw new UafProcessingException("UAF client response intent is missing the UAFIntentType extra.");
            }
        }
    }
}
