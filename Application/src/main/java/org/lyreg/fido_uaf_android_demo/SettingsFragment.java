package org.lyreg.fido_uaf_android_demo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.lyreg.fido_uaf_android_demo.utils.Preferences;

/**
 * Displays application settings based on preferences.xml
 */
public class SettingsFragment extends PreferenceFragment {
    /**
     * Displays application settings based on preferences.xml
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesMode(Preferences.PREFERENCES_MODE);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }


}
