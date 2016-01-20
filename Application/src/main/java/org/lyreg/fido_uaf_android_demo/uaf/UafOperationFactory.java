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
    public static UafOperation createUAFOperationCompletionStatus(final UAFMessage message, final Short responseCode,
                                                                  final String responseCodeMessage) {
        return new UafOperation(UAFIntentType.UAF_OPERATION_COMPLETION_STATUS, message, null, null, responseCode,
                responseCodeMessage);
    }
}
