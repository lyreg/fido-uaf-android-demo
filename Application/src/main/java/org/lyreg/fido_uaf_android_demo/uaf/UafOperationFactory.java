package org.lyreg.fido_uaf_android_demo.uaf;

/**
 * Created by Administrator on 2016/1/14.
 */
public class UafOperationFactory {

    public static UafOperation createDiscover() {
        return new UafOperation(UAFIntentType.DISCOVER, null, null, null, null ,null);
    }

    public static UafOperation createUAFOperation(final UAFMessage message, final String origin, final String channelBindings) {
        return new UafOperation(UAFIntentType.UAF_OPERATION, message, origin, channelBindings, null, null);
    }
}
