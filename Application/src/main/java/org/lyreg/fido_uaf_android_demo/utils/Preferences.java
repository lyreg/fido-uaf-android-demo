package org.lyreg.fido_uaf_android_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/1/19.
 */
public class Preferences {

    public static final String PREFERENCES_NAME = "Preferences";
    public static final int    PREFERENCES_MODE = Context.MODE_PRIVATE;

    public static final String PREF_SERVER_URL = "pref_server_url";
    public static final String PREF_SERVER_PORT = "pref_server_port";
    public static final String PREF_SERVER_SECURE = "pref_server_secure";

    public static final String GET_AUTH_REQUEST = "/fidouaf/v1/public/authRequest";
    public static final String POST_AUTH_RESPONSE = "/fidouaf/v1/public/authResponse";
    public static final String POST_DEREG_RESPONSE = "/fidouaf/v1/public/deregRequest";
    public static final String GET_REG_REQUEST = "/fidouaf/v1/public/regRequest/";
    public static final String POST_REG_RESPONSE = "/fidouaf/v1/public/regResponse";

    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE);
        return preferences;
    }

    public static String getSettingsParam(Context context, String paramKey, String defaultValue) {
        SharedPreferences settings = getSharedPreferences(context);
        return settings.getString(paramKey, defaultValue);
    }

    public static void setSettingsParam(Context context, String paramKey, String paramValue) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(paramKey, paramValue);
        editor.commit();
    }


}
