package org.lyreg.fido_uaf_android_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lyreg.fido_uaf_android_demo.uaf.AndroidClientIntentParameters;
import org.lyreg.fido_uaf_android_demo.uaf.FidoOperation;
import org.lyreg.fido_uaf_android_demo.uaf.UafClientLogUtils;
import org.lyreg.fido_uaf_android_demo.uaf.UafClientUtils;
import org.lyreg.fido_uaf_android_demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends BaseActivity {

    // UI Components
    private View mIntroView;
    private ProgressBar mIntroProgressBar;
    private Button mDiscoveryButton;
    private Button mFidoRegiterButton;

    // Used during the process of dicover all the FIDO clients on the device
    private int uafClientIdx = 0;
    private boolean clientsDiscoverAttempted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIntroView = findViewById(R.id.intro_form);
        mIntroProgressBar = (ProgressBar) findViewById(R.id.intro_progress);
        mDiscoveryButton = (Button) findViewById(R.id.discovery_button);
        mFidoRegiterButton = (Button) findViewById(R.id.register_fido_button);

        mDiscoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true);
                FindClientsAndAuthenticators findClientsAndAuthenticators = new FindClientsAndAuthenticators();
                findClientsAndAuthenticators.execute();
            }
        });

        mIntroView.setVisibility(View.VISIBLE);

        mDiscoveryButton.setEnabled(true);
        mFidoRegiterButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("onResume", clientsDiscoverAttempted + "");
        if(clientsDiscoverAttempted == true) {
            showProgress(false);
//            mFidoRegiterButton.setVisibility(View.VISIBLE);
        }
    }

    /***
     * Attempt to get the list of UAF Clients on the device and
     * add these to the static list
     */
    private void loadClientsList() {
        List<ResolveInfo> uafClientList;
        Intent intent = new Intent();
        intent.setAction(AndroidClientIntentParameters.intentAction);
        intent.setType(AndroidClientIntentParameters.intentType);

        PackageManager packageManager = this.getPackageManager();
        uafClientList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        getUafClientList().addAll(uafClientList);
    }

    /***
     * Retrieve the set of authenticators from the FIDO clients by using the FIDO discovery operation
     *
     * The list of authenticators is cached within this class so if authenticators can be
     * dynamically added or removed from the device, they will not be picked up after the
     * app is initialized.
     *
     */
    private void retrieveAvailableAuthenticatorAaids() {

        if(!clientsDiscoverAttempted) {
            List<ResolveInfo> clientList = this.getUafClientList();
            this.setCurrentUafOperation(FidoOperation.Discover);
            Intent intent = getUafClientUtils().getDiscoverIntent();
            if(clientList != null && clientList.size() > 0) {
                intent.setComponent(new ComponentName(clientList.get(uafClientIdx).activityInfo.packageName,
                        clientList.get(uafClientIdx).activityInfo.name));
                UafClientLogUtils.logUafDiscoverRequest(intent);
                UafClientLogUtils.logUafClientDetails(clientList.get(uafClientIdx));
                startActivityForResult(intent, AndroidClientIntentParameters.requestCode);
                return;
            } else {
                // End now if there are no clients
                LogUtils.logDebug(LogUtils.TAG, "No FIDO UAF Client was found.");
                clientsDiscoverAttempted = true;
            }
        } else {

        }
    }

    /***
     * Add the discovered authenticators to the set of authenticators.
     * @param retrievedAaids the list of AAIDs
     */
    protected void updateAvailableAuthenticatorAaidList(List<String> retrievedAaids) {
        this.getAvailableAuthenticatorAaids().addAll(retrievedAaids);
        LogUtils.logAaidRetrievalUpdate(uafClientIdx, retrievedAaids);
        uafClientIdx++;
        if(uafClientIdx < getUafClientList().size()) {
            LogUtils.logAaidRetrievalContinue(uafClientIdx);
        } else {
            LogUtils.logAaidRetrievalEnd();
            clientsDiscoverAttempted = true;
        }
    }

    /***
     * Callback from the FIDO Client with the response from the discovery request
     *
     * @param uafResponseJson the response
     */
    @Override
    protected void processUafClientResponse(String uafResponseJson) {

        updateAvailableAuthenticatorAaidList(getAaidsFromDiscoveryData(uafResponseJson));
        retrieveAvailableAuthenticatorAaids();
    }

    @Override
    protected void onActivityResultFailure(String errorMsg) {

        updateAvailableAuthenticatorAaidList(new ArrayList<String>());
        retrieveAvailableAuthenticatorAaids();
    }

    /***
     * From the discovery data, create the list of AAIDs
     *
     * @param discoveryData the discovery data
     * @return a list of AAIDs
     */
    protected List<String> getAaidsFromDiscoveryData(String discoveryData) {
        List<String> aaidList = new ArrayList<>();
        if(discoveryData != null) {
            try {
                JSONObject discoveryDataJsonObj = new JSONObject(new JSONTokener(discoveryData));

                JSONArray availableAuthenticators = discoveryDataJsonObj.getJSONArray("availableAuthenticators");
                for(int i = 0; i < availableAuthenticators.length(); i++) {
                    JSONObject authenticator = availableAuthenticators.getJSONObject(i);
                    aaidList.add(authenticator.getString("aaid"));
                }
                return aaidList;
            } catch (JSONException e) {
                Log.e(LogUtils.TAG, "Invalid discovery data format returned by the client.");
                e.printStackTrace();
            }
        }
        return aaidList;
    }

    /**
     * Represents an asynchronous task used to find all clients and available authentivators
     */
    public class FindClientsAndAuthenticators extends AsyncTask {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Object doInBackground(Object[] params) {

            loadClientsList();
            retrieveAvailableAuthenticatorAaids();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
            if(clientsDiscoverAttempted == true) {
                Log.v("onPostExecute", clientsDiscoverAttempted + "");
                showProgress(false);
//                mFidoRegiterButton.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
            mIntroView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mIntroProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        if(clientsDiscoverAttempted == true) {
            mFidoRegiterButton.setEnabled(true);
        } else {
            mFidoRegiterButton.setEnabled(false);
        }
    }
}
