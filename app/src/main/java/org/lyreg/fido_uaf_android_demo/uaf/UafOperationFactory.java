package org.lyreg.fido_uaf_android_demo.uaf;

/**
 * Created by Administrator on 2016/1/14.
 */
public class UafOperationFactory {

    public static UafOperation createDiscover() {
        return new UafOperation(UAFIntentType.DISCOVER, null, null, null, null ,null);
    }
}
