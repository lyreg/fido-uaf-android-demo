package org.lyreg.fido_uaf_android_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lyreg.fido_uaf_android_demo.adapter.AuthenticatorListAdapter;
import org.lyreg.fido_uaf_android_demo.controller.model.GetAuthRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.GetRegRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.PostAuthResponseResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.ServerOperationResult;
import org.lyreg.fido_uaf_android_demo.exception.CommunicationsException;
import org.lyreg.fido_uaf_android_demo.exception.ServerError;
import org.lyreg.fido_uaf_android_demo.uaf.AndroidClientIntentParameters;
import org.lyreg.fido_uaf_android_demo.uaf.FidoOperation;
import org.lyreg.fido_uaf_android_demo.uaf.UafClientLogUtils;
import org.lyreg.fido_uaf_android_demo.uaf.UafServerResponseCodes;
import org.lyreg.fido_uaf_android_demo.utils.LogUtils;
import org.lyreg.fido_uaf_android_demo.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends BaseActivity {

    // UI Components
    private View mIntroView;
    private ProgressBar mIntroProgressBar;
    private Button mDiscoveryButton;
    private Button mFidoRegiterButton;
    private Button mFidoLoginButton;

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
        mFidoLoginButton = (Button) findViewById(R.id.login_fido_button);

        mDiscoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("OnClick", "discovery");
                attemptFindClientsAndAuthenticators();
            }
        });

        mFidoRegiterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

        mFidoLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoginWithFido();
            }
        });

        mIntroView.setVisibility(View.VISIBLE);

        mDiscoveryButton.setEnabled(true);
        mFidoRegiterButton.setEnabled(false);
        mFidoLoginButton.setEnabled(false);
        Log.v("onCreate", Preferences.getSettingsParam(this, Preferences.PREF_SERVER_URL, ""));
    }

    private void attemptFindClientsAndAuthenticators() {
        showProgress(true);
        FindClientsAndAuthenticators findClientsAndAuthenticators = new FindClientsAndAuthenticators();
        findClientsAndAuthenticators.execute();
    }

    private void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void attemptLoginWithFido() {
        showProgress(true);
        GetAuthRequestTask mGetAuthRequestTask = new GetAuthRequestTask();
        mGetAuthRequestTask.execute();
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
            Log.v("retrieveAvailableAuthenticator", clientsDiscoverAttempted + "");
//            mFidoRegiterButton.setVisibility(View.VISIBLE);

            showAvailableAuthenticatorAaidList();
        }
    }

    /***
     * Show the discovered authenticators list in a PopupWindow.
     */
    private void showAvailableAuthenticatorAaidList() {
        showProgress(false);
        List<String> list = new ArrayList<String>(getAvailableAuthenticatorAaids());
        for(int i = 0; i < list.size(); i++) {
            Log.v("AuthAaidList", list.get(i) + " test");
        }
        new AuthenticatorPopupWindow(this).show(this);
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

        if(getCurrentFidoOperation() == FidoOperation.Discover) {
            updateAvailableAuthenticatorAaidList(getAaidsFromDiscoveryData(uafResponseJson));
            retrieveAvailableAuthenticatorAaids();
        } else if(getCurrentFidoOperation() == FidoOperation.Authentication) {
            // Continue FIDO authentication (log-in with FIDO)
            LoginWithFidoTask mLoginWithFidoTask = new LoginWithFidoTask(uafResponseJson);
            mLoginWithFidoTask.execute();
        }

    }

    @Override
    protected void onActivityResultFailure(String errorMsg) {

        if(getCurrentFidoOperation() == FidoOperation.Discover) {
            updateAvailableAuthenticatorAaidList(new ArrayList<String>());
            retrieveAvailableAuthenticatorAaids();
        } else if(getCurrentFidoOperation() == FidoOperation.Authentication) {
            showProgress(false);
            Toast.makeText(this, "Could not process the authentication request.  Error from FIDO Client: " + errorMsg, Toast.LENGTH_LONG).show();
        }

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

        @Override
        protected Object doInBackground(Object[] params) {

            clientsDiscoverAttempted = false;
            loadClientsList();
            retrieveAvailableAuthenticatorAaids();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
            if(clientsDiscoverAttempted) {
                showProgress(false);
            }
        }
    }

    /***
     * Class to handle the creation of the FIDO authentication request
     */
    public class GetAuthRequestTask extends AsyncTask<Void, Void, ServerOperationResult<GetAuthRequestResponse>> {

        public GetAuthRequestTask() {}

        @Override
        protected ServerOperationResult<GetAuthRequestResponse> doInBackground(Void... params) {
            ServerOperationResult<GetAuthRequestResponse> result = null;

            try {
                GetAuthRequestResponse response = getRelyingPartyComms().GetAuthRequest();
                result = new ServerOperationResult<>(response);
            } catch (ServerError e) {
                result = new ServerOperationResult<>(e.getError());
            } catch (CommunicationsException e) {
                result = new ServerOperationResult<>(e.getError());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ServerOperationResult<GetAuthRequestResponse> result) {
            if(result.isSuccessful()) {
                GetAuthRequestResponse response = result.getResponse();

                // Send authentication request to the UAF client
                setCurrentUafOperation(FidoOperation.Authentication);
                Intent intent = getUafClientUtils().getUafOperationIntent(FidoOperation.Authentication,
                        response.getFidoAuthenticationRequest());
                sendUafClientIntent(intent, FidoOpCommsType.Return);
            } else {
                endProgerssWithMsg(result.getError().getMessage());
            }
        }
    }

    /***
     * Class to handle the actual authentication of the user with FIDO.  The
     * response from the FIDO client is sent to the server where the authentication is
     * performed.
     */
    public class LoginWithFidoTask extends AsyncTask<Void, Void, ServerOperationResult<PostAuthResponseResponse>> {

        private String uafResponseJson;
        public LoginWithFidoTask(String response) { this.uafResponseJson = response; }

        @Override
        protected ServerOperationResult<PostAuthResponseResponse> doInBackground(Void... params) {
            ServerOperationResult<PostAuthResponseResponse> result = null;
            try {
                PostAuthResponseResponse response = getRelyingPartyComms().PostAuthResponse(this.uafResponseJson);
                result = new ServerOperationResult<>(response);
            } catch (ServerError e) {
                result = new ServerOperationResult<>(e.getError());
            } catch (CommunicationsException e) {
                result = new ServerOperationResult<>(e.getError());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ServerOperationResult<PostAuthResponseResponse> result) {
            if(result.isSuccessful()) {
                // SERVER RESPONDED OK
                PostAuthResponseResponse response = result.getResponse();

                Log.e("PostRegResponseTask", response.getFidoAuthenticationResponse());
                Intent intent = getUafClientUtils().
                        getUafOperationCompletionStatusIntent(response.getFidoAuthenticationResponse(), 1200, "success");
                sendFidoOperationCompletionIntent(intent);

                showProgress(false);
            } else {
                // SERVER ERROR
                // Now we need to send the registration response and server error back to the UAF client.
                Intent intent = getUafClientUtils().getUafOperationCompletionStatusIntent(
                        uafResponseJson, UafServerResponseCodes.INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
                sendFidoOperationCompletionIntent(intent);

                endProgerssWithMsg(result.getError().getMessage());
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

        if(clientsDiscoverAttempted == true &&
                getUafClientList().size() > 0 && getAvailableAuthenticatorAaids().size() > 0 ) {
            mFidoRegiterButton.setEnabled(true);
            mFidoLoginButton.setEnabled(true);
        } else {
            mFidoRegiterButton.setEnabled(false);
            mFidoLoginButton.setEnabled(false);
        }
    }

    private void endProgerssWithMsg(String errorMsg) {
        showProgress(false);
        displayError(errorMsg);
        mIntroView.requestFocus();
    }

    public class AuthenticatorPopupWindow extends BottomPushPopupWindow {

        public AuthenticatorPopupWindow(Activity context) {
            super(context);
//            this.list = list;
        }

        @Override
        protected View generateCustomView() {
            View root = View.inflate(context, R.layout.window_popup, null);
//            if(this.list == null) {
//                Log.v("onResume", "getView");
//            }
            AuthenticatorListAdapter authAdapter = new AuthenticatorListAdapter((Activity) context, new ArrayList<String>(getAvailableAuthenticatorAaids()));

            ListView listView = (ListView) root.findViewById(R.id.authenticatorListView);
            listView.setAdapter(authAdapter);
            return root;
        }
    }
}
