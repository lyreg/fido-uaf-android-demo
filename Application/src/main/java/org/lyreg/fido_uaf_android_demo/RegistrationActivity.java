package org.lyreg.fido_uaf_android_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.lyreg.fido_uaf_android_demo.controller.model.GetRegRequestResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.PostRegResponseResponse;
import org.lyreg.fido_uaf_android_demo.controller.model.ServerOperationResult;
import org.lyreg.fido_uaf_android_demo.exception.CommunicationsException;
import org.lyreg.fido_uaf_android_demo.exception.ServerError;
import org.lyreg.fido_uaf_android_demo.http.HttpResponse;
import org.lyreg.fido_uaf_android_demo.uaf.FidoOperation;
import org.lyreg.fido_uaf_android_demo.uaf.UafServerResponseCodes;

public class RegistrationActivity extends BaseActivity {

    private View     mRegView;
    private ProgressBar mRegProgressBar;
    private EditText mUserNameEditText;
    private Button   mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegView = findViewById(R.id.register_form);
        mRegProgressBar = (ProgressBar) findViewById(R.id.reg_progress);
        mUserNameEditText = (EditText) findViewById(R.id.register_username);
        mRegisterButton = (Button) findViewById(R.id.register_fido_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempRegistration();
            }
        });
    }

    /**
     * Attempts to register the account.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual Register attempt is made.
     */
    private void attempRegistration() {

        // Reset errors.
        mUserNameEditText.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserNameEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(user)) {
            mUserNameEditText.setError("The username is required");
            focusView = mUserNameEditText;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mUserNameEditText.getWindowToken(), 0);
            GetRegRequestTask mGetRegRequestTask = new GetRegRequestTask(user);
            mGetRegRequestTask.execute();
        }
    }


    protected void onActivityResultFailure(String errorMsg) {
        endProgerssWithMsg(errorMsg);
    }

    protected void processUafClientResponse(String uafResponseJson) {
        Log.v("processUafClientResponse", uafResponseJson);
        PostRegResponseTask mPostRegResponseTask = new PostRegResponseTask(uafResponseJson);
        mPostRegResponseTask.execute();
    }

    public class GetRegRequestTask extends AsyncTask<Void, Void, ServerOperationResult<GetRegRequestResponse>> {

        private String username;
        public GetRegRequestTask(String username) {
            this.username = username;
        }

        @Override
        protected ServerOperationResult<GetRegRequestResponse> doInBackground(Void... params) {

            ServerOperationResult<GetRegRequestResponse> result = null;

            try {

                GetRegRequestResponse response = getRelyingPartyComms().GetRegRequest(username);
                Log.e("GetRegRequestTask", "doInBackground");
                result = new ServerOperationResult<GetRegRequestResponse>(response);
            } catch (ServerError e) {
                result = new ServerOperationResult<GetRegRequestResponse>(e.getError());
            } catch (CommunicationsException e) {
                result = new ServerOperationResult<GetRegRequestResponse>(e.getError());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ServerOperationResult<GetRegRequestResponse> result) {

            if(result.isSuccessful()) {
                GetRegRequestResponse response = result.getResponse();

                // Send authentication request to the UAF client
                setCurrentUafOperation(FidoOperation.Registration);
                Intent regIntent = getUafClientUtils()
                        .getUafOperationIntent(FidoOperation.Registration, response.getFidoRegistrationRequest());
                sendUafClientIntent(regIntent, FidoOpCommsType.Return);
            } else {
                endProgerssWithMsg(result.getError().getMessage());
            }
        }
    }

    public class PostRegResponseTask extends AsyncTask<Void, Void, ServerOperationResult<PostRegResponseResponse>> {

        private String payload;
        public PostRegResponseTask(String payload) {
            this.payload = payload;
        }

        protected ServerOperationResult<PostRegResponseResponse> doInBackground(Void... params) {
            ServerOperationResult<PostRegResponseResponse> result = null;

            try {
                PostRegResponseResponse response = getRelyingPartyComms().PostRegResponse(this.payload);
                result = new ServerOperationResult<PostRegResponseResponse>(response);
            } catch (ServerError e) {
                result = new ServerOperationResult<PostRegResponseResponse>(e.getError());
            } catch (CommunicationsException e) {
                result = new ServerOperationResult<PostRegResponseResponse>(e.getError());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ServerOperationResult<PostRegResponseResponse> result) {
            if(result.isSuccessful()) {
                // SERVER RESPONDED OK
                // Server responded OK but this doesn't necessarily mean that an authenticator was created successfully.
                // The response contains a code which indicates success or failure. This response code is sent on to the UAF
                // client so that it might delete the credential it generated on server failure. The response code
                // is also checked by the RP app. If the return code indicates that no error was returned by the server then
                // the log-in success screen is displayed.
                PostRegResponseResponse response = result.getResponse();
                Log.e("PostRegResponseTask", response.getFidoRegistrationResponse());
                Intent intent = getUafClientUtils().
                        getUafOperationCompletionStatusIntent(response.getFidoRegistrationResponse(), 1200, "success");
                sendFidoOperationCompletionIntent(intent);

                showProgress(false);

            } else {
                // SERVER ERROR
                // Now we need to send the registration response and server error back to the UAF client.
                Intent intent = getUafClientUtils().getUafOperationCompletionStatusIntent(
                        payload, UafServerResponseCodes.INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
                sendFidoOperationCompletionIntent(intent);

                endProgerssWithMsg(result.getError().getMessage());
            }
        }
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void endProgerssWithMsg(String errorMsg) {
        showProgress(false);
        displayError(errorMsg);
        mUserNameEditText.requestFocus();
    }
}
