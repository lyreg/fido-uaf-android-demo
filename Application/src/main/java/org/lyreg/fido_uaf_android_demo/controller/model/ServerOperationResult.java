package org.lyreg.fido_uaf_android_demo.controller.model;

/**
 * Created by Administrator on 2016/1/20.
 */
public class ServerOperationResult<T> {
    private Error error;
    private T response;

    public ServerOperationResult(Error error) { this.error = error; }

    public ServerOperationResult(T response) { this.response = response; }

    public Error getError() { return this.error; }
    public void setError(Error error) { this.error = error; }

    public T getResponse() { return this.response; }
    public void setResponse(T response) { this.response = response; }

    public boolean isSuccessful() { return this.getError() == null; }
}
