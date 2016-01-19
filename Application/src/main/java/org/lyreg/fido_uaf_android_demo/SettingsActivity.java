package org.lyreg.fido_uaf_android_demo;

import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_SERVER_URL = "pref_server_url";
    public static final String PREF_SERVER_PORT = "pref_server_port";
    public static final String PREF_SERVER_SECURE = "pref_server_secure";

    /**
     * Display the {@link SettingsFragment}
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home :
                finish();
//                overridePendingTransition(R.anim.push_bottom_out, R.anim.push_bottom_in);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
