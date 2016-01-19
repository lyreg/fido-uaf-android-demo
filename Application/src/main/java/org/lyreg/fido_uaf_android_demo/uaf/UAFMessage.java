package org.lyreg.fido_uaf_android_demo.uaf;

/**
 * Created by Administrator on 2016/1/14.
 */
public class UAFMessage {

    final private String uafProtocolMessage;
    final private Object additionalData;


    /**
     *
     * @param uafProtocolMessage
     *            The JSON-serialised UAF message, e.g. RegistrationRequest, AuthenticationRequest,
     *            etc...
     * @param additionalData
     *            Allows the FIDO Server or client application to attach additional data for use by
     *            the FIDO UAF Client as a JSON object, or the FIDO UAF Client or client application
     *            to attach additional data for use by the client application.
     */
    public UAFMessage(final String uafProtocolMessage, final Object additionalData) {
        this.uafProtocolMessage = uafProtocolMessage;
        this.additionalData = additionalData;
    }

    public String getUafProtocolMessage() {
        return uafProtocolMessage;
    }

    public Object getAdditionalData() {
        return additionalData;
    }
}
