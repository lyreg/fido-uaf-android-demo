package org.lyreg.fido_uaf_android_demo.http;

import android.content.Context;
import android.util.Log;

import org.lyreg.fido_uaf_android_demo.controller.model.*;
import org.lyreg.fido_uaf_android_demo.controller.model.Error;
import org.lyreg.fido_uaf_android_demo.exception.CommunicationsException;
import org.lyreg.fido_uaf_android_demo.utils.Preferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/1/19.
 */
public class RelyingPartyServerComms implements IRelyingPartyComms {

    private static final int CONNECTION_TIMEOUT = 200000;
    private static final int READ_TIMEOUT       = 200000;
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE        =  "application/json";
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";


    private Context context;


    public RelyingPartyServerComms(Context context) {
        this.context = context;
    }

    /***
     * PROTECTED OPERATION - the server will only process this if a valid session is in place
     * <p/>
     * Create a FIDO registration request, allowing a user to assocaited a FIDO authenticator with
     * the account.
     *
     * @return CreateRegRequestResponse
     */
    @Override
    public CreateRegRequestResponse CreateRegRequest() {
        return null;
    }

    /***
     * In order to authenticate with FIDO, an authentication request must be created.
     * This call does that.
     *
     * @return CreateAuthRequestResponse
     */
    @Override
    public CreateAuthRequestResponse CreateAuthRequest() {
        return null;
    }


    private String getAbsoluteUrl(String relativeUrl) {
        if(relativeUrl.startsWith("/")) {
            relativeUrl = relativeUrl.substring(1, relativeUrl.length());
        }
        return getBaseUrl() + relativeUrl;
    }

    private String getBaseUrl() {
        String serverUrl = Preferences.getSettingsParam(context, Preferences.PREF_SERVER_URL, "openidconnect.ebay.com");

        if(serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
        }

        String port = Preferences.getSettingsParam(context, Preferences.PREF_SERVER_PORT, "80");

        String scheme = Preferences.getSharedPreferences(context)
                .getBoolean(Preferences.PREF_SERVER_SECURE, false) ? "https" : "http";

        return scheme + "://" + serverUrl + ":" + port + "/";
    }

    private HttpResponse get(String relativeUrl) {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = this.createHttpConnection(relativeUrl, GET_METHOD, false);

            int httpRespCode = urlConnection.getResponseCode();
            String response;
            if(httpRespCode == HttpURLConnection.HTTP_OK || httpRespCode == HttpURLConnection.HTTP_CREATED) {
                response = this.readStream(urlConnection.getInputStream());
            } else {
                response = this.readStream(urlConnection.getErrorStream());
            }
            return new HttpResponse(httpRespCode, response);

        } catch(MalformedURLException e) {
            Error error = new Error();
            error.setCode(-1);
            error.setMessage("Unable to connect to the server - likely a programming error");
            throw new CommunicationsException(error);
        } catch (IOException e) {
            Error error = new Error();
            error.setCode(-2);
            error.setMessage("Unable to connect to the server.  Is the server running?");
            throw new CommunicationsException(error);
        } catch(GeneralSecurityException e) {
            Error error = new Error();
            error.setCode(-3);
            error.setMessage("Security error initialising HTTPS connection");
            throw new CommunicationsException(error);
        } finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
    }

    private HttpResponse post(String relativeUrl, String payload) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = this.createHttpConnection(relativeUrl, POST_METHOD, true);
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(payload);
            out.close();

            int httpStatusCode = urlConnection.getResponseCode();
            String response;
            if(httpStatusCode == HttpURLConnection.HTTP_OK || httpStatusCode == HttpURLConnection.HTTP_CREATED) {
                response = readStream(urlConnection.getInputStream());
            } else {
                response = readStream(urlConnection.getErrorStream());
            }
            return new HttpResponse(httpStatusCode, response);
        } catch (MalformedURLException e) {
            Error error = new Error();
            error.setCode(-1);
            error.setMessage("Unable to connect to the server - likely a programming error");
            throw new CommunicationsException(error);
        } catch (IOException e) {
            Error error = new Error();
            error.setCode(-2);
            error.setMessage("Unable to connect to the server.  Is the server running?");
            throw new CommunicationsException(error);
        } catch(GeneralSecurityException e) {
            Error error = new Error();
            error.setCode(-3);
            error.setMessage("Security error initialising HTTPS connection");
            throw new CommunicationsException(error);
        } finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
    }

    private HttpURLConnection createHttpConnection(String relativeUrl, String method, boolean output) throws
            IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url = new URL(getAbsoluteUrl(relativeUrl));
        Log.v("createHttpConnection", url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(output);
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE);

        httpURLConnection.connect();
        return httpURLConnection;
    }

    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

        String line;
        while( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
