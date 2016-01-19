package org.lyreg.fido_uaf_android_demo.uaf;

import android.content.Intent;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/1/14.
 */
public class UafOperation {

    private final UAFIntentType uafIntentType;
    private final UAFMessage    message;
    private final String        origin;
    private final String        channelBindings;
    private final Short         responseCode;
    private final String        responseCodeMessage;

    public UafOperation(final UAFIntentType uafIntentType, final UAFMessage message, final String origin,
                         final String channelBindings, final Short responseCode, final String responseCodeMessage) {
        this.uafIntentType = uafIntentType;
        this.message = message;
        this.origin = origin;
        this.channelBindings = channelBindings;
        this.responseCode = responseCode;
        this.responseCodeMessage = responseCodeMessage;
    }

    public Intent toIntent() {
        final Intent intent = new Intent();
        intent.setAction(AndroidClientIntentParameters.intentAction);
        intent.setType(AndroidClientIntentParameters.intentType);
        Gson gson = new Gson();

        if(uafIntentType != null) {
            intent.putExtra("UAFIntentType", uafIntentType.getDescription());
        }
        if(message != null) {
            intent.putExtra("message", gson.toJson(message));
        }
        if(origin != null) {
            intent.putExtra("origin", origin);
        }
        if(channelBindings != null) {
            intent.putExtra("channelBindings", channelBindings);
        }
        if(responseCode != null) {
            intent.putExtra("responseCode", (short) responseCode);
        }
        if(responseCodeMessage != null) {
            intent.putExtra("responseCodeMessage", responseCodeMessage);
        }

        return intent;
    }

}
